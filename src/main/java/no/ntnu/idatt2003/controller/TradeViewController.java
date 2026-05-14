package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.model.logic.PurchaseCalculator;
import no.ntnu.idatt2003.model.logic.SaleCalculator;
import no.ntnu.idatt2003.model.logic.TransactionCalculator;
import no.ntnu.idatt2003.model.observer.Observer;
import no.ntnu.idatt2003.view.TradeView;

/**
 * Controller for the trade view.
 *
 * <p>Handles stock purchases and sales for a selected stock.
 * The controller validates trades, calculates transaction previews,
 * updates the trade view, and observes the exchange for price changes.</p>
 */
public class TradeViewController implements Observer {

    private final Exchange exchange;
    private final Player player;
    private final Stock stock;
    private final TradeView view;
    private final Runnable onTradeCompleted;
    private final Runnable onCancel;

    private boolean buyMode = true;

    /**
     * Creates a new trade view controller for the selected stock.
     *
     * @param exchange the exchange used to execute trades
     * @param player the current player performing trades
     * @param stock the stock currently displayed in the trade view
     * @param onTradeCompleted callback executed after a successful trade
     * @param onCancel callback executed when the trade view is cancelled
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
     *
     * <p>This is called through the Observer pattern when stock prices
     * are updated by the exchange.</p>
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
        view.updateTradeButtonText(true);
        view.updateModeLabel("Buy this share");
        view.setMaxQuantity(9999);
        updateOrderPreview();
    }

    /**
     * Switches the trade view into sell mode.
     */
    public void setSellMode() {
        buyMode = false;
        view.updateTradeButtonText(false);
        view.updateModeLabel("Sell this share");

        BigDecimal ownedQuantity = player.getPortfolio().getQuantityOwned(stock);

        if (ownedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            view.setMaxQuantity(1);
            view.showError("You do not own this stock.");
        } else {
            view.setMaxQuantity(ownedQuantity.intValue());
        }

        updateOrderPreview();
    }

    /**
     * Cancels the trade view and returns to the previous view.
     */
    public void cancelTrade() {
        exchange.detach(this);
        onCancel.run();
    }

    /**
     * Updates the transaction preview displayed in the trade view.
     */
    public void updateOrderPreview() {
        BigDecimal quantity = view.getQuantity();

        try {
            TransactionCalculator calculator = buyMode
                    ? createPurchaseCalculator(quantity)
                    : createSaleCalculator(quantity);

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
    }

    /**
     * Executes a stock purchase or sale transaction.
     *
     * @param quantity the quantity of shares to trade
     */
    public void executeTrade(final BigDecimal quantity) {
        try {
            validateQuantity(quantity);

            if (buyMode) {
                validateCanAfford(quantity);
                exchange.buy(stock.getSymbol(), quantity, player);
            } else {
                validateCanSell(quantity);

                Share ownedShare = player.getPortfolio().getOwnedShare(stock);

                Share shareToSell = new Share(
                        stock,
                        quantity,
                        ownedShare.getPurchasePrice()
                );

                exchange.sell(shareToSell, player);
            }

            onTradeCompleted.run();
            updateView();

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

    private TransactionCalculator createSaleCalculator(
            final BigDecimal quantity) {

        Share ownedShare = player.getPortfolio().getOwnedShare(stock);

        if (ownedShare == null) {
            throw new IllegalStateException("You do not own this stock.");
        }

        Share share = new Share(
                stock,
                quantity,
                ownedShare.getPurchasePrice()
        );

        return new SaleCalculator(share);
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

    private void validateCanSell(final BigDecimal quantity) {
        BigDecimal ownedQuantity = player.getPortfolio().getQuantityOwned(stock);

        if (ownedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("You do not own this stock.");
        }

        if (quantity.compareTo(ownedQuantity) > 0) {
            throw new IllegalStateException(
                    "You cannot sell more shares than you own."
            );
        }
    }

    private void updateView() {
        view.updateCurrentPrice(stock.getSalesPrice());
        view.updateMoney(player.getMoney());
        view.updatePriceChart(stock.getHistoricalPrices());

        BigDecimal ownedQuantity =
                player.getPortfolio().getQuantityOwned(stock);

        view.updateOwnedQuantity(ownedQuantity);

        if (!buyMode) {
            view.setMaxQuantity(Math.max(1, ownedQuantity.intValue()));
        }

        updateOrderPreview();
    }
}
