package ro.axonsoft.eval.minibank.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ro.axonsoft.eval.minibank.domain.enums.AccountType;
import ro.axonsoft.eval.minibank.domain.enums.Currency;

@Component
@ConfigurationProperties(prefix = "bank")
@Getter
@Setter
public class BankProperties {
    private String iban;
    private String ownerName;
    private Currency currency;
    private AccountType accountType;
}