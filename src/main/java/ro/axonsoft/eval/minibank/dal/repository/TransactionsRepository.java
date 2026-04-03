package ro.axonsoft.eval.minibank.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.axonsoft.eval.minibank.domain.enums.TransactionType;
import ro.axonsoft.eval.minibank.domain.model.Transactions;

import java.time.Instant;
import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

    Page<Transactions> findByAccountIdOrderByTimestampAsc(Long accountId, Pageable pageable);

    List<Transactions> findByAccountIdAndTypeInAndTimestampBetween(Long accountId, List<TransactionType> types, Instant startTimestamp, Instant endTimestamp);
}