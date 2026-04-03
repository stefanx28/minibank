package ro.axonsoft.eval.minibank.exception;

public class InvalidIbanException extends RuntimeException {
    public InvalidIbanException(String message) {
        super(message);
    }
}
