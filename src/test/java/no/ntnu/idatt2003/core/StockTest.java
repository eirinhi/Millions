package no.ntnu.idatt2003.core;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StockTest {

  private Stock stock;
  private List<BigDecimal> initialPrices;

  @BeforeEach
  void setUp() {
    initialPrices = new ArrayList<>();
    initialPrices.add(new BigDecimal("100.00"));

    stock = new Stock("AAPL", "Apple Inc.", new ArrayList<>(initialPrices));
  }

  @Test
  void testGetPrices() {
    List<BigDecimal> prices = stock.getPrices();
    assertEquals(1, prices.size());
    assertEquals(new BigDecimal("100.00"), prices.get(0));
  }

  @Test
  void testStockCreation() {

    assertEquals(stock.getSymbol(), "AAPL");
    assertEquals(stock.getCompany(), "Apple Inc.");
    assertEquals(stock.getSalesPrice(), new BigDecimal("100.00"));
  }

  @Test
  void testAddNewSalesPrice() {

    stock.addNewSalesPrice(new BigDecimal("130.00"));
    assertEquals(stock.getSalesPrice(), new BigDecimal("130.00"));
  }

  @Test
  void testIllegalAddNewSalesPrice() {
    Stock emptyStock = new Stock("AAPL", "Apple Inc.", new ArrayList<>());
    assertThrows(IllegalStateException.class, emptyStock::getSalesPrice);
  }

  @Test
  void testGetLatestSalesPrice() {
    stock.addNewSalesPrice(new BigDecimal("130.00"));
    assertEquals(stock.getSalesPrice(), new BigDecimal("130.00"));
  }
}
