package no.ntnu.idatt2003.model.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SaleCalculatorTest {

  private SaleCalculator positiveSale;
  private SaleCalculator negativeSale;
  private SaleCalculator zeroProfitSale;

  @BeforeEach
  void setUp() {
    Stock positiveStock = new Stock("SYMBOL", "Company", List.of(new BigDecimal("120.00")));
    Share positiveShare = new Share(positiveStock, new BigDecimal("10"), new BigDecimal("100.00"));
    positiveSale = new SaleCalculator(positiveShare);

    Stock negativeStock = new Stock("SYMBOL", "Company", List.of(new BigDecimal("90.00")));
    Share negativeShare = new Share(negativeStock, new BigDecimal("10"), new BigDecimal("100.00"));
    negativeSale = new SaleCalculator(negativeShare);

    Stock zeroStock = new Stock("SYMBOL", "Company", List.of(new BigDecimal("100.00")));
    Share zeroShare = new Share(zeroStock, new BigDecimal("10"), new BigDecimal("100.00"));
    zeroProfitSale = new SaleCalculator(zeroShare);
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

