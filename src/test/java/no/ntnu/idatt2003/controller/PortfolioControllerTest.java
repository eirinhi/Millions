package no.ntnu.idatt2003.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.controller.PortfolioFormatter.HoldingRow;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;

class PortfolioControllerTest {

    private Stock stock;
    private Exchange exchange;
    private Player player;
    private FakeMainView mainView;
    private FakePortfolioViewFactory viewFactory;
    private AtomicReference<Stock> tradeRequest;
    private PortfolioController controller;

    @BeforeEach
    void setUp() {
        stock = new Stock(
            "AAPL",
            "Apple",
            List.of(new BigDecimal("100"), new BigDecimal("120"))
        );
        exchange = new Exchange("Test Exchange", List.of(stock));
        player = new Player("Player", new BigDecimal("10000"));
        mainView = new FakeMainView();
        viewFactory = new FakePortfolioViewFactory();
        tradeRequest = new AtomicReference<>();

        controller = new PortfolioController(
            mainView,
            player,
            exchange,
            tradeRequest::set,
            viewFactory
        );
    }

    @Test
    void refreshAndRecordWeekAreSafeBeforeViewIsCreated() {
        controller.refresh();
        controller.recordWeek();

        assertEquals(null, viewFactory.createdView);
        assertEquals(0, mainView.setViewCount);
    }

    @Test
    void showPortfolioViewWithoutMainViewThrows() {
        PortfolioController controllerWithoutMainView = new PortfolioController(
            (PortfolioController.MainViewPort) null,
            player,
            exchange,
            testStock -> {},
            viewFactory
        );

        assertThrows(IllegalStateException.class, controllerWithoutMainView::showPortfolioView);
    }

    @Test
    void showPortfolioViewCreatesViewAndSetsInitialData() {
        player.addNetWorthRecord(new BigDecimal("10000"));

        controller.showPortfolioView();

        FakePortfolioView view = viewFactory.createdView;
        assertEquals(view, mainView.lastView);
        assertEquals(1, mainView.setViewCount);
        assertEquals(List.of(10000.0), view.chartHistory);
        assertEquals("+0,00%", view.performance);
        assertEquals("10000,00 $", view.equity);
        assertEquals("10000,00 $", view.money);
        assertTrue(view.positive);
    }

    @Test
    void showPortfolioViewWithEmptyHistoryRecordsCurrentNetWorth() {
        controller.showPortfolioView();

        assertEquals(List.of(), viewFactory.createdView.chartHistory);
        assertEquals(List.of(10000.0), viewFactory.createdView.chartPoints);
    }

    @Test
    void refreshUpdatesStatsHoldingsAndReceipts() {
        exchange.buy("AAPL", new BigDecimal("2"), player);
        controller.showPortfolioView();

        FakePortfolioView view = viewFactory.createdView;

        assertEquals(1, view.holdingRows.size());
        HoldingRow row = view.holdingRows.get(0);
        assertEquals("AAPL", row.symbol());
        assertEquals(2, row.quantity());
        assertEquals(1, view.receipts.size());
        assertEquals("Purchase", view.receipts.get(0).type());
        assertEquals("9758,80 $", view.money);
    }

    @Test
    void captureWeekStartMakesRefreshShowWeeklyPerformance() {
        exchange.buy("AAPL", new BigDecimal("2"), player);
        controller.showPortfolioView();

        controller.captureWeekStart();
        stock.addNewSalesPrice(new BigDecimal("130"));
        controller.refresh();

        assertEquals("+0,15%", viewFactory.createdView.performance);
        assertTrue(viewFactory.createdView.positive);
    }

    @Test
    void onSearchAndFilterLimitDisplayedReceipts() {
        exchange.buy("AAPL", BigDecimal.ONE, player);
        Share share = player.getPortfolio().getShares().get(0);
        exchange.sell(share, player);
        controller.showPortfolioView();

        FakePortfolioView view = viewFactory.createdView;
        assertEquals(2, view.receipts.size());

        controller.onFilter("Sale");
        assertEquals(1, view.receipts.size());
        assertEquals("Sale", view.receipts.get(0).type());

        controller.onFilter("all");
        controller.onSearch("aapl");
        assertEquals(2, view.receipts.size());

        controller.onSearch("2");
        assertTrue(view.receipts.isEmpty());
    }

    @Test
    void stockSelectionCallbackRequestsTradeForKnownStockOnly() {
        controller.showPortfolioView();

        viewFactory.createdView.selectStock("AAPL");
        assertEquals(stock, tradeRequest.get());

        tradeRequest.set(null);
        viewFactory.createdView.selectStock("UNKNOWN");
        assertEquals(null, tradeRequest.get());
    }

    @Test
    void updateRefreshesCreatedView() {
        controller.showPortfolioView();
        int updateCount = viewFactory.createdView.updateStatsCount;

        controller.update();

        assertEquals(updateCount + 1, viewFactory.createdView.updateStatsCount);
    }

    @Test
    void disposeStopsExchangeObserverUpdates() {
        controller.showPortfolioView();
        int updateCount = viewFactory.createdView.updateStatsCount;

        controller.dispose();
        exchange.advance();

        assertEquals(updateCount, viewFactory.createdView.updateStatsCount);
    }

    private static class FakeMainView implements PortfolioController.MainViewPort {

        private PortfolioController.PortfolioViewPort lastView;
        private int setViewCount;

        @Override
        public void setView(final PortfolioController.PortfolioViewPort view) {
            lastView = view;
            setViewCount++;
        }
    }

    private static class FakePortfolioViewFactory
            implements PortfolioController.PortfolioViewFactory {

        private FakePortfolioView createdView;

        @Override
        public PortfolioController.PortfolioViewPort create(
                final Consumer<String> onStockSelected) {
            createdView = new FakePortfolioView(onStockSelected);
            return createdView;
        }
    }

    private static class FakePortfolioView implements PortfolioController.PortfolioViewPort {

        private final Consumer<String> onStockSelected;
        private String performance;
        private String equity;
        private String money;
        private boolean positive;
        private int updateStatsCount;
        private List<HoldingRow> holdingRows = List.of();
        private List<ReceiptData> receipts = List.of();
        private List<Double> chartHistory = List.of();
        private List<Double> chartPoints = List.of();
        private Consumer<String> searchHandler;
        private Consumer<String> filterHandler;

        FakePortfolioView(final Consumer<String> onStockSelected) {
            this.onStockSelected = onStockSelected;
        }

        @Override
        public void updateStats(
                final String performance,
                final String equity,
                final String money,
                final boolean positive) {
            this.performance = performance;
            this.equity = equity;
            this.money = money;
            this.positive = positive;
            updateStatsCount++;
        }

        @Override
        public void setHoldingRows(final List<HoldingRow> rows) {
            holdingRows = rows;
        }

        @Override
        public void setReceipts(final List<ReceiptData> receipts) {
            this.receipts = receipts;
        }

        @Override
        public void addChartPoint(final double netWorth) {
            chartPoints = List.of(netWorth);
        }

        @Override
        public void setChartHistory(final List<Double> values) {
            chartHistory = values;
        }

        @Override
        public void setOnTransactionSearch(final Consumer<String> handler) {
            searchHandler = handler;
        }

        @Override
        public void setOnTransactionFilter(final Consumer<String> handler) {
            filterHandler = handler;
        }

        void selectStock(final String symbol) {
            onStockSelected.accept(symbol);
        }
    }
}
