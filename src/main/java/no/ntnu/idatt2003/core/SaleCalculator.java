package no.ntnu.idatt2003.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SaleCalculator implements the TransactionCalculator  interface for
 * sales and transactions.
 * <p>
 *   It calculates gross value, commission, tax, and total value of a
 *   sale based on the sale price, purchase price, and quantity.
 * </p>
 * Rules applied:
 * <ul>
 *   <li>Gross = salePrice * quantity</li>
 *   <li>Commission = 1% of gross</li>
 *   <li>Tax = 30% of profit (profit = gross - commission - purchase price</li>
 *   <li>Total = gross - commission - tax</li>
 * </ul>
 */
public class SaleCalculator implements TransactionCalculator {

  /** Purchase price per unit*/
  private final BigDecimal purchasePrice;

  /** Sale price per unit*/
  private final BigDecimal salePrice;

  /** Quantity of items sold*/
  private final BigDecimal quantity;

  /** Commission rate (1%)*/
  private final BigDecimal COMMISSION_RATE = BigDecimal.valueOf(0.01);

  /** Tax rate (30%)*/
  private final BigDecimal TAX_RATE = BigDecimal.valueOf(0.30);

  /**
   * Construct a SaleCalculator with the specified purchase
   * price, sale price, and quantity.
   *
   * @param purchasePrice the price paid per unit
   * @param salePrice     the price sold per unit
   * @param quantity      the number of units sold
   */
  public SaleCalculator(BigDecimal purchasePrice, BigDecimal salePrice, BigDecimal quantity) {
    this.purchasePrice = purchasePrice;
    this.salePrice = salePrice;
    this.quantity = quantity;
  }

  /**
   * Calculates the gross value of the sale.
   * <p>
   * Gross = salePrice *quantity
   *
   * @return gross value rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateGross() {
    return salePrice
        .multiply(quantity)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the commission fee for the sale.
   * <p>
   * Commission = 1% of gross value
   *
   * @return commission amount rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross()
        .multiply(COMMISSION_RATE)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the tax on the profit of the sale.
   * <p>
   * Profit = gross - commission - (purchase * quantity)
   * Tax = 30% of profit if profit is positive, else, tax is 0.
   *
   * @return tax amount rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateTax() {
    BigDecimal profit = calculateGross()
        .subtract(calculateCommission())
        .subtract(purchasePrice.multiply(quantity));

    if (profit.compareTo(BigDecimal.ZERO) <= 0) {
      return BigDecimal.ZERO
          .setScale(2, RoundingMode.HALF_UP);
    }
    return profit
        .multiply(TAX_RATE)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the total value of the sale.
   * <p>
   * Total = gross - commission - tax
   *
   * @return total sale value rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross()
        .subtract(calculateCommission())
        .subtract(calculateTax())
        .setScale(2, RoundingMode.HALF_UP);
  }
}