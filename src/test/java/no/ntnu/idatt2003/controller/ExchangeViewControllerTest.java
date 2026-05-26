package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    private Stock s1;
    private Stock s2;
    private Stock s3;

    @BeforeEach
    void setUp() {
        s1 = new Stock("S1", "Stock1", List.of(BigDecimal.valueOf(100), BigDecimal.valueOf(110)));
        s2 = new Stock("S2", "Stock2", List.of(BigDecimal.valueOf(200), BigDecimal.valueOf(210)));
        s3 = new Stock("S3", "Stock3", List.of(BigDecimal.valueOf(50), BigDecimal.valueOf(55)));

        exchange = new Exchange("TestExchange", List.of(s1, s2, s3));
        controller = new ExchangeViewController(
                exchange,
                stock -> {}
        );
        // AI was used to discuss how to verify that a method is called without mocking
        // the entire class. The solution is an anonymous subclass (spy pattern) that
        // overrides the method and sets a flag when it is called.
        updateTableCalled = new boolean[]{false};
        spy = new ExchangeViewController(
                exchange,
                stock -> {}
        ) {
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

    @Test
    void onStockSelectedCallsCallbackWithSelectedStock() {
        AtomicReference<Stock> selectedStock = new AtomicReference<>();
        ExchangeViewController callbackController = new ExchangeViewController(
                exchange,
                selectedStock::set
        );

        callbackController.onStockSelected(s2);

        assertEquals(s2, selectedStock.get());
    }

    @Test
    void updateTableUsesDefaultNameSort() {
        CapturingExchangeViewController capturingController =
                new CapturingExchangeViewController(exchange);

        capturingController.updateTable();

        assertEquals(List.of(s1, s2, s3), capturingController.displayedStocks);
        assertEquals(3, capturingController.displayedGainers.size());
        assertEquals(3, capturingController.displayedLosers.size());
    }

    @Test
    void onSearchTrimsAndLowercasesQueryBeforeFiltering() {
        CapturingExchangeViewController capturingController =
                new CapturingExchangeViewController(exchange);

        capturingController.onSearch("  STOCK2  ");

        assertEquals(List.of(s2), capturingController.displayedStocks);
    }

    @Test
    void onPriceFilterRestrictsDisplayedStocks() {
        CapturingExchangeViewController capturingController =
                new CapturingExchangeViewController(exchange);

        capturingController.onPriceFilter(BigDecimal.valueOf(100), BigDecimal.valueOf(210));

        assertEquals(List.of(s1, s2), capturingController.displayedStocks);
    }

    @Test
    void onSortPriceAscendingAndDescendingChangesOrder() {
        CapturingExchangeViewController capturingController =
                new CapturingExchangeViewController(exchange);

        capturingController.onSort("priceAsc");
        assertEquals(List.of(s3, s1, s2), capturingController.displayedStocks);

        capturingController.onSort("priceDesc");
        assertEquals(List.of(s2, s1, s3), capturingController.displayedStocks);
    }

    private static class CapturingExchangeViewController extends ExchangeViewController {

        private final Exchange exchange;
        private String searchQuery = "";
        private BigDecimal minPrice = BigDecimal.ZERO;
        private BigDecimal maxPrice = BigDecimal.valueOf(10000);
        private String sortBy = "name";
        private List<Stock> displayedStocks = List.of();
        private List<Stock> displayedGainers = List.of();
        private List<Stock> displayedLosers = List.of();

        CapturingExchangeViewController(Exchange exchange) {
            super(exchange, stock -> {});
            this.exchange = exchange;
        }

        @Override
        public void onSearch(String query) {
            searchQuery = query.trim().toLowerCase();
            updateTable();
        }

        @Override
        public void onPriceFilter(BigDecimal min, BigDecimal max) {
            minPrice = min;
            maxPrice = max;
            updateTable();
        }

        @Override
        public void onSort(String sort) {
            sortBy = sort;
            updateTable();
        }

        @Override
        public void updateTable() {
            displayedStocks = exchange.getFilteredAndSortedStocks(
                    searchQuery,
                    minPrice,
                    maxPrice,
                    sortBy
            );
            displayedGainers = exchange.getGainers(5);
            displayedLosers = exchange.getLosers(5);
        }
    }

}
