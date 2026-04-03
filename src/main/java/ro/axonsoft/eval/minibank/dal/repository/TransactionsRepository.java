package ro.axonsoft.eval.minibank.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.axonsoft.eval.minibank.domain.enums.TransactionType;
import ro.axonsoft.eval.minibank.domain.model.Transactions;

import java.time.Instant;
import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

    List<Transactions> findByAccountIdOrderByTimestampAsc(Long accountId);

    List<Transactions> findByAccountIdAndTypeInAndTimestampBetween(Long accountId, List<TransactionType> types, Instant startTimestamp, Instant endTimestamp);
}