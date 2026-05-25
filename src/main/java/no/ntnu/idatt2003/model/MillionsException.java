package no.ntnu.idatt2003.model;

import java.io.IOException;

/**
 * Super exception class for specific errors in the application.
 */
public class MillionsException extends IOException {

    /**
     * Creates a new exception with the given message.
     *
     * @param message the detail message
     */
    public MillionsException(final String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given message and cause.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public MillionsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
