package no.ntnu.idatt2003.model.io;

/**
 * Exception thrown when there is an error during game saving or loading.
 */
public class GameSaveException extends Exception {

    public GameSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
