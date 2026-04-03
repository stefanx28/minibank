package ro.axonsoft.eval.minibank.bl.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferCreateRequest {
    private String sourceIban;
    private String targetIban;
    private BigDecimal amount;
    private String idempotencyKey;
}
