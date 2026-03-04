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

        sale.committed = true;
        assertTrue(sale.isCommitted());
    }

    @Test
    void testCommit() {
        player.getPortfolio().addShare(share);

        sale.commit(player);
        assertTrue(sale.isCommitted());
        
        BigDecimal totalFromSale = sale.getCalculator().calculateTotal();
        BigDecimal expectedMoney = startingMoney.add(totalFromSale);
        assertEquals(0, expectedMoney.compareTo(player.getMoney()));
        assertFalse(player.getPortfolio().contains(share));
        assertTrue(player.getTransactionArchive().getTransactions(week).contains(sale));
    }
    
    @Test
    void testInvalidCommit() {
        assertThrows(IllegalArgumentException.class, () -> sale.commit(null));

        sale.committed = true;
        assertThrows(IllegalStateException.class, () -> sale.commit(player));

        stock = new Stock(name, name, price);
        share = new Share(stock, quantity, salePrice);
        sale = new Sale(share, week);
        assertThrows(IllegalStateException.class, () -> sale.commit(player));
    }
}
