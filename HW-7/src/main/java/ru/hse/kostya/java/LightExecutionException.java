package ru.hse.kostya.java;

/**
 * Exception that should be returned when another exception was thrown
 *      during execution of LightFuture's get function.
 */
public class LightExecutionException extends Exception {

    public LightExecutionException(Throwable cause) {
        super(cause);
    }

    public LightExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
