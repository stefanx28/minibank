package ro.axonsoft.eval.minibank.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import ro.axonsoft.eval.minibank.model.Currency;
import ro.axonsoft.eval.minibank.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@JsonPropertyOrder({"id", "timestamp", "type", "amount", "currency", "balanceAfter", "counterpartyIban", "transferId"})
public class TransactionResponse {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant timestamp;

    private TransactionType type;
    private BigDecimal amount;
    private Currency currency;
    private BigDecimal balanceAfter;
    private String counterpartyIban;
    private Long transferId;
}
