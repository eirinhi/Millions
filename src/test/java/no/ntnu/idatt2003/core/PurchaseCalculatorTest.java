package no.ntnu.idatt2003.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PurchaseCalculatorTest {

  private Stock stock;
  private Share share;
  private PurchaseCalculator calculator;

  @BeforeEach
  void setUp() {
    stock      = new Stock("AAPL", "Apple Inc.", List.of(new BigDecimal("100.00")));
    share      = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));
    calculator = new PurchaseCalculator(share);
  }

  @Test
  void testConstructorNullShare() {
    assertThrows(IllegalArgumentException.class, () -> new PurchaseCalculator(null));
  }

  @Test
  void testCalculateGross(){
    assertEquals(new BigDecimal("500.00"), calculator.calculateGross());
  }

  @Test
  void testCalculateCommission (){
    assertEquals(new BigDecimal("2.50"), calculator.calculateCommission());
  }

  @Test
  void testCalculateTax(){
    assertEquals(BigDecimal.ZERO, calculator.calculateTax());
  }

  @Test
  void testCalculateTotal(){
    assertEquals(new BigDecimal("502.50"), calculator.calculateTotal());
  }
}
