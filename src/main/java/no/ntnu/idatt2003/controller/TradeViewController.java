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

    /** The trade view managed by this controller. */
    private final TradeView view;

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

        this.view = new TradeView(this, stock, player);

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
        return view;
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
}
