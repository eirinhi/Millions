package no.ntnu.idatt2003.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestShare {
    List<BigDecimal> price;
    Stock stock;
    BigDecimal quantity;
    BigDecimal purchasePrice;
    Share share;

    @BeforeEach
    void setUp() {
        price = List.of(new BigDecimal("100.00"));
        stock = new Stock("SYMBOL", "Company", price);
        quantity = new BigDecimal("10");
        purchasePrice = new BigDecimal("1000");
        share = new Share(stock, quantity, purchasePrice);
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Share(null, quantity, purchasePrice));

        assertThrows(IllegalArgumentException.class, () -> new Share(stock, null, purchasePrice));
        assertThrows(IllegalArgumentException.class, () -> new Share(stock, new BigDecimal("0"), purchasePrice));

        assertThrows(IllegalArgumentException.class, () -> new Share(stock, quantity, null));
        assertThrows(IllegalArgumentException.class, () -> new Share(stock, quantity, new BigDecimal("0")));
    }

    @Test
    void testGetStock() {
        assertEquals(stock, share.getStock());
    }

    @Test
    void testGetQuantity() {
        assertEquals(quantity, share.getQuantity());
    }

    @Test
    void testGetPurchasePrice() {
        assertEquals(purchasePrice, share.getPurchasePrice());
    }

    @Test
    void testToString() {
        assertEquals(quantity + " shares of " + stock.getSymbol() + " at " + purchasePrice + " per share", share.toString());
    }
}
