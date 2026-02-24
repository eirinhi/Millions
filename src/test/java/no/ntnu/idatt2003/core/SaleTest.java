package no.ntnu.idatt2003.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleTest {
    List<BigDecimal> price;
    Stock stock;
    BigDecimal quantity;
    BigDecimal salePrice;
    Share share;
    int week;
    Sale sale;
    String name;
    BigDecimal startingMoney;
    Player player;

    @BeforeEach
    void setUp() {
        price = List.of(new BigDecimal("100.00"));
        stock = new Stock("SYMBOL", "Company", price);
        quantity = new BigDecimal("10");
        salePrice = new BigDecimal("1000");
        share = new Share(stock, quantity, salePrice);
        week = 1;
        sale = new Sale(share, week);
        name = "Player1";
        startingMoney = new BigDecimal("10000");
        player = new Player(name, startingMoney);
    }

    @Test
    void testConstructor() {
        int illegalWeek = -1;
        assertThrows(IllegalArgumentException.class, () -> new Sale(null, week));
        assertThrows(IllegalArgumentException.class, () -> new Sale(share, illegalWeek));
    }

    @Test
    void testGetShare() {
        assertEquals(share, sale.getShare());
    }

    @Test
    void testGetWeek() {
        assertEquals(week, sale.getWeek());
    }
    @Test
    void testGetCalculator() {
        assertTrue(sale.getCalculator() instanceof SaleCalculator);
    }

    @Test
    void testIsCommitted() {
        assertFalse(sale.isCommitted());

        sale.setCommitted(true);
        assertTrue(sale.isCommitted());
    }

    @Test
    void testSetCommitted() {
        sale.setCommitted(true);
        assertTrue(sale.isCommitted());

        sale.setCommitted(false);
        assertFalse(sale.isCommitted());
    }

    @Test
    void testCommit() {
        sale.commit(player);
        assertTrue(sale.isCommitted());

        assertThrows(IllegalStateException.class, () -> sale.commit(player));
        
        player = null;
        sale.setCommitted(false);
        assertThrows(IllegalArgumentException.class, () -> sale.commit(player));
    }
}
