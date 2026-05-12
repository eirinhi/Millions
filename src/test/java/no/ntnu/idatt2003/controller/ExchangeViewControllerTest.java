package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeViewControllerTest {

    private Exchange exchange;
    private ExchangeViewController controller;
    private ExchangeViewController spy;
    private boolean[] updateTableCalled;

    @BeforeEach
    void setUp() {
        Stock s1 = new Stock("S1", "Stock1", List.of(BigDecimal.valueOf(100), BigDecimal.valueOf(110)));
        Stock s2 = new Stock("S2", "Stock2", List.of(BigDecimal.valueOf(200), BigDecimal.valueOf(210)));
        Stock s3 = new Stock("S3", "Stock3", List.of(BigDecimal.valueOf(50), BigDecimal.valueOf(55)));

        exchange = new Exchange("TestExchange", List.of(s1, s2, s3));
        controller = new ExchangeViewController(exchange);

        // AI was used to discuss how to verify that a method is called without mocking
        // the entire class. The solution is an anonymous subclass (spy pattern) that
        // overrides the method and sets a flag when it is called.
        updateTableCalled = new boolean[]{false};
        spy = new ExchangeViewController(exchange) {
            @Override
            public void updateTable() {
                updateTableCalled[0] = true;
            }
        };
    }

    @Test
    void setViewTest() {
        spy.setView(null);
        spy.update();
    }

    @Test
    void getAllStocksTest() {
        assertEquals(3, controller.getAllStocks().size());
    }

    @Test
    void updateTest() {
        spy.update();
        assertTrue(updateTableCalled[0]);
    }

    @Test
    void onSearchTest() {
        spy.onSearch("Stock1");
        assertTrue(updateTableCalled[0]);
    }

    @Test
    void onPriceFilterTest() {
        spy.onPriceFilter(BigDecimal.valueOf(50), BigDecimal.valueOf(200));
        assertTrue(updateTableCalled[0]);
    }

    @Test
    void updateTableTest() {
        assertThrows(NullPointerException.class, () -> controller.updateTable());
    }

    @Test
    void getGainersTest() {
        assertTrue(controller.getGainers().size() <= 3);
    }

    @Test
    void getLosersTest() {
        assertTrue(controller.getLosers().size() <= 3);
    }

    @Test
    void onSortTest() {
        spy.onSort("priceAsc");
        assertTrue(updateTableCalled[0]);
    }

}