package no.ntnu.idatt2003.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExchangeTest {
    Stock s1;
    Stock s2;
    Stock s3;
    List<BigDecimal> prices;
    List<Stock> stocks;
    Exchange exchange;

    @BeforeEach
    void setUp() {
        prices = new ArrayList<>();
        prices.add(new BigDecimal("1000"));
        s1 = new Stock("S1", "Company1", prices);

        stocks = new ArrayList<>();
        stocks.add(s1);
        exchange = new Exchange("Test", stocks);
    }

    @Test
    void testConstructor() {
        List<Stock> emptyList = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> new Exchange(null, emptyList));
        assertThrows(IllegalArgumentException.class, () -> new Exchange("", emptyList));
        assertThrows(IllegalArgumentException.class, () -> new Exchange("Name", null));
    }

    @Test
    void testGetName() {
        assertEquals("Test", exchange.getName());
    }

    @Test
    void testGetWeek() {
        assertEquals(1, exchange.getWeek());
    }

    @Test
    void testHasStock() {
        String nonExisting = "SYMBOL";
        assertFalse(exchange.hasStock(nonExisting));
        assertTrue(exchange.hasStock(s1.getSymbol()));
    }

    @Test
    void testGetStock() {
        assertEquals(null, exchange.getStock(null));
        assertEquals(s1, exchange.getStock(s1.getSymbol()));
    }

    @Test
    void testFindStock() {
        assertEquals(List.of(), exchange.findStocks(null));
        assertEquals(List.of(), exchange.findStocks("searchTerm not found in stocks"));
        assertEquals(List.of(s1), exchange.findStocks("S1"));
        assertEquals(List.of(s1), exchange.findStocks("company1"));
    }

    @Test
    void testBuy() {
        BigDecimal quantiy = new BigDecimal("10");
        String name = "player";
        Player player = new Player(name, quantiy);
        assertThrows(IllegalArgumentException.class, () -> exchange.buy("non existing symbol", quantiy, player));

        Transaction transaction = exchange.buy("S1", quantiy, player);
        assertNotNull(transaction);
        assertTrue(transaction.isCommitted());
    }

    @Test
    void testSell() {
        BigDecimal quantiy = new BigDecimal("10");
        BigDecimal purchasePrice = new BigDecimal("1000");
        String name = "player";
        Player player = new Player(name, quantiy);
        Share share = new Share(s1, quantiy, purchasePrice);

        Transaction transaction = exchange.sell(share, player);
        assertNotNull(transaction);
        assertTrue(transaction.isCommitted());
    }

    @Test
    void testAdvance() {
        assertEquals(1, exchange.getWeek());
        BigDecimal currentPrice = s1.getSalesPrice();

        exchange.advance();
        assertEquals(2,exchange.getWeek());
        assertNotSame(currentPrice, s1.getSalesPrice());
    }
}
