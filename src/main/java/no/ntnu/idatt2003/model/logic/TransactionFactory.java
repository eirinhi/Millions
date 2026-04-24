package no.ntnu.idatt2003.model.logic;

import no.ntnu.idatt2003.model.entity.Purchase;
import no.ntnu.idatt2003.model.entity.Sale;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Transaction;

/**
 * Factory for creating transaction objects.
 * All transaction creation should go through this class rather than
 * instantiating {@link Purchase} or {@link Sale} directly.
 */
public final class TransactionFactory {

  /**
   * Private constructor to prevent initialization of this utility class.
   */
  private TransactionFactory() {
  }

  /**
   * Creates a transaction of the specified type.
   * The correct calculator is assigned automatically based on the type.
   *
   * @param transactionType the type of transaction, either "purchase" or "sale"
   * @param share           the share involved in the transaction
   * @param week            the week in which the transaction occurs
   * @return a {@link Transaction} of the specified type
   * @throws IllegalArgumentException    if share is null or week is negative
   * @throws UnknownTransactionException if the transaction type is unknown
   */
  public static Transaction get(
      final String transactionType,
      final Share share,
      final int week) throws UnknownTransactionException {

    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    if (week < 0) {
      throw new IllegalArgumentException("Week cannot be negative");
    }

    return switch (transactionType) {
      case "purchase" -> new Purchase(share, week);
      case "sale" -> new Sale(share, week);
      default -> throw new UnknownTransactionException(
          "Unknown transaction type: '" + transactionType + "'");
    };
  }
}
