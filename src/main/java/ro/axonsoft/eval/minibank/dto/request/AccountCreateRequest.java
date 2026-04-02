package ro.axonsoft.eval.minibank.dto.request;

import lombok.Data;
import ro.axonsoft.eval.minibank.model.AccountType;
import ro.axonsoft.eval.minibank.model.Currency;

@Data
public class AccountCreateRequest {
    private String ownerName;
    private String iban;
    private Currency currency;
    private AccountType accountType;
}
