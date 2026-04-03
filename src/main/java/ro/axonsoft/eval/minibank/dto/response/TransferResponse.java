package ro.axonsoft.eval.minibank.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import ro.axonsoft.eval.minibank.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@JsonPropertyOrder({"id", "sourceIban", "targetIban", "amount", "currency", "targetCurrency", "exchangeRate", "convertedAmount", "idempotencyKey", "createdAt"})
public class TransferResponse {
    private Long id;
    private String sourceIban;
    private String targetIban;
    private BigDecimal amount;
    private Currency currency;
    private Currency targetCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal convertedAmount;
    private String idempotencyKey;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;
}