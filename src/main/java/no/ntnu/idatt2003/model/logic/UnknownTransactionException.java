package no.ntnu.idatt2003.model.logic;

import no.ntnu.idatt2003.model.MillionsException;

/**
 * Thrown when an unknown transaction type is provided to {@link TransactionFactory}.
 */
public class UnknownTransactionException extends MillionsException {

    /**
     * Creates a new exception with the given message.
     *
     * @param message the detail message
     */
    public UnknownTransactionException(final String message) {
        super(message);
    }
}
