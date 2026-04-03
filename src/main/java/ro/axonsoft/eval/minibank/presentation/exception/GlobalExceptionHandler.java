package ro.axonsoft.eval.minibank.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidIbanException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidIban(InvalidIbanException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAccountNotFound(AccountNotFoundException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(TransferNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleTransferNotFound(TransferNotFoundException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(DuplicateIbanException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateIban(DuplicateIbanException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleInsufficientFunds(InsufficientFundsException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(NonSepaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNonSepa(NonSepaException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(DailyLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDailyLimit(DailyLimitExceededException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return Map.of("status", "REJECTED", "message", "Transfer conflict, please retry");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidAmount(InvalidAmountException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }

    @ExceptionHandler(UnsupportedCurrencyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnsupportedCurrency(UnsupportedCurrencyException ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneric(Exception ex) {
        return Map.of("status", "REJECTED", "message", ex.getMessage());
    }
}