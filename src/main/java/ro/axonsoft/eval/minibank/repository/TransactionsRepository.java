package ro.axonsoft.eval.minibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.axonsoft.eval.minibank.model.Transactions;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

}
