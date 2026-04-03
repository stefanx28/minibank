package ro.axonsoft.eval.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.axonsoft.eval.minibank.model.TransactionType;
import ro.axonsoft.eval.minibank.model.Transactions;

import java.time.Instant;
import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

    List<Transactions> findByAccountIdOrderByTimestampAsc(Long accountId);

    List<Transactions> findByAccountIdAndTypeInAndTimestampBetween(
            Long accountId,
            List<TransactionType> types,
            Instant start,
            Instant end
    );
}