package ro.axonsoft.eval.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.axonsoft.eval.minibank.model.Accounts;

import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Accounts, Long> {

    boolean existsByIban(String iban);

    Optional<Accounts> findByIban(String iban);
}
