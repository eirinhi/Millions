package no.ntnu.idatt2003.model.io;

import java.io.IOException;

/**
 * Exception thrown when there is an error during game saving or loading.
 */
public class GameSaveException extends IOException {

    /**
     * Creates a new GameSaveException with the given message and cause.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public GameSaveException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
