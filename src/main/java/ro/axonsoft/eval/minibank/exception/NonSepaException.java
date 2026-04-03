package ro.axonsoft.eval.minibank.exception;

public class NonSepaException extends RuntimeException {
    public NonSepaException(String message) {
        super(message);
    }
}
