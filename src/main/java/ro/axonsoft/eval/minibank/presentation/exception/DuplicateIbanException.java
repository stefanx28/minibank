package ro.axonsoft.eval.minibank.presentation.exception;

public class DuplicateIbanException extends RuntimeException {
    public DuplicateIbanException(String message) {
        super(message);
    }
}
