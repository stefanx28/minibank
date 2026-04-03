package ro.axonsoft.eval.minibank.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

@ConfigurationProperties(prefix = "exchange-rates")
public record ExchangeRatesProperties(Map<String, BigDecimal> rates) {
}
