package no.ntnu.idatt2003.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;

class StartControllerTest {

    @Test
    void testValidValidateInput() {
        assertTrue(StartController.validateInput("Valid Name", 1000, new File("dummy.csv")));
    }

    @Test
    void testInvalidValidateInput() {
        assertFalse(StartController.validateInput("", 1000, new File("dummy.csv")));
        assertFalse(StartController.validateInput(null, 1000, new File("dummy.csv")));

        assertFalse(StartController.validateInput("Name", 0, new File("dummy.csv")));
        assertFalse(StartController.validateInput("Name", 1000000, new File("dummy.csv")));

        assertFalse(StartController.validateInput("Name", 1000, null));
    }

    @Test
    void testCreatePlayer() {
        Player player = StartController.createPlayer("Test Player", 5000);
        assertEquals("Test Player", player.getName());
        assertEquals(new BigDecimal("5000"), player.getMoney());
        assertEquals(new BigDecimal("5000"), player.getStartingMoney());
    }

    @Test
    void testCreatePlayerRejectsInvalidValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> StartController.createPlayer(null, 5000)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> StartController.createPlayer("   ", 5000)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> StartController.createPlayer("Player", -1)
        );
    }

    @Test
    void testValidateInputBlankName() {
        File file = new File("dummy.csv");

        assertFalse(StartController.validateInput("   ", 1000, file));
    }

    @Test
    void testLoadStocksWithInvalidFileReturnsNull() {
        List<Stock> stocks = StartController.loadStocks(new File("nonexistent.csv"));

        assertNull(stocks);
    }

    @Test
    void testLoadStocksWithNullFileReturnsNull() {
        assertNull(StartController.loadStocks(null));
    }

    @Test
    void testLoadStocksWithValidFile() {
        File file = new File("src/test/resources/test-stocks.csv");

        List<Stock> stocks = StartController.loadStocks(file);

        assertEquals(2, stocks.size());

        Stock first = stocks.get(0);
        assertEquals("AAPL", first.getSymbol());
        assertEquals("Apple Inc", first.getCompany());
        assertEquals(new BigDecimal("150.00"), first.getSalesPrice());
    }

    @Test
    void testCreateExchange() {
        File file = new File("src/test/resources/test-stocks.csv");
        List<Stock> stocks = StartController.loadStocks(file);

        Exchange exchange = StartController.createExchange(stocks);

        assertEquals("Millions Exchange", exchange.getName());
        assertEquals(2, exchange.getAllStocks().size());
        assertTrue(exchange.hasStock(stocks.get(0).getSymbol()));
    }

    @Test
    void testCreateExchangeRejectsInvalidStockList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> StartController.createExchange(null)
        );
    }

    @Test
    void testCreateExchangeAllowsEmptyStockList() {
        Exchange exchange = StartController.createExchange(List.of());

        assertEquals("Millions Exchange", exchange.getName());
        assertTrue(exchange.getAllStocks().isEmpty());
    }

    @Test
    void testValidateInputCapitalBoundaries() {
        File file = new File("dummy.csv");

        assertFalse(StartController.validateInput("Name", 999, file));
        assertTrue(StartController.validateInput("Name", 1000, file));
        assertTrue(StartController.validateInput("Name", 100000, file));
        assertFalse(StartController.validateInput("Name", 100001, file));
    }

    @Test
    void testCreateMainSceneReturnsNullForInvalidInputBeforeJavaFxIsNeeded() {
        File file = new File("src/test/resources/test-stocks.csv");

        assertNull(StartController.createMainScene("", 1000, file));
        assertNull(StartController.createMainScene(null, 1000, file));
        assertNull(StartController.createMainScene("Name", 999, file));
        assertNull(StartController.createMainScene("Name", 100001, file));
        assertNull(StartController.createMainScene("Name", 1000, null));
    }

    @Test
    void testCreateMainSceneReturnsNullWhenStockFileCannotBeLoaded() {
        assertNull(StartController.createMainScene(
                "Name",
                1000,
                new File("nonexistent.csv")
        ));
    }
}
