package ru.hse.kostya.java;

public class LightExecutionException extends Exception {

    public LightExecutionException(String message) {
        super(message);
    }
    public LightExecutionException(Throwable cause) {
        super(cause);
    }

    public LightExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
