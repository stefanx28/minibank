package ro.axonsoft.eval.minibank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.model.Currency;
import ro.axonsoft.eval.minibank.model.ExchangeRatesProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;

@Service
@AllArgsConstructor
public class ExchangeRatesService {

    private final ExchangeRatesProperties properties;

    public Map<String, BigDecimal> getRates(){
        return Collections.unmodifiableMap(properties.rates());
    }

    public BigDecimal getRate(Currency currency){
        BigDecimal rate = properties.rates().get(currency.name());
        if(rate == null){
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        return rate;
    }

    public BigDecimal convert(BigDecimal amount, Currency src, Currency target){
        if(src == target){
            return amount.setScale(2, RoundingMode.HALF_EVEN);
        }
        BigDecimal srcToRon = getRate(src);
        BigDecimal targetToRon = getRate(target);

        return amount.multiply(srcToRon)
                .divide(targetToRon, 2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal toEur(BigDecimal amount, Currency currency) {
        return convert(amount, currency, Currency.EUR);
    }

    public BigDecimal getExchangeRate(Currency sourceCurrency, Currency targetCurrency) {
        return getRate(sourceCurrency)
                .divide(getRate(targetCurrency), 6, RoundingMode.HALF_EVEN);
    }

}
