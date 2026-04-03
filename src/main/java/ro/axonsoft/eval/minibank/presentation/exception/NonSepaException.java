package ro.axonsoft.eval.minibank.presentation.exception;

public class NonSepaException extends RuntimeException {
    public NonSepaException(String message) {
        super(message);
    }
}
