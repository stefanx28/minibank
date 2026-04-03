package ro.axonsoft.eval.minibank.presentation.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.axonsoft.eval.minibank.bl.service.ExchangeRatesService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange-rates")
@AllArgsConstructor
public class ExchangeController {
    private final ExchangeRatesService exchangeRatesService;

    @GetMapping
    public Map<String, BigDecimal> getExchangeRates() {

        return exchangeRatesService.getRates();
    }
}
