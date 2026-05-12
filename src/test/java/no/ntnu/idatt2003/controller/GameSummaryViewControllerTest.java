package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}
