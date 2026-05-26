package no.ntnu.idatt2003.model.entity;

import no.ntnu.idatt2003.model.logic.TransactionCalculator;

/**
 * Represents a transaction in the stock game.
 * A transaction can be either a purchase or a sale of shares.
 */
public abstract class Transaction {

    /** The share involved in the transaction. */
    private final Share share;

    /** The week in which the transaction occurs. */
    private final int week;

    /** The calculator used to calculate transaction values. */
    private final TransactionCalculator calculator;

    /** Indicates if the transaction has been committed. */
    protected boolean committed;

    /**
     * Creates a new transaction with the specified share, week, and calculator.
     *
     * @param share      the share involved in the transaction
     * @param week       the week in which the transaction occurs
     * @param calculator the calculator used to calculate transaction values
     * @throws IllegalArgumentException if share or calculator
     *                                  is null, or week is negative
     */
    protected Transaction(
        final Share share,
        final int week,
        final TransactionCalculator calculator) {

        if (share == null || calculator == null) {
            throw new IllegalArgumentException(
                "Share and calculator cannot be null");
        }

        if (week < 0) {
            throw new IllegalArgumentException("Week cannot be negative");
        }

        this.share = share;
        this.week = week;
        this.calculator = calculator;
        this.committed = false;
    }

    /**
     * Returns the share involved in the transaction.
     *
     * @return the share
     */
    public Share getShare() {
        return share;
    }

    /**
     * Returns the week in which the transaction occurs.
     *
     * @return the week
     */
    public int getWeek() {
        return week;
    }

    /**
     * Returns the calculator used to calculate transaction values.
     *
     * @return the calculator
     */
    public TransactionCalculator getCalculator() {
        return calculator;
    }

    /**
     * Returns whether the transaction has been committed.
     *
     * @return true if the transaction is committed, false otherwise
     */
    public boolean isCommitted() {
        return committed;
    }

    /**
     * Commits the transaction.
     * This method should be implemented by subclasses to perform the
     * specific actions required for committing a buy or sell transaction.
     * The method should set the committed flag to true after successful
     * commitment, and should handle any necessary updates to the player's
     * portfolio, money, and transaction archive.
     *
     * @param player the player committing the transaction
     */
    public abstract void commit(Player player);
}
