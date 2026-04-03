package ro.axonsoft.eval.minibank.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.axonsoft.eval.minibank.model.Accounts;

import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Accounts, Long> {

    boolean existsByIban(String iban);

    Optional<Accounts> findByIban(String iban);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Accounts a WHERE a.iban = :iban")
    Optional<Accounts> findByIbanForUpdate(@Param("iban") String iban);
}
