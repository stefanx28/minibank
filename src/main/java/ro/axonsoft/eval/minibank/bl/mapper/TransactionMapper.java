package ro.axonsoft.eval.minibank.bl.mapper;

import ro.axonsoft.eval.minibank.domain.enums.TransactionType;
import ro.axonsoft.eval.minibank.domain.model.Accounts;
import ro.axonsoft.eval.minibank.domain.model.Transactions;
import ro.axonsoft.eval.minibank.domain.model.Transfers;

import java.math.BigDecimal;

public class TransactionMapper {

    public static Transactions toEntity(Accounts account, Transfers transfer, TransactionType type, BigDecimal amount, String targetIban){
        Transactions transaction = new Transactions();
        transaction.setAccount(account);
        transaction.setTransfer(transfer);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getCurrency());
        transaction.setBalanceAfter(account.getBalance());
        transaction.setCounterpartyIban(targetIban);
        return transaction;
    }
}
