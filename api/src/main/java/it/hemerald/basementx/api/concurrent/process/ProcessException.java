package it.hemerald.basementx.api.concurrent.process;

public class ProcessException extends RuntimeException {

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(String message, Throwable stack) {
        super(message, stack);
    }
}
