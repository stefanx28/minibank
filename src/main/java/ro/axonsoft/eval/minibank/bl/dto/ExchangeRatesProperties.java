package ro.axonsoft.eval.minibank.bl.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

@ConfigurationProperties(prefix = "exchange")
public record ExchangeRatesProperties(Map<String, BigDecimal> rates) {
}
