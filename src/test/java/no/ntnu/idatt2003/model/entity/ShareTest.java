package no.ntnu.idatt2003.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;

class ShareTest {
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
        BigDecimal zero = new BigDecimal("0");

        assertThrows(IllegalArgumentException.class, () -> new Share(null, quantity, purchasePrice));

        assertThrows(IllegalArgumentException.class, () -> new Share(stock, null, purchasePrice));
        assertThrows(IllegalArgumentException.class, () -> new Share(stock, zero, purchasePrice));

        assertThrows(IllegalArgumentException.class, () -> new Share(stock, quantity, null));
        assertThrows(IllegalArgumentException.class, () -> new Share(stock, quantity, zero));
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
        assertEquals("10 SYMBOL - Company purchase price: 1000, value: 100.00", share.toString());
    }
}
