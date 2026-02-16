package no.ntnu.idatt2003.core;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.core.Stock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class testStock {

  @Test
  void testStockCreation() {
    List<BigDecimal> price = new ArrayList<>();
    price.add(new BigDecimal("100.00"));

    Stock stock = new Stock("AAPL", "Apple Inc.", price);

    assertEquals(stock.getSymbol(), "AAPL");
    assertEquals(stock.getCompany(), "Apple Inc.");
    assertEquals(stock.getSalesPrice(), new BigDecimal("100.00"));
  }

  @Test
  void testAddNewSalesPrice() {
    List<BigDecimal> price = new ArrayList<>();
    price.add(new BigDecimal("100.00"));

    Stock stock = new Stock("AAPL", "Apple Inc.", price);

    stock.addNewSalesPrice(new BigDecimal("130.00"));

    assertEquals(stock.getSalesPrice(), new BigDecimal("130.00"));
  }
  @Test
  void testIllegalAddNewSalesPrice() {
    List<BigDecimal> price = new ArrayList<>();
    Stock stock = new Stock("AAPL", "Apple Inc.", price);

    assertThrows(IllegalStateException.class, () ->{
      stock.getSalesPrice();
    });
  }
  @Test
  void testGetLatestSalesPrice() {
    List<BigDecimal> price = new ArrayList<>();
    price.add(new BigDecimal("100.00"));
    price.add(new BigDecimal("130.00"));

    Stock stock = new Stock("AAPL", "Apple Inc.", price);

    BigDecimal result = stock.getSalesPrice();
    assertEquals(result, new BigDecimal("130.00"));

  }
}
