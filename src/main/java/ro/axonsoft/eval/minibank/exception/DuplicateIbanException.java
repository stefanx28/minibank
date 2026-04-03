package ro.axonsoft.eval.minibank.exception;

public class DuplicateIbanException extends RuntimeException {
    public DuplicateIbanException(String message) {
        super(message);
    }
}
