package ro.axonsoft.eval.minibank.bl.mapper;

import ro.axonsoft.eval.minibank.bl.dto.request.AccountCreateRequest;
import ro.axonsoft.eval.minibank.bl.dto.response.AccountResponse;
import ro.axonsoft.eval.minibank.domain.model.Accounts;

import java.math.BigDecimal;

public final class AccountMapper {


    private AccountMapper() {}

    public static Accounts toEntity(AccountCreateRequest request) {
        Accounts account = new Accounts();
        account.setOwnerName(request.getOwnerName());
        account.setIban(request.getIban());
        account.setCurrency(request.getCurrency());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO); // default balance
        return account;
    }


}