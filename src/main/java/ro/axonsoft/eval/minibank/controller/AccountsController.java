package ro.axonsoft.eval.minibank.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ro.axonsoft.eval.minibank.dto.request.AccountCreateRequest;
import ro.axonsoft.eval.minibank.dto.response.AccountResponse;
import ro.axonsoft.eval.minibank.service.AccountsService;

import java.awt.print.Pageable;
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
    public Map<String, Object> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return accountsService.getAllAccounts(page, size);
    }
}


