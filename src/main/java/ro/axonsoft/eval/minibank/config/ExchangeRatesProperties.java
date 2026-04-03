package ro.axonsoft.eval.minibank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;


@ConfigurationProperties(prefix = "exchange")
public record ExchangeRatesProperties(Map<String, BigDecimal> rates) {
}
