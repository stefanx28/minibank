package ro.axonsoft.eval.minibank.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ro.axonsoft.eval.minibank.domain.enums.AccountType;
import ro.axonsoft.eval.minibank.domain.model.Accounts;
import ro.axonsoft.eval.minibank.domain.enums.Currency;
import ro.axonsoft.eval.minibank.dal.repository.AccountsRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountsRepository accountsRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (accountsRepository.count() == 0) {
            Accounts bank = new Accounts();
            bank.setOwnerName("Bank");
            bank.setIban("RO49AAAA1B31007593840000");
            bank.setCurrency(Currency.RON);
            bank.setAccountType(AccountType.CHECKING);
            bank.setBalance(BigDecimal.ZERO);
            accountsRepository.save(bank);
        }
    }
}
