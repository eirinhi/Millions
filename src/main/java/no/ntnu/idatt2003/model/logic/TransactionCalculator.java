package no.ntnu.idatt2003.model.logic;

import java.math.BigDecimal;

/**
 * Defines calculation methods for financial transactions.
 * Implementations handle purchase and sale calculations.
 */
public interface TransactionCalculator {

  /**
   * Calculates the gross value of the transaction.
   *
   * @return gross value before fees and tax
   */
  BigDecimal calculateGross();

  /**
   * Calculate the commission fee.
   *
   * @return commission amount
   */
  BigDecimal calculateCommission();

  /**
   * Calculate the tax.
   *
   * @return tax amount
   */
  BigDecimal calculateTax();

  /**
   * Calculates the total transaction value.
   *
   * @return total value including fees and tax
   */
  BigDecimal calculateTotal();
}
