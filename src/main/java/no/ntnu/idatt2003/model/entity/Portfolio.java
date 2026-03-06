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
