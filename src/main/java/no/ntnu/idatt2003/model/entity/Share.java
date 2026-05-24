package no.ntnu.idatt2003.model.entity;

import java.math.BigDecimal;

/**
 * Represents a share of a stock.
 * A share consists of a stock, a quantity, and a purchase price.
 */
public class Share {

    /** The stock associated with this share. */
    private final Stock stock;

    /** The quantity of shares. */
    private final BigDecimal quantity;

    /** The purchase price of the share. */
    private final BigDecimal purchasePrice;

    /**
     * Creates a new share with the given stock, quantity, and purchase price.
     * @param stock         the stock associated with this share
     * @param quantity      the quantity of shares
     * @param purchasePrice the purchase price of the share
     * @throws IllegalArgumentException if any of the parameters are invalid
     */
    public Share(
        final Stock stock,
        final BigDecimal quantity,
        final BigDecimal purchasePrice) {

        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null");
        }

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (purchasePrice == null
            || purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Purchase price must be positive");
        }

        this.stock = stock;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    /**
     * Returns the stock associated with this share.
     * 
     * @return the stock
     */
    public Stock getStock() {
        return stock;
    }

    /**
     * Returns the quantity of shares.
     * 
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Returns the purchase price of the share.
     * 
     * @return the purchase price
     */
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Returns a string representation of the share.
     * 
     * @return the string representation
     */
    @Override
    public String toString() {
        return quantity + " " + stock.getSymbol()
                + " - " + stock.getCompany()
                + " purchase price: " + purchasePrice
                + ", value: " + stock.getSalesPrice();
    }
}
