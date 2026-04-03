package ro.axonsoft.eval.minibank.bl.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.bl.dto.response.TransactionResponse;
import ro.axonsoft.eval.minibank.bl.mapper.AccountMapper;
import ro.axonsoft.eval.minibank.presentation.exception.AccountNotFoundException;
import ro.axonsoft.eval.minibank.bl.dto.request.AccountCreateRequest;
import ro.axonsoft.eval.minibank.bl.dto.response.AccountResponse;
import ro.axonsoft.eval.minibank.presentation.exception.DuplicateIbanException;
import ro.axonsoft.eval.minibank.presentation.exception.InvalidIbanException;
import ro.axonsoft.eval.minibank.domain.model.Accounts;
import ro.axonsoft.eval.minibank.domain.model.Transactions;
import ro.axonsoft.eval.minibank.dal.repository.AccountsRepository;
import ro.axonsoft.eval.minibank.dal.repository.TransactionsRepository;
import ro.axonsoft.eval.minibank.util.IbanValidator;
import ro.axonsoft.eval.minibank.util.PaginationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AccountsService {

    private final AccountsRepository accountsRepository;
    private final TransactionsRepository transactionsRepository;

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request){

        boolean isIbanValid = IbanValidator.isValid(request.getIban());
        if(!isIbanValid){
            throw new InvalidIbanException("Iban invalid");
        }
        boolean isIbanUsed = accountsRepository.existsByIban(request.getIban());
        if (isIbanUsed) {
            throw new DuplicateIbanException("IBAN already in use: " + request.getIban());
        }

        Accounts account = AccountMapper.toEntity(request);
        Accounts saved = accountsRepository.save(account);

        return toResponse(saved);
    }

    public AccountResponse getAccount(Long accountId){
        Accounts account = accountsRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found: " + accountId)
        );
        return toResponse(account);
    }

    public Map<String, Object> getAllAccounts(int pageNumber, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
        Page<Accounts> accountsPage = accountsRepository.findAll(pageable);

        return PaginationUtil.toPaginatedResponse(accountsPage, this::toResponse);
    }

    public Map<String, Object> getTransactions(Long accountId, int pageNumber, int pageSize) {
        if (!accountsRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account not found: " + accountId);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").ascending());
        Page<Transactions> transactionsPage = transactionsRepository.findByAccountIdOrderByTimestampAsc(accountId, pageable);
        return PaginationUtil.toPaginatedResponse(transactionsPage, this::toTransactionResponse);
    }

    private TransactionResponse toTransactionResponse(Transactions transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTimestamp(transaction.getTimestamp());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount().setScale(2, RoundingMode.HALF_EVEN));
        response.setCurrency(transaction.getCurrency());
        response.setBalanceAfter(transaction.getBalanceAfter().setScale(2, RoundingMode.HALF_EVEN));
        response.setCounterpartyIban(transaction.getCounterpartyIban());
        response.setTransferId(transaction.getTransfer().getId());
        return response;
    }

    private AccountResponse toResponse(Accounts account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setOwnerName(account.getOwnerName());
        response.setIban(account.getIban());
        response.setCurrency(account.getCurrency());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance().setScale(2, RoundingMode.HALF_EVEN));
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }
}
