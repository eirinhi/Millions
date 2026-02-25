package no.ntnu.idatt2003.core;

import java.math.BigDecimal;

/**
 * Represents a purchase transaction in the stock game.
 * A purchase transaction involves buying a share of a stock.
 */
public class Purchase extends Transaction {

    /**
     * Creates a new purchase transaction with the specified share and week.
     *
     * @param share the share being purchased
     * @param week the week in which the purchase occurs
     * @throws IllegalArgumentException if share is null or if week is negative
     */
    public Purchase(final Share share, final int week) {
        super(share, week, new PurchaseCalculator(share));
    }

    /**
     * Commits the purchase transaction for the given player.
     *
     * @param player the player making the purchase
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

        BigDecimal totalCost = getCalculator().calculateTotal();

        if (player.getMoney().compareTo(totalCost) < 0) {
            throw new IllegalStateException(
                "Not enough money to complete purchase");
        }

        setCommitted(true);
        player.withdrawMoney(totalCost);
        player.getPortfolio().addShare(getShare());
        player.getTransactionArchive().add(this);
    }
}
