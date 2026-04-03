package ro.axonsoft.eval.minibank.bl.mapper;

import ro.axonsoft.eval.minibank.bl.dto.request.TransferCreateRequest;
import ro.axonsoft.eval.minibank.domain.enums.Currency;
import ro.axonsoft.eval.minibank.domain.model.Transfers;

import java.math.BigDecimal;

public class TransferMapper {
    public static Transfers toEntity(TransferCreateRequest request, BigDecimal amount, Currency srcCurrency, Currency targetCurrency, BigDecimal exchangeRate, BigDecimal convertedAmount){
        Transfers transfer = new Transfers();
        transfer.setSourceIban(request.getSourceIban());
        transfer.setTargetIban(request.getTargetIban());
        transfer.setAmount(amount);
        transfer.setCurrency(srcCurrency);
        transfer.setTargetCurrency(targetCurrency);
        transfer.setExchangeRate(exchangeRate);
        transfer.setConvertedAmount(convertedAmount);
        transfer.setIdempotencyKey(request.getIdempotencyKey());

        return transfer;
    }
}
