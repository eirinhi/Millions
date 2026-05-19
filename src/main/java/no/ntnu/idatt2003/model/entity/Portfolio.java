package no.ntnu.idatt2003.model.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.idatt2003.model.logic.SaleCalculator;

/**
 * Represents a portfolio of shares owned by a player in the stock game.
 */
public class Portfolio {

    /** List of shares in the portfolio. */
    private final List<Share> shares;

    /**
     * Creates a new, empty portfolio.
     */
    public Portfolio() {
        this.shares = new ArrayList<>();
    }

    /**
     * Adds a share to the portfolio.
     *
     * @param share the share to add
     * @return true if the share was added successfully, false otherwise
     */
    public boolean addShare(final Share share) {
        if (share == null || shares.contains(share)) {
            return false;
        }
        return shares.add(share);
    }

    /**
     * Removes a share from the portfolio.
     *
     * @param share the share to remove
     * @return true if the share was removed successfully, false otherwise
     */
    public boolean removeShare(final Share share) {
        if (share == null || !shares.contains(share)) {
            return false;
        }
        return shares.remove(share);
    }

    /**
     * Returns a copy of the list of shares in the portfolio.
     *
     * @return the list of shares
     */
    public List<Share> getShares() {
        return List.copyOf(shares);
    }

    /**
     * Returns the list of shares matching the given stock symbol.
     *
     * @param symbol the stock symbol to search for
     * @return the list of shares matching the symbol
     * @throws IllegalArgumentException if the symbol is null
     */
    public List<Share> getShares(final String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol cannot be null");
        }

        return shares.stream()
                .filter(s -> s.getStock().getSymbol().equals(symbol))
                .toList();
    }

    /**
     * Returns the quantity of the given stock owned by the player.
     *
     * @param stock the stock to check
     * @return the quantity owned
     */
    public BigDecimal getQuantityOwned(final Stock stock) {
        if (stock == null) {
            return BigDecimal.ZERO;
        }

        return shares.stream()
                .filter(share -> share.getStock().getSymbol().equals(stock.getSymbol()))
                .map(Share::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the share owned by the player for the given stock.
     *
     * @param stock the stock to check
     * @return the owned share, or null if not owned
     */
    public Share getOwnedShare(final Stock stock) {
        return shares.stream()
                .filter(share -> share.getStock().getSymbol().equals(stock.getSymbol()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Sells a specified quantity of shares from the player's owned shares.
     *
     * @param stock the stock to sell
     * @param quantity the quantity to sell
     * @throws IllegalStateException if the player does not own the stock
     * @throws IllegalArgumentException if the quantity is negative
     * @throws IllegalStateException if the quantity exceeds owned quantity
     */
    public void sellPartialShare(final Stock stock, final BigDecimal quantity) {
        Share ownedShare = getOwnedShare(stock);

        if (ownedShare == null) {
            throw new IllegalStateException("You do not own this stock.");
        }

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        if (quantity.compareTo(ownedShare.getQuantity()) > 0) {
            throw new IllegalStateException("You cannot sell more shares than you own.");
        }

        shares.remove(ownedShare);

        BigDecimal remainingQuantity = ownedShare.getQuantity().subtract(quantity);

        if (remainingQuantity.compareTo(BigDecimal.ZERO) > 0) {
            shares.add(new Share(
                    stock,
                    remainingQuantity,
                    ownedShare.getPurchasePrice()
            ));
        }
    }

    /**
     * Checks if the portfolio contains the given share.
     *
     * @param share the share to check for
     * @return true if the share is in the portfolio, false otherwise
     */
    public boolean contains(final Share share) {
        return shares.contains(share);
    }

    /**
     * Returns the net worth of the portfolio.
     * The net worth is the sum of the total value of the sale
     * of all shares in the portfolio.
     *
     * @return the net worth of the portfolio
     */
    public BigDecimal getNetWorth() {
        return shares.stream()
                .map(share -> new SaleCalculator(share).calculateTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
