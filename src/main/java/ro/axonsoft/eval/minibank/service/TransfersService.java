package ro.axonsoft.eval.minibank.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.dto.request.TransferCreateRequest;
import ro.axonsoft.eval.minibank.dto.response.TransferResponse;
import ro.axonsoft.eval.minibank.exception.AccountNotFoundException;
import ro.axonsoft.eval.minibank.exception.DailyLimitExceededException;
import ro.axonsoft.eval.minibank.exception.InsufficientFundsException;
import ro.axonsoft.eval.minibank.exception.TransferNotFoundException;
import ro.axonsoft.eval.minibank.model.*;
import ro.axonsoft.eval.minibank.repository.AccountsRepository;
import ro.axonsoft.eval.minibank.repository.TransactionsRepository;
import ro.axonsoft.eval.minibank.repository.TransfersRepository;

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
    3. idempotency
    4.funds
    5.solve concurrency
    6.exchange rates and currency
    7.savings account
     */
    @Transactional
    public TransferResponse createTransfer(TransferCreateRequest request){

        //if key present, find if exists and return, else create
        if(request.getIdempotencyKey() != null){
            Optional<Transfers> existing = transfersRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return toResponse(existing.get());
            }
        }

        //check iban exists
        Accounts src = accountsRepository.findByIban(request.getSourceIban())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
        Accounts target = accountsRepository.findByIban(request.getTargetIban())
                .orElseThrow(() -> new AccountNotFoundException("Target account not found"));

        //check for SEPA TODO

        //check balance
        if(!src.getId().equals(BANK_ACCOUNT_ID)){
            if(src.getBalance().compareTo(request.getAmount()) < 0){
                throw new InsufficientFundsException("Insufficient funds on account:" + src.getId());
            }
        }

        //check savings limit
        if(src.getAccountType() == AccountType.SAVINGS && src.getId() != BANK_ACCOUNT_ID){
            checkSavingsLimit(src, request.getAmount());
        }

        //exchange
        Currency srcCurrency = src.getCurrency();
        Currency targetCurrency = target.getCurrency();
        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal convertedAmount = null;
        BigDecimal exchangeRate = null;

        if(srcCurrency != targetCurrency){
            exchangeRate = exchangeRatesService.getRate(srcCurrency)
                    .divide(exchangeRatesService.getRate(targetCurrency), 6, RoundingMode.HALF_EVEN);
            convertedAmount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_EVEN);
        }

        BigDecimal targetAmount = convertedAmount != null ? convertedAmount : amount;

        //modify balance
        if(src.getId() != BANK_ACCOUNT_ID){
            src.setBalance(src.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_EVEN));
            accountsRepository.save(src);
        }

        target.setBalance(target.getBalance().add(targetAmount).setScale(2, RoundingMode.HALF_EVEN));

        //finally save transfer
        Transfers transfer = new Transfers();
        transfer.setSourceIban(request.getSourceIban());
        transfer.setTargetIban(request.getTargetIban());
        transfer.setAmount(amount);
        transfer.setCurrency(srcCurrency);
        transfer.setTargetCurrency(targetCurrency);
        transfer.setExchangeRate(exchangeRate);
        transfer.setConvertedAmount(convertedAmount);
        transfer.setIdempotencyKey(request.getIdempotencyKey());

        Transfers saved = transfersRepository.save(transfer);

        //create transaction record TODO!!

        return toResponse(saved);
    }



    public TransferResponse getTransfer(Long id) {
        Transfers transfer = transfersRepository.findById(id)
                .orElseThrow(() -> new TransferNotFoundException("Transfer not found: " + id));
        return toResponse(transfer);
    }

    public Map<String, Object> getAllTransfers(String iban, Instant fromDate, Instant toDate, int page, int size) {
        List<Transfers> all;

//        if (iban != null) {
//            all = transfersRepository.findBySourceIbanOrTargetIban(iban, iban);
//        } else {
//
//        }
        all = transfersRepository.findAll();

        // apply date filters
        if (fromDate != null) {
            all = all.stream().filter(t -> !t.getCreatedAt().isBefore(fromDate)).toList();
        }
        if (toDate != null) {
            all = all.stream().filter(t -> !t.getCreatedAt().isAfter(toDate)).toList();
        }

        int totalElements = all.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int offset = page * size;

        List<TransferResponse> content = all.stream()
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
