package no.ntnu.idatt2003.model.logic;

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

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.entity.Transaction;
import no.ntnu.idatt2003.model.logic.Exchange;

class ExchangeTest {
    Stock s1;
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
        BigDecimal startingMoney = new BigDecimal("20000");
        Player player = new Player(name, startingMoney);
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
        BigDecimal startingMoney = new BigDecimal("20000");
        Player player = new Player(name, startingMoney);

        Share share = new Share(s1, quantiy, purchasePrice);
        player.getPortfolio().addShare(share);

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

    @Test
    void testInvalidGetGainers() {
        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> exchange.getGainers(-1)
        );
        assertEquals("Limit must be greater than 0.", e.getMessage());
    }

    @Test
    void testInvalidGetLosers() {
        Exception e = assertThrows(
            IllegalArgumentException.class,
            () -> exchange.getLosers(-1)
        );
        assertEquals("Limit must be greater than 0.", e.getMessage());
    }

    @Test
    void testGainersAndLosers() {
        List<BigDecimal> prices1 = new ArrayList<>();
        prices1.add(new BigDecimal("100"));
        s1 = new Stock("SYMBOL1", "Company1", prices1);
        
        List<BigDecimal> prices2 = new ArrayList<>();
        prices2.add(new BigDecimal("100"));
        Stock s2 = new Stock("SYMBOL2", "Company2", prices2);

        List<BigDecimal> prices3 = new ArrayList<>();
        prices3.add(new BigDecimal("100"));
        Stock s3 = new Stock("SYMBOL3", "Company3", prices3);

        exchange = new Exchange("Test", List.of(s1, s2, s3));

        s1.addNewSalesPrice(new BigDecimal("120"));
        s2.addNewSalesPrice(new BigDecimal("110"));
        s3.addNewSalesPrice(new BigDecimal("90"));

        List<Stock> gainers = exchange.getGainers(3);
        assertEquals(List.of(s1, s2, s3), gainers);

        List<Stock> losers = exchange.getLosers(3);
        assertEquals(List.of(s3, s2, s1), losers);
    }
}
