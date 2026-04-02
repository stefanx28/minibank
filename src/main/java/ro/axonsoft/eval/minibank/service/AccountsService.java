package ro.axonsoft.eval.minibank.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.exception.AccountNotFoundException;
import ro.axonsoft.eval.minibank.dto.request.AccountCreateRequest;
import ro.axonsoft.eval.minibank.dto.response.AccountResponse;
import ro.axonsoft.eval.minibank.model.Accounts;
import ro.axonsoft.eval.minibank.repository.AccountsRepository;
import ro.axonsoft.eval.minibank.repository.TransactionsRepository;

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


        Accounts account = new Accounts();
        account.setOwnerName(request.getOwnerName());
        account.setIban(request.getIban());
        account.setCurrency(request.getCurrency());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);

        Accounts saved = accountsRepository.save(account);

        return toResponse(saved);
    }

    public AccountResponse getAccount(Long id){
        Accounts account = accountsRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException("Account not found: " + id)
        );
        return toResponse(account);
    }

    public Map<String, Object> getAllAccounts(int page, int size) {
        int offset = page * size;
        List<Accounts> all = accountsRepository.findAll();
        int totalElements = all.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<AccountResponse> content = all.stream()
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
