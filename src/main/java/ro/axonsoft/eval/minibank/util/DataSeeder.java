package ro.axonsoft.eval.minibank.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.axonsoft.eval.minibank.domain.model.Accounts;
import ro.axonsoft.eval.minibank.dal.repository.AccountsRepository;
import ro.axonsoft.eval.minibank.config.BankProperties;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountsRepository accountsRepository;
    private final BankProperties bankProperties;

    @Override
    @Transactional
    public void run(String... args) {
        if (accountsRepository.count() == 0) {
            Accounts bank = new Accounts();
            bank.setOwnerName(bankProperties.getOwnerName());
            bank.setIban(bankProperties.getIban());
            bank.setCurrency(bankProperties.getCurrency());
            bank.setAccountType(bankProperties.getAccountType());
            bank.setBalance(BigDecimal.ZERO);
            accountsRepository.save(bank);
        }
    }
}