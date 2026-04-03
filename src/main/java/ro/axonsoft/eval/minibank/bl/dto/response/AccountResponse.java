package ro.axonsoft.eval.minibank.bl.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import ro.axonsoft.eval.minibank.domain.enums.AccountType;
import ro.axonsoft.eval.minibank.domain.enums.Currency;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@JsonPropertyOrder({"id", "ownerName", "iban", "currency", "accountType", "balance", "createdAt"})
public class AccountResponse {
    private Long id;
    private String ownerName;
    private String iban;
    private Currency currency;
    private AccountType accountType;
    private BigDecimal balance;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;
}
