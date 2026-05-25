package no.ntnu.idatt2003.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.entity.Transaction;
import no.ntnu.idatt2003.model.logic.Exchange;

class TradeViewControllerTest {

    private Stock stock;
    private Exchange exchange;
    private Player player;
    private FakeTradeView view;
    private AtomicBoolean cancelled;
    private AtomicBoolean tradeCompleted;
    private TradeViewController controller;

    @BeforeEach
    void setUp() {
        stock = new Stock(
            "AAPL",
            "Apple",
            List.of(new BigDecimal("100"), new BigDecimal("120"))
        );
        exchange = new Exchange("Test Exchange", List.of(stock));
        player = new Player("Player", new BigDecimal("10000"));
        view = new FakeTradeView();
        cancelled = new AtomicBoolean(false);
        tradeCompleted = new AtomicBoolean(false);

        controller = new TradeViewController(
            exchange,
            player,
            stock,
            () -> tradeCompleted.set(true),
            () -> cancelled.set(true),
            view
        );
    }

    @Test
    void constructorInitializesBuyModeAndCurrentValues() {
        assertTrue(view.buyMode);
        assertEquals(9999, view.maxQuantity);
        assertEquals(new BigDecimal("120"), view.currentPrice);
        assertEquals(new BigDecimal("10000"), view.money);
        assertEquals(BigDecimal.ZERO, view.ownedQuantity);
        assertFalse(view.inWatchlist);
        assertTrue(view.previewUpdated);
    }

    @Test
    void setBuyModeUpdatesModeMaxQuantityAndPreview() {
        view.previewUpdated = false;

        controller.setBuyMode();

        assertTrue(view.buyMode);
        assertEquals(9999, view.maxQuantity);
        assertTrue(view.previewUpdated);
    }

    @Test
    void setSellModeShowsOwnedSharesForSale() {
        exchange.buy("AAPL", new BigDecimal("2"), player);

        controller.setSellMode();

        assertFalse(view.buyMode);
        assertEquals(1, view.sharesForSale.size());
        assertEquals(new BigDecimal("2"), view.selectedShare.getQuantity());
    }

    @Test
    void setSellModeWithNoSharesShowsEmptySellList() {
        controller.setSellMode();

        assertFalse(view.buyMode);
        assertTrue(view.sharesForSale.isEmpty());
        assertEquals(null, view.selectedShare);
    }

    @Test
    void updateOrderPreviewInBuyModeCalculatesPurchasePreview() {
        view.quantity = new BigDecimal("2");

        controller.setBuyMode();
        controller.updateOrderPreview();

        assertEquals(new BigDecimal("240"), view.gross);
        assertEquals(new BigDecimal("1.20"), view.commission);
        assertEquals(BigDecimal.ZERO, view.tax);
        assertEquals(new BigDecimal("241.20"), view.total);
    }

    @Test
    void updateOrderPreviewWithInvalidBuyQuantityShowsZeroPreview() {
        view.quantity = BigDecimal.ZERO;

        controller.updateOrderPreview();

        assertZeroPreview();
    }

    @Test
    void updateOrderPreviewInSellModeWithoutSelectedShareShowsZeroPreview() {
        controller.setSellMode();
        view.selectedShare = null;

        controller.updateOrderPreview();

        assertZeroPreview();
    }

    @Test
    void toggleWatchlistUpdatesPlayerAndView() {
        controller.toggleWatchlist();

        assertTrue(player.isInWatchlist("AAPL"));
        assertTrue(view.inWatchlist);

        controller.toggleWatchlist();

        assertFalse(player.isInWatchlist("AAPL"));
        assertFalse(view.inWatchlist);
    }

    @Test
    void cancelTradeRunsCancelCallback() {
        controller.cancelTrade();

        assertTrue(cancelled.get());
    }

    @Test
    void updateRefreshesPriceMoneyChartWatchlistAndOwnedQuantity() {
        exchange.buy("AAPL", new BigDecimal("3"), player);
        player.addToWatchlist("AAPL");
        stock.addNewSalesPrice(new BigDecimal("130"));

        controller.update();

        assertEquals(new BigDecimal("130"), view.currentPrice);
        assertEquals(player.getMoney(), view.money);
        assertEquals(stock.getHistoricalPrices(), view.chartPrices);
        assertTrue(view.inWatchlist);
        assertEquals(new BigDecimal("3"), view.ownedQuantity);
    }

    @Test
    void executeTradeWithNullQuantityShowsError() {
        controller.executeTrade(null);

        assertEquals("Quantity must be greater than zero.", view.errorMessage);
        assertFalse(tradeCompleted.get());
    }

    @Test
    void executeTradeWithZeroQuantityShowsError() {
        controller.executeTrade(BigDecimal.ZERO);

        assertEquals("Quantity must be greater than zero.", view.errorMessage);
        assertFalse(tradeCompleted.get());
    }

    @Test
    void executeTradeWhenPlayerCannotAffordShowsError() {
        controller.executeTrade(new BigDecimal("100000"));

        assertEquals(
            "You do not have enough money to complete this purchase.",
            view.errorMessage
        );
        assertFalse(tradeCompleted.get());
    }

    @Test
    void executeSellWithoutSelectedShareShowsError() {
        controller.setSellMode();
        view.selectedShare = null;

        controller.executeSell();

        assertEquals("Please select a share to sell.", view.errorMessage);
        assertFalse(tradeCompleted.get());
    }

    private void assertZeroPreview() {
        assertEquals(BigDecimal.ZERO, view.gross);
        assertEquals(BigDecimal.ZERO, view.commission);
        assertEquals(BigDecimal.ZERO, view.tax);
        assertEquals(BigDecimal.ZERO, view.total);
    }

    private static class FakeTradeView implements TradeViewController.TradeViewPort {

        private BigDecimal quantity = BigDecimal.ONE;
        private Share selectedShare;
        private List<Share> sharesForSale = List.of();
        private List<BigDecimal> chartPrices = List.of();

        private boolean buyMode;
        private boolean previewUpdated;
        private boolean inWatchlist;
        private int maxQuantity;

        private BigDecimal currentPrice;
        private BigDecimal money;
        private BigDecimal ownedQuantity;
        private BigDecimal gross;
        private BigDecimal commission;
        private BigDecimal tax;
        private BigDecimal total;
        private String errorMessage;

        @Override
        public void updateOrderPanelMode(final boolean isBuyMode) {
            buyMode = isBuyMode;
        }

        @Override
        public void setMaxQuantity(final int maxQuantity) {
            this.maxQuantity = maxQuantity;
        }

        @Override
        public BigDecimal getQuantity() {
            return quantity;
        }

        @Override
        public Share getSelectedShare() {
            return selectedShare;
        }

        @Override
        public void showSharesForSale(
                final List<Share> shares,
                final Runnable onSelectionChanged) {
            sharesForSale = shares;
            selectedShare = shares.isEmpty() ? null : shares.get(0);
        }

        @Override
        public void updateWatchlistButton(final boolean inWatchlist) {
            this.inWatchlist = inWatchlist;
        }

        @Override
        public void updateOrderPreview(
                final BigDecimal gross,
                final BigDecimal commission,
                final BigDecimal taxes,
                final BigDecimal total) {
            previewUpdated = true;
            this.gross = gross;
            this.commission = commission;
            this.tax = taxes;
            this.total = total;
        }

        @Override
        public void showError(final String message) {
            errorMessage = message;
        }

        @Override
        public void updateCurrentPrice(final BigDecimal price) {
            currentPrice = price;
        }

        @Override
        public void updateMoney(final BigDecimal money) {
            this.money = money;
        }

        @Override
        public void updatePriceChart(final List<BigDecimal> prices) {
            chartPrices = prices;
        }

        @Override
        public void updateOwnedQuantity(final BigDecimal quantity) {
            ownedQuantity = quantity;
        }

        @Override
        public void showReceipt(final Transaction transaction) {
        }
    }
}
