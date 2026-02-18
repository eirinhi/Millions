package no.ntnu.idatt2003.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseTest {
    List<BigDecimal> price;
    Stock stock;
    BigDecimal quantity;
    BigDecimal purchasePrice;
    Share share;
    int week;
    Purchase purchase;
    Player player;

    @BeforeEach
    void setUp() {
        price = List.of(new BigDecimal("100.00"));
        stock = new Stock("SYMBOL", "Company", price);
        quantity = new BigDecimal("10");
        purchasePrice = new BigDecimal("1000");
        share = new Share(stock, quantity, purchasePrice);
        week = 1;
        purchase = new Purchase(share, week);
        player = new Player();
    }

    @Test
    void testConstructor() {
        int illegalWeek = -1;
        assertThrows(IllegalArgumentException.class, () -> new Purchase(null, week));
        assertThrows(IllegalArgumentException.class, () -> new Purchase(share, illegalWeek));
    }

    @Test
    void testGetShare() {
        assertEquals(share, purchase.getShare());
    }

    @Test
    void testGetWeek() {
        assertEquals(week, purchase.getWeek());
    }
    @Test
    void testGetCalculator() {
        assertTrue(purchase.getCalculator() instanceof PurchaseCalculator);
    }

    @Test
    void testIsCommitted() {
        assertFalse(purchase.isCommitted());

        purchase.setCommitted(true);
        assertTrue(purchase.isCommitted());
    }

    @Test
    void testSetCommitted() {
        purchase.setCommitted(true);
        assertTrue(purchase.isCommitted());

        purchase.setCommitted(false);
        assertFalse(purchase.isCommitted());
    }

    @Test
    void testCommit() {
        purchase.commit(player);
        assertTrue(purchase.isCommitted());

        assertThrows(IllegalStateException.class, () -> purchase.commit(player));
        
        player = null;
        purchase.setCommitted(false);
        assertThrows(IllegalArgumentException.class, () -> purchase.commit(player));
    }
}
