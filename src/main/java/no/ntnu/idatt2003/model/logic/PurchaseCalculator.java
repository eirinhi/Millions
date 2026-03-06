package no.ntnu.idatt2003.model.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import no.ntnu.idatt2003.model.entity.Share;

/**
 * Performs calculations for a purchase transaction.
 * The calculations are based on purchase price and quantity.
 */
public class PurchaseCalculator implements TransactionCalculator {

  /** Commission rate for purchase transactions. */
  private static final BigDecimal COMMISSION_RATE =
      new BigDecimal("0.005");

  /** The purchase price of the share. */
  private final BigDecimal purchasePrice;

  /** The quantity of shares in the purchase. */
  private final BigDecimal quantity;

  /**
   * Creates a PurchaseCalculator based on a Share.
   * @param share the share involved in the purchase
   * @throws IllegalArgumentException if share is null
   */
  public PurchaseCalculator(final Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share must not be null");
    }

    this.purchasePrice = share.getPurchasePrice();
    this.quantity = share.getQuantity();
  }

  /**
   * Calculates the gross value of the purchase.
   * Gross value is defined as purchasePrice multiplied by quantity.
   *
   * @return the gross value of the purchase
   */
  @Override
  public BigDecimal calculateGross() {
    return purchasePrice.multiply(quantity);
  }

  /**
   * Calculates the commission fee for the purchase.
   * Commission is 0.5% of the gross value.
   *
   * @return the commission fee
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross()
        .multiply(COMMISSION_RATE)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the tax for the purchase.
   * For purchases, tax is always zero.
   *
   * @return zero tax
   */
  @Override
  public BigDecimal calculateTax() {
    return BigDecimal.ZERO;
  }

  /**
   * Calculate the total cost of the purchase.
   * Total is gross value plus commission and tax.
   *
   * @return the total purchase cost
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross()
        .add(calculateCommission());
  }
}
