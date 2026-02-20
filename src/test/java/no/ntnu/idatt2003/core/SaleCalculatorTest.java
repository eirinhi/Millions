package no.ntnu.idatt2003.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SaleCalculatorTest {

  private SaleCalculator positiveSale;
  private SaleCalculator negativeSale;
  private SaleCalculator zeroProfitSale;

  @BeforeEach
  void setUp() {

    positiveSale = new SaleCalculator(
        new BigDecimal("100.00"),  // purchasePrice
        new BigDecimal("120.00"),  // salePrice
        new BigDecimal("10")       // quantity
    );

    negativeSale = new SaleCalculator(
        new BigDecimal("100.00"), // purchasePrice
        new BigDecimal("90.00"),  // salePrice
        new BigDecimal("10")      // quantity
    );

    zeroProfitSale = new SaleCalculator(
        new BigDecimal("100.00"),  // purchasePrice
        new BigDecimal("100.00"),  // salePrice
        new BigDecimal("10")       // quantity
    );
  }

  @Test
  void testPositiveProfit() {
    assertEquals(new BigDecimal("1200.00"), positiveSale.calculateGross());
    assertEquals(new BigDecimal("12.00"),   positiveSale.calculateCommission());
    assertEquals(new BigDecimal("56.40"),   positiveSale.calculateTax());
    assertEquals(new BigDecimal("1131.60"), positiveSale.calculateTotal());
  }

  @Test
  void testNegativeProfit() {
    assertEquals(new BigDecimal("900.00"), negativeSale.calculateGross());
    assertEquals(new BigDecimal("9.00"),   negativeSale.calculateCommission());
    assertEquals(new BigDecimal("0.00"),   negativeSale.calculateTax());
    assertEquals(new BigDecimal("891.00"), negativeSale.calculateTotal());
  }

  @Test
  void testZeroProfit() {
    assertEquals(new BigDecimal("1000.00"), zeroProfitSale.calculateGross());
    assertEquals(new BigDecimal("10.00"),   zeroProfitSale.calculateCommission());
    assertEquals(new BigDecimal("0.00"),    zeroProfitSale.calculateTax());
    assertEquals(new BigDecimal("990.00"),  zeroProfitSale.calculateTotal());
  }
}

