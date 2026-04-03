package ro.axonsoft.eval.minibank.bl.dto.request;

import lombok.Data;
import ro.axonsoft.eval.minibank.domain.enums.AccountType;
import ro.axonsoft.eval.minibank.domain.enums.Currency;


@Data
public class AccountCreateRequest {
    private String ownerName;

    private String iban;
    private Currency currency;
    private AccountType accountType;
}
