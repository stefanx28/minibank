package ro.axonsoft.eval.minibank.presentation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ro.axonsoft.eval.minibank.bl.dto.request.AccountCreateRequest;
import ro.axonsoft.eval.minibank.bl.dto.response.AccountResponse;
import ro.axonsoft.eval.minibank.bl.service.AccountsService;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountsController {
    private final AccountsService accountsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestBody AccountCreateRequest request){
        return accountsService.createAccount(request);
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id) {
        return accountsService.getAccount(id);
    }

    @GetMapping
    public Map<String, Object> getAllAccounts(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
        return accountsService.getAllAccounts(pageNumber, pageSize);
    }

    @GetMapping("/{accountID}/transactions")
    public Map<String, Object> getTransactions(@PathVariable Long accountID, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        return accountsService.getTransactions(accountID, pageNumber, pageSize);
    }
}


