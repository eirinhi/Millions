package no.ntnu.idatt2003.model.entity;

import no.ntnu.idatt2003.model.logic.SaleCalculator;

/**
 * Represents a sale transaction in the stock game.
 * A sale transaction involves selling a share of a stock.
 */
public class Sale extends Transaction {

    /**
     * Creates a new sale transaction with the specified share and week.
     *
     * @param share the share being sold
     * @param week  the week in which the sale occurs
     * @throws IllegalArgumentException if share is null or if week is negative
     */
    public Sale(final Share share, final int week) {
        super(share, week, new SaleCalculator(share));
    }

    /**
     * Commits the sale transaction for the given player.
     *
     * @param player the player making the sale
     * @throws IllegalStateException if the transaction has been committed
     * @throws IllegalArgumentException if player is null
     */
    @Override
    public void commit(final Player player) {
        if (isCommitted()) {
            throw new IllegalStateException("Transaction already committed");
        }

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        if (!player.getPortfolio().removeShare(getShare())) {
            throw new IllegalStateException("You do not own this stock.");
        }

        committed = true;

        player.addMoney(getCalculator().calculateTotal());
        player.getTransactionArchive().add(this);
    }
}
