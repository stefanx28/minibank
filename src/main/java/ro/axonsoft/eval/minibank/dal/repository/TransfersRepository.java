package ro.axonsoft.eval.minibank.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.axonsoft.eval.minibank.domain.model.Transfers;

import java.util.Optional;

public interface TransfersRepository extends JpaRepository<Transfers, Long> {
    Optional<Transfers> findByIdempotencyKey(String idenpotencyKey);
}
