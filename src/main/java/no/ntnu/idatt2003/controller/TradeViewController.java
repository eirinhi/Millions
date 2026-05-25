package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.entity.Transaction;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.model.logic.PurchaseCalculator;
import no.ntnu.idatt2003.model.logic.SaleCalculator;
import no.ntnu.idatt2003.model.logic.TransactionCalculator;
import javafx.application.Platform;
import no.ntnu.idatt2003.model.observer.Observer;
import no.ntnu.idatt2003.view.SoundPlayer;
import no.ntnu.idatt2003.view.TradeView;

/**
 * Controller for the trade view.
 *
 * <p>Handles stock purchases and sales for a selected stock.
 * The controller validates trades, calculates transaction previews,
 * updates the trade view, and observes the exchange for price changes.</p>
 */
public class TradeViewController implements Observer {

    /** Maximum allowed quantity for a buy order. */
    private static final int MAX_BUY_QUANTITY = 9999;

    /** The exchange used for market operations. */
    private final Exchange exchange;

    /** The player executing trades. */
    private final Player player;

    /** The stock currently selected for trading. */
    private final Stock stock;

    /** Concrete JavaFX trade view exposed to the application. */
    private final TradeView tradeView;

    /** View boundary used by the controller. */
    private final TradeViewPort view;

    /** Callback invoked after a trade completes successfully. */
    private final Runnable onTradeCompleted;

    /** Callback invoked when the user cancels the trade view. */
    private final Runnable onCancel;

    /** Whether the controller is currently in buy mode. */
    private boolean buyMode = true;

    /**
     * Creates a new trade view controller for the selected stock.
     *
     * @param exchange          the exchange used to execute trades
     * @param player            the current player performing trades
     * @param stock             the stock currently displayed in the trade view
     * @param onTradeCompleted  callback executed after a successful trade
     * @param onCancel          callback executed when the trade view is cancelled
     */
    public TradeViewController(
            final Exchange exchange,
            final Player player,
            final Stock stock,
            final Runnable onTradeCompleted,
            final Runnable onCancel) {

        this.exchange = exchange;
        this.player = player;
        this.stock = stock;
        this.onTradeCompleted = onTradeCompleted;
        this.onCancel = onCancel;

        this.tradeView = new TradeView(this, stock, player);
        this.view = new TradeViewAdapter(tradeView);

        this.exchange.attach(this);

        setBuyMode();
        updateView();
    }

    /**
     * Creates a controller with a supplied view boundary.
     *
     * <p>This constructor is package-private so tests can exercise controller
     * logic without starting JavaFX.</p>
     *
     * <p>This was introduced through AI assistance to facilitate testing coverage.
     * In production, the controller uses real TradeView. In test, it uses {@code TradeViewPort}.</p>
     *
     * @param exchange         the exchange used to execute trades  
     * @param player           the current player performing trades
     * @param stock            the stock currently displayed in the trade view
     * @param onTradeCompleted callback executed after a successful trade
     * @param onCancel         callback executed when the trade view is cancelled
     * @param view             test view boundary
     */
    TradeViewController(
            final Exchange exchange,
            final Player player,
            final Stock stock,
            final Runnable onTradeCompleted,
            final Runnable onCancel,
            final TradeViewPort view) {

        this.exchange = exchange;
        this.player = player;
        this.stock = stock;
        this.onTradeCompleted = onTradeCompleted;
        this.onCancel = onCancel;
        this.tradeView = null;
        this.view = view;

        this.exchange.attach(this);

        setBuyMode();
        updateView();
    }

    /**
     * Returns the trade view managed by this controller.
     *
     * @return the trade view
     */
    public TradeView getView() {
        return tradeView;
    }

    /**
     * Updates the trade view when the exchange model changes.
     */
    @Override
    public void update() {
        updateView();
    }

    /**
     * Switches the trade view into buy mode.
     */
    public void setBuyMode() {
        buyMode = true;
        view.updateOrderPanelMode(true);
        view.setMaxQuantity(MAX_BUY_QUANTITY);
        updateOrderPreview();
    }

    /**
     * Switches the trade view into sell mode.
     */
    public void setSellMode() {
        buyMode = false;
        view.updateOrderPanelMode(false);

        List<Share> shares = player.getPortfolio().getShares(stock.getSymbol());
        view.showSharesForSale(shares, this::updateOrderPreview);
    }

    /**
     * Cancels the trade view and returns to the previous view.
     */
    public void cancelTrade() {
        exchange.detach(this);
        onCancel.run();
    }

    /**
     * Toggles the currently displayed stock in the player's watchlist.
     */
    public void toggleWatchlist() {
        boolean inWatchlist = player.toggleWatchlist(stock.getSymbol());
        view.updateWatchlistButton(inWatchlist);
    }

    /**
     * Updates the transaction preview displayed in the trade view.
     */
    public void updateOrderPreview() {
        if (buyMode) {
            BigDecimal quantity = view.getQuantity();
            try {
                TransactionCalculator calculator = createPurchaseCalculator(quantity);
                view.updateOrderPreview(
                    calculator.calculateGross(),
                    calculator.calculateCommission(),
                    calculator.calculateTax(),
                    calculator.calculateTotal()
                );

            } catch (IllegalStateException | IllegalArgumentException e) {
                view.updateOrderPreview(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                );
            }
        } else {
            Share selectedShare = view.getSelectedShare();
            if (selectedShare == null) {
                view.updateOrderPreview(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                );
                return;
            }
            try {
                SaleCalculator calculator = new SaleCalculator(selectedShare);
                    view.updateOrderPreview(
                        calculator.calculateGross(),
                        calculator.calculateCommission(),
                        calculator.calculateTax(),
                        calculator.calculateTotal()
                    );
            } catch (IllegalStateException |IllegalArgumentException e) {
                view.updateOrderPreview(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                );
            }
        }
    }

    /**
     * Executes a stock buy transaction.
     *
     * @param quantity the quantity of shares to trade
     */
    public void executeTrade(final BigDecimal quantity) {
        try {
            validateQuantity(quantity);

            Transaction transaction;

            validateCanAfford(quantity);
            transaction = exchange.buy(stock.getSymbol(), quantity, player);

            SoundPlayer.playKaching();
            onTradeCompleted.run();
            updateView();
            Platform.runLater(() -> view.showReceipt(transaction));

        } catch (IllegalStateException | IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    /**
     * Executes a stock sale transaction.
     */
    public void executeSell() {
        try {
            Share selectedShare = view.getSelectedShare();
            if (selectedShare == null) {
                view.showError("Please select a share to sell.");
                return;
            }
            Transaction transaction = exchange.sell(selectedShare, player);
            SoundPlayer.playKaching();
            onTradeCompleted.run();
            updateView();
            Platform.runLater(() -> view.showReceipt(transaction));
        } catch (IllegalStateException | IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private TransactionCalculator createPurchaseCalculator(
        final BigDecimal quantity) {

        Share share = new Share(
            stock,
            quantity,
            stock.getSalesPrice()
        );

        return new PurchaseCalculator(share);
    }

    private void validateQuantity(final BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero."
            );
        }
    }

    private void validateCanAfford(final BigDecimal quantity) {
        TransactionCalculator calculator = createPurchaseCalculator(quantity);

        if (player.getMoney().compareTo(calculator.calculateTotal()) < 0) {
            throw new IllegalStateException(
                "You do not have enough money to complete this purchase."
            );
        }
    }

    private void updateView() {
        view.updateCurrentPrice(stock.getSalesPrice());
        view.updateMoney(player.getMoney());
        view.updatePriceChart(stock.getHistoricalPrices());
        view.updateWatchlistButton(player.isInWatchlist(stock.getSymbol()));

        BigDecimal ownedQuantity =
            player.getPortfolio().getQuantityOwned(stock);

        view.updateOwnedQuantity(ownedQuantity);

        if (!buyMode) {
            List<Share> shares = player.getPortfolio().getShares(stock.getSymbol());
            view.showSharesForSale(shares, this::updateOrderPreview);
        }

        updateOrderPreview();
    }

    /**
     * Interface for the trade view.
     * This allows the controller to interact with the view without depending on JavaFX,
     * this interface was discussed with AI for assistance in testing.
     */
    interface TradeViewPort {
        /**
         * Switch the order panel between buy and sell mode.
         *
         * @param isBuyMode true for buy mode, false for sell mode
         */
        void updateOrderPanelMode(boolean isBuyMode);

        /**
         * Limit the maximum selectable quantity in the UI.
         *
         * @param maxQuantity maximum allowed quantity
         */
        void setMaxQuantity(int maxQuantity);

        /**
         * Read the quantity currently entered by the user.
         *
         * @return the requested quantity, may be null if not set
         */
        BigDecimal getQuantity();

        /**
         * Return the share currently selected for selling.
         *
         * @return selected Share or null if none selected
         */
        Share getSelectedShare();

        /**
         * Show shares available for sale and register a callback invoked on selection change.
         *
         * @param shares list of shares to display
         * @param onSelectionChanged callback to invoke when selection changes
         */
        void showSharesForSale(List<Share> shares, Runnable onSelectionChanged);

        /**
         * Update the watchlist toggle to reflect whether the stock is in the player's watchlist.
         *
         * @param inWatchlist true if stock is in watchlist
         */
        void updateWatchlistButton(boolean inWatchlist);

        /**
         * Update the order preview panel with calculated numbers.
         *
         * @param gross      gross amount
         * @param commission commission cost
         * @param taxes      taxes applied
         * @param total      total amount
         */
        void updateOrderPreview(
            BigDecimal gross,
            BigDecimal commission,
            BigDecimal taxes,
            BigDecimal total);

        /**
         * Show an error message to the user.
         *
         * @param message error text
         */
        void showError(String message);

        /**
         * Update displayed current price.
         *
         * @param price current price
         */
        void updateCurrentPrice(BigDecimal price);

        /**
         * Update displayed player money.
         *
         * @param money player's available money
         */
        void updateMoney(BigDecimal money);

        /**
         * Update the price chart with a sequence of historical prices.
         *
         * @param prices historical prices (may be empty)
         */
        void updatePriceChart(List<BigDecimal> prices);

        /**
         * Update how many units the player owns of the selected stock.
         *
         * @param quantity owned quantity
         */
        void updateOwnedQuantity(BigDecimal quantity);

        /**
         * Show a transaction receipt in the UI.
         *
         * @param transaction the completed transaction to display
         */
        void showReceipt(Transaction transaction);
    }

    /**
     * Adapter for the trade view, implementing the TradeViewPort interface.
     */
    private record TradeViewAdapter(TradeView delegate) implements TradeViewPort {

        /**
         * Update the order panel mode (buy/sell).
         *
         * @param isBuyMode true for buy mode, false for sell mode
         */
        @Override
        public void updateOrderPanelMode(final boolean isBuyMode) {
            delegate.updateOrderPanelMode(isBuyMode);
        }

        /**
         * Set the maximum selectable quantity in the UI.
         *
         * @param maxQuantity maximum allowed quantity
         */
        @Override
        public void setMaxQuantity(final int maxQuantity) {
            delegate.setMaxQuantity(maxQuantity);
        }

        /**
         * Read the quantity currently entered by the user.
         *
         * @return the requested quantity, may be null if not set
         */
        @Override
        public BigDecimal getQuantity() {
            return delegate.getQuantity();
        }

        /**
         * Get the currently selected share.
         *
         * @return the selected Share or null if none selected
         */
        @Override
        public Share getSelectedShare() {
            return delegate.getSelectedShare();
        }

        /**
         * Show available shares for sale.
         *
         * @param shares            list of shares to display
         * @param onSelectionChanged callback to invoke when selection changes
         */
        @Override
        public void showSharesForSale(
                final List<Share> shares,
                final Runnable onSelectionChanged) {
            delegate.showSharesForSale(shares, onSelectionChanged);
        }

        /**
         * Update the watchlist button state.
         *
         * @param inWatchlist true if the share is in the watchlist, false otherwise
         */
        @Override
        public void updateWatchlistButton(final boolean inWatchlist) {
            delegate.updateWatchlistButton(inWatchlist);
        }

        /**
         * Update the order preview with the latest values.
         *
         * @param gross      gross amount
         * @param commission commission amount
         * @param taxes      taxes amount
         * @param total      total amount
         */
        @Override
        public void updateOrderPreview(
                final BigDecimal gross,
                final BigDecimal commission,
                final BigDecimal taxes,
                final BigDecimal total) {
            delegate.updateOrderPreview(gross, commission, taxes, total);
        }

        /**
         * Show an error message in the UI.
         *
         * @param message the error message to display
         */
        @Override
        public void showError(final String message) {
            delegate.showError(message);
        }

        /**
         * Update the current price display.
         *
         * @param price the current price to display
         */
        @Override
        public void updateCurrentPrice(final BigDecimal price) {
            delegate.updateCurrentPrice(price);
        }

        /**
         * Update the user's available money display.
         *
         * @param money the available money to display
         */
        @Override
        public void updateMoney(final BigDecimal money) {
            delegate.updateMoney(money);
        }

        /**
         * Update the price chart with the latest price data.
         *
         * @param prices the list of prices to display
         */
        @Override
        public void updatePriceChart(final List<BigDecimal> prices) {
            delegate.updatePriceChart(prices);
        }

        /**
         * Update the user's owned quantity display.
         *
         * @param quantity the owned quantity to display
         */
        @Override
        public void updateOwnedQuantity(final BigDecimal quantity) {
            delegate.updateOwnedQuantity(quantity);
        }

        /**
         * Show the receipt for a completed transaction.
         *
         * @param transaction the completed transaction
         */
        @Override
        public void showReceipt(final Transaction transaction) {
            delegate.showReceipt(transaction);
        }
    }
}
