package no.ntnu.idatt2003.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;

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
        assertTrue(player.getMoney().equals(new java.math.BigDecimal(5000)));
    }

    @Test
    void testLoadStocksWithInvalidFile() {
        assertEquals(null, StartController.loadStocks(new File("nonexistent.csv")));

        File file = new File("src/test/resources/test-stocks.csv");
        List<Stock> stocks = StartController.loadStocks(file);
        assertEquals(2, stocks.size());
    }
}
