package no.ntnu.idatt2003.core;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

  private Stock stock;
  private List<BigDecimal> initialPrices;

  @BeforeEach
  void setUp() {
    initialPrices = new ArrayList<>();
    initialPrices.add(new BigDecimal("100.00"));

    stock = new Stock("AAPL", "Apple Inc.", new ArrayList<>(initialPrices));
  }

  @Test
  void testConstructor() {
    assertThrows(IllegalArgumentException.class, () -> new Stock(null, "Company", initialPrices));
    assertThrows(IllegalArgumentException.class, () -> new Stock("SYMBOL", null, initialPrices));
    assertThrows(IllegalArgumentException.class, () -> new Stock("SYMBOL", "Company", null));
  }

  @Test
  void testGetPrices() {
    List<BigDecimal> prices = stock.getPrices();
    assertEquals(1, prices.size());
    assertEquals(new BigDecimal("100.00"), prices.get(0));
  }

  @Test
  void testStockCreation() {
    assertEquals("AAPL", stock.getSymbol());
    assertEquals("Apple Inc.", stock.getCompany());
    assertEquals(stock.getSalesPrice(), new BigDecimal("100.00"));
  }

  @Test
  void testAddNewSalesPrice() {
    stock.addNewSalesPrice(new BigDecimal("130.00"));
    assertEquals(stock.getSalesPrice(), new BigDecimal("130.00"));
  }

  @Test
  void testIllegalAddNewSalesPrice() {
    assertThrows(IllegalArgumentException.class, () -> stock.addNewSalesPrice(null));
  }

  @Test
  void testToString() {
    assertEquals("Stock [symbol=AAPL, company=Apple Inc., prices=[100.00]]", stock.toString());
  }
}
