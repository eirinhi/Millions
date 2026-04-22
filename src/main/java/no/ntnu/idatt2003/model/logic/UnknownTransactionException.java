package no.ntnu.idatt2003.model.logic;

/**
 * Thrown when an unknown transaction type is provided to {@link TransactionFactory}.
 */
public class UnknownTransactionException extends Exception {

  /**
   * Creates a new exception with the given message.
   *
   * @param message the detail message
   */
  public UnknownTransactionException(final String message) {
    super(message);
  }
}
