package no.ntnu.idatt2003.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortfolioTest {
    Portfolio portfolio;
    List<BigDecimal> price;
    Stock stock;
    BigDecimal quantity;
    BigDecimal purchasePrice;
    Share share;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        price = List.of(new BigDecimal("100.00"));
        stock = new Stock("SYMBOL", "Company", price);
        quantity = new BigDecimal("10");
        purchasePrice = new BigDecimal("1000");
        share = new Share(stock, quantity, purchasePrice);
    }

    @Test
    void testConstructor() {
        assertEquals(0, portfolio.getShares().size());
    }

    @Test
    void testAddShare() {
        assertEquals(0, portfolio.getShares().size());
        assertEquals(true, portfolio.addShare(share));
        assertEquals(1, portfolio.getShares().size());
        assertEquals(false, portfolio.addShare(share));
        assertEquals(1, portfolio.getShares().size());

        assertFalse(portfolio.addShare(null));
        assertFalse(portfolio.addShare(share));
    }

    @Test
    void testRemoveShare() {
        portfolio.addShare(share);
        assertEquals(1, portfolio.getShares().size());
        assertEquals(true, portfolio.removeShare(share));
        assertEquals(0, portfolio.getShares().size());
        assertEquals(false, portfolio.removeShare(share));
        assertEquals(0, portfolio.getShares().size());

        assertFalse(portfolio.removeShare(null));
        assertFalse(portfolio.removeShare(share));
    }

    @Test
    void testGetShares() {
        portfolio.addShare(share);
        assertEquals(List.of(share), portfolio.getShares());
    }

    @Test
    void testGetSharesBySymbol() {
        portfolio.addShare(share);
        assertEquals(List.of(share), portfolio.getShares(share.getStock().getSymbol()));
        assertThrows(IllegalArgumentException.class, () -> portfolio.getShares(null));
    }

    @Test
    void testContains() {
        portfolio.addShare(share);
        assertTrue(portfolio.contains(share));
    }
}
