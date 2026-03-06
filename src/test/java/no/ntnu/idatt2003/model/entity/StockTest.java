package no.ntnu.idatt2003.model.entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Stock;

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
  void testGetHistoricalPrices() {
    List<BigDecimal> prices = stock.getHistoricalPrices();
    assertEquals(1, prices.size());
    assertEquals(new BigDecimal("100.00"), prices.get(0));

    stock = new Stock("SYMBOL", "Company", List.of());
    assertThrows(IllegalStateException.class, () -> stock.getSalesPrice());
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
  void testGetHighestPrice() {
    stock.addNewSalesPrice(new BigDecimal("100.00"));
    stock.addNewSalesPrice(new BigDecimal("130.00"));
    assertEquals(new BigDecimal("130.00"), stock.getHighestPrice());
  }

  @Test
  void testGetLowestPrice() {
    stock.addNewSalesPrice(new BigDecimal("100.00"));
    stock.addNewSalesPrice(new BigDecimal("130.00"));
    assertEquals(new BigDecimal("100.00"), stock.getLowestPrice());
  }

  @Test
  void testGetLatestPriceChange() {
    stock.addNewSalesPrice(new BigDecimal("130.00"));
    assertEquals(new BigDecimal("30.00"), stock.getLatestPriceChange());
  }

  @Test
  void testToString() {
    String output = stock.toString();

    assertTrue(output.contains("Stock information"));
    assertTrue(output.contains("Symbol: AAPL"));
    assertTrue(output.contains("Company: Apple Inc."));
    assertTrue(output.contains("Current price: 100.00"));
  }
}
