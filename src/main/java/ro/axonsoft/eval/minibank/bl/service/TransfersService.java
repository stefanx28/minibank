package ro.axonsoft.eval.minibank.bl.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.bl.dto.request.TransferCreateRequest;
import ro.axonsoft.eval.minibank.bl.dto.response.TransferResponse;
import ro.axonsoft.eval.minibank.bl.mapper.TransactionMapper;
import ro.axonsoft.eval.minibank.bl.mapper.TransferMapper;
import ro.axonsoft.eval.minibank.domain.enums.AccountType;
import ro.axonsoft.eval.minibank.domain.enums.Currency;
import ro.axonsoft.eval.minibank.domain.enums.TransactionType;
import ro.axonsoft.eval.minibank.presentation.exception.*;
import ro.axonsoft.eval.minibank.domain.model.*;
import ro.axonsoft.eval.minibank.dal.repository.AccountsRepository;
import ro.axonsoft.eval.minibank.dal.repository.TransactionsRepository;
import ro.axonsoft.eval.minibank.dal.repository.TransfersRepository;
import ro.axonsoft.eval.minibank.util.IbanValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransfersService {

    private static final Long BANK_ACCOUNT_ID = 1L;
    private static final BigDecimal SAVINGS_LIMIT = new BigDecimal("5000.00");

    private final AccountsRepository accountsRepository;
    private final TransfersRepository transfersRepository;
    private final ExchangeRatesService exchangeRatesService;
    private final TransactionsRepository transactionsRepository;


    /*
    before creating check:
    1.IBAN
    2.SEPA
    3.idempotency
    4.funds
    5.solve concurrency
    6.exchange rates and currency
    7.savings account
     */


    @Transactional
    public TransferResponse createTransfer(TransferCreateRequest request){

        //check iban exists
        Accounts sourceAccount = accountsRepository.findByIbanForUpdate(request.getSourceIban())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
        Accounts targetAccount = accountsRepository.findByIbanForUpdate(request.getTargetIban())
                .orElseThrow(() -> new AccountNotFoundException("Target account not found"));

        //if key present, find if exists and return, else create
        if(request.getIdempotencyKey() != null){
            Optional<Transfers> existing = transfersRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return toResponse(existing.get());
            }
        }

        validateTransfer(request, sourceAccount, targetAccount);

        //exchange
        Currency srcCurrency = sourceAccount.getCurrency();
        Currency targetCurrency = targetAccount.getCurrency();
        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal convertedAmount = null;
        BigDecimal exchangeRate = null;


        if(srcCurrency != targetCurrency){
            exchangeRate = exchangeRatesService.getRate(srcCurrency)
                    .divide(exchangeRatesService.getRate(targetCurrency), 6, RoundingMode.HALF_EVEN);
            convertedAmount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_EVEN);
        }

        BigDecimal targetAmount = convertedAmount != null ? convertedAmount : amount;

        boolean isCentralBankAccount = sourceAccount.getId().equals(BANK_ACCOUNT_ID);
        //modify balance
        if(!isCentralBankAccount){
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_EVEN));
            accountsRepository.save(sourceAccount);
        }
        targetAccount.setBalance(targetAccount.getBalance().add(targetAmount).setScale(2, RoundingMode.HALF_EVEN));

        //finally save transfer
        Transfers transfer = TransferMapper.toEntity(request, amount, srcCurrency, targetCurrency, exchangeRate, convertedAmount);
        Transfers saved = transfersRepository.save(transfer);

        createTransaction(sourceAccount, targetAccount, saved, amount, targetAmount);
        return toResponse(saved);
    }


    public TransferResponse getTransfer(Long id) {
        Transfers transfer = transfersRepository.findById(id)
                .orElseThrow(() -> new TransferNotFoundException("Transfer not found: " + id));
        return toResponse(transfer);
    }

    public Map<String, Object> getAllTransfers(String iban, Instant fromDate, Instant toDate, int page, int size) {
        List<Transfers> allTransfers;

        allTransfers = transfersRepository.findAll();

        // apply date filters
        if (fromDate != null) {//filter by fromDate
            allTransfers = allTransfers.stream().filter(t -> !t.getCreatedAt().isBefore(fromDate)).toList();
        }
        if (toDate != null) {//filter by todate boolean
            allTransfers = allTransfers.stream().filter(t -> !t.getCreatedAt().isAfter(toDate)).toList();
        }

        int totalElements = allTransfers.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int offset = page * size;

        List<TransferResponse> content = allTransfers.stream()
                .skip(offset)
                .limit(size)
                .map(this::toResponse)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", content);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("number", page);
        response.put("size", size);
        return response;
    }

    private void validateTransfer(TransferCreateRequest request, Accounts sourceAccount, Accounts targetAccount){

        boolean isAmountValid = request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0;

        if (isAmountValid) {
            throw new InvalidAmountException("Amount must be positive");
        }

        //check for SEPA
        boolean isSEPATransfer = IbanValidator.isSepa(sourceAccount.getIban())||!IbanValidator.isSepa(targetAccount.getIban());

        if(!isSEPATransfer){
            throw new NonSepaException("Transfer must be between SEPA countries");
        }

        //check balance
        boolean isCentralBankAccount = sourceAccount.getId().equals(BANK_ACCOUNT_ID);
        boolean isBalancePositive = sourceAccount.getBalance().compareTo(request.getAmount()) < 0;
        if(isBalancePositive && !isCentralBankAccount){
            throw new InsufficientFundsException("Insufficient funds on account:" + sourceAccount.getId());
        }

        //check savings limit
        if(sourceAccount.getAccountType() == AccountType.SAVINGS){
            checkSavingsLimit(sourceAccount, request.getAmount());
        }
    }


    private void createTransaction(Accounts src, Accounts target, Transfers transfer, BigDecimal amount, BigDecimal targetAmount){

        boolean srcBank = BANK_ACCOUNT_ID.equals(src.getId());
        boolean targetBank = BANK_ACCOUNT_ID.equals(target.getId());

        //switch
        //deposit on bank
        if(srcBank){

            Transactions deposit = TransactionMapper.toEntity(target, transfer, TransactionType.DEPOSIT, targetAmount, null);
            transactionsRepository.save(deposit);
        }
        //withdrawal
        else if (targetBank) {
            Transactions withdrawal = TransactionMapper.toEntity(src, transfer, TransactionType.WITHDRAWAL, amount, null);
            transactionsRepository.save(withdrawal);
        }
        //transfer between two accounts
        else{
            Transactions transferOut = TransactionMapper.toEntity(src, transfer, TransactionType.TRANSFER_OUT, amount, target.getIban());
            transactionsRepository.save(transferOut);
            Transactions transferIn = TransactionMapper.toEntity(target, transfer, TransactionType.TRANSFER_IN, targetAmount, src.getIban());
            transactionsRepository.save(transferIn);
        }

    }


    private TransferResponse toResponse(Transfers transfer) {
        TransferResponse response = new TransferResponse();
        response.setId(transfer.getId());
        response.setSourceIban(transfer.getSourceIban());
        response.setTargetIban(transfer.getTargetIban());
        response.setAmount(transfer.getAmount().setScale(2, RoundingMode.HALF_EVEN));
        response.setCurrency(transfer.getCurrency());
        response.setTargetCurrency(transfer.getTargetCurrency());
        response.setExchangeRate(transfer.getExchangeRate());
        response.setConvertedAmount(transfer.getConvertedAmount() != null
                ? transfer.getConvertedAmount().setScale(2, RoundingMode.HALF_EVEN)
                : null);
        response.setIdempotencyKey(transfer.getIdempotencyKey());
        response.setCreatedAt(transfer.getCreatedAt());
        return response;
    }

    private void checkSavingsLimit(Accounts account, BigDecimal amount){
        ZonedDateTime startOfDay = ZonedDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC);
        Instant startOfDayInstant = startOfDay.toInstant();
        Instant endOfDayInstant = startOfDay.plusDays(1).toInstant();

        List<Transactions> todayTransactions = transactionsRepository
                .findByAccountIdAndTypeInAndTimestampBetween(
                        account.getId(),
                        List.of(TransactionType.TRANSFER_OUT, TransactionType.WITHDRAWAL),
                        startOfDayInstant,
                        endOfDayInstant
                );

        BigDecimal totalEurToday = todayTransactions.stream()
                .map(t -> exchangeRatesService.toEur(t.getAmount(), account.getCurrency()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentEur = exchangeRatesService.toEur(amount, account.getCurrency());
        BigDecimal newTotal = totalEurToday.add(currentEur).setScale(2, RoundingMode.HALF_EVEN);

        if(newTotal.compareTo(SAVINGS_LIMIT) > 0){
            throw new DailyLimitExceededException("Daily limit of 5000 EUR for SAVINGS account exceeded");
        }
    }



}
