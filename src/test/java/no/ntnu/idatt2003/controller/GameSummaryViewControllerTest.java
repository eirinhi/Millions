package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSummaryViewControllerTest {

    private Exchange exchange;
    private Player player;

    @BeforeEach
    void setUp() {
        Stock stock = new Stock(
            "S1", "Stock1",
            List.of(BigDecimal.valueOf(100), BigDecimal.valueOf(150))
        );
        exchange = new Exchange("TestExchange", List.of(stock));
        player = new Player("TestPlayer", BigDecimal.valueOf(10000));
    }

    @Test
    void portfolioIsEmptyAfterConstruction() {
        exchange.buy("S1", BigDecimal.valueOf(10), player);
        new GameSummaryViewController(exchange, player);
        assertTrue(player.getPortfolio().getShares().isEmpty());
    }

    @Test
    void gainIsZeroWhenNoTrades() {
        GameSummaryViewController controller =
            new GameSummaryViewController(exchange, player);
        assertEquals(
            BigDecimal.ZERO,
            controller.getGain().stripTrailingZeros()
        );
    }

    @Test
    void gainPositiveIsTrueWhenNoLoss() {
        GameSummaryViewController controller =
            new GameSummaryViewController(exchange, player);
        assertTrue(controller.isGainPositive());
    }

    @Test
    void returnPctIsZeroWhenNoTrades() {
        GameSummaryViewController controller =
            new GameSummaryViewController(exchange, player);
        assertEquals(
            BigDecimal.ZERO,
            controller.getReturnPct().stripTrailingZeros()
        );
    }

    @Test
    void returnPositiveIsTrueWhenNoLoss() {
        GameSummaryViewController controller =
            new GameSummaryViewController(exchange, player);
        assertTrue(controller.isReturnPositive());
    }

    @Test
    void gainAndReturnAreNegativeWhenPortfolioLosesValue() {
        exchange.buy("S1", BigDecimal.valueOf(10), player);
        exchange.getStock("S1").addNewSalesPrice(BigDecimal.valueOf(50));

        GameSummaryViewController controller =
            new GameSummaryViewController(exchange, player);

        assertEquals(new BigDecimal("-1012.50"), controller.getGain());
        assertEquals(new BigDecimal("-10.13"), controller.getReturnPct());
        assertFalse(controller.isGainPositive());
        assertFalse(controller.isReturnPositive());
    }

    @Test
    void gainAndReturnArePositiveWhenPortfolioIncreasesValue() {
        exchange.buy("S1", BigDecimal.valueOf(10), player);
        exchange.getStock("S1").addNewSalesPrice(BigDecimal.valueOf(300));

        GameSummaryViewController controller =
            new GameSummaryViewController(exchange, player);

        assertEquals(new BigDecimal("1021.50"), controller.getGain());
        assertEquals(new BigDecimal("10.22"), controller.getReturnPct());
        assertTrue(controller.isGainPositive());
        assertTrue(controller.isReturnPositive());
    }

    @Test
    void constructorSellsAllSharesAndArchivesSales() {
        exchange.buy("S1", BigDecimal.valueOf(4), player);
        exchange.buy("S1", BigDecimal.valueOf(6), player);

        new GameSummaryViewController(exchange, player);

        assertTrue(player.getPortfolio().getShares().isEmpty());
        assertEquals(2, player.getTransactionArchive().getSales(1).size());
        assertEquals(4, player.getTransactionArchive().getAll().size());
    }

    @Test
    void zeroGainAndReturnAreTreatedAsPositive() {
        GameSummaryViewController controller =
            new GameSummaryViewController(exchange, player);

        assertEquals(BigDecimal.ZERO, controller.getGain().stripTrailingZeros());
        assertEquals(BigDecimal.ZERO, controller.getReturnPct().stripTrailingZeros());
        assertTrue(controller.isGainPositive());
        assertTrue(controller.isReturnPositive());
    }
}
