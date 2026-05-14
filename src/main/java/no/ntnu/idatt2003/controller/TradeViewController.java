package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;

import javafx.scene.control.Alert;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.model.logic.PurchaseCalculator;
import no.ntnu.idatt2003.model.logic.SaleCalculator;
import no.ntnu.idatt2003.model.logic.TransactionCalculator;
import no.ntnu.idatt2003.view.TradeView;

/**
 * /**
 * Controller for the trade view.
 *
 * <p>Handles stock purchases and sales for a selected stock.
 * The controller validates trades, calculates transaction previews,
 * updates the trade view, and coordinates communication between
 * the model ({@link Exchange}, {@link Player}, {@link Stock})
 * and the {@link TradeView}.</p>
 *
 * <p>The controller supports both buy and sell modes,
 * including validation of available funds and owned shares.</p>
 */
public class TradeViewController {

    private final Exchange exchange;
    private final Player player;
    private final Stock stock;
    private final TradeView view;
    private final Runnable onTradeCompleted;

    private boolean buyMode = true;

    /**
     * Creates a new TradeViewController for the selected stock.
     *
     * @param exchange        the exchange to use for trading
     * @param player          the player making the trades
     * @param stock           the stock to trade
     * @param onTradeCompleted a callback to invoke when a trade is completed
     */
    public TradeViewController(
            final Exchange exchange,
            final Player player,
            final Stock stock,
            final Runnable onTradeCompleted) {

        this.exchange = exchange;
        this.player = player;
        this.stock = stock;
        this.onTradeCompleted = onTradeCompleted;

        this.view = new TradeView(this, stock, player);

        setBuyMode();
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
     * Switches the trade view into buy mode.
     *
     * <p>Updates the UI to display purchase-related information and enables
     * unrestricted quantity selection.</p>
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
     *
     * <p>Updates the UI to display sale-related information and limits
     * the maximum quantity selection to the amount owned by the player.</p>
     */
    public void setSellMode() {
        buyMode = false;
        view.updateTradeButtonText(false);
        view.updateModeLabel("Sell this share");

        BigDecimal ownedQuantity = player.getPortfolio().getQuantityOwned(stock);

        if (ownedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            view.setMaxQuantity(1);
            showError("You do not own this stock.");
        } else {
            view.setMaxQuantity(ownedQuantity.intValue());
        }

        updateOrderPreview();
    }

    /**
     * Updates the transaction preview displayed in the trade view.
     *
     * <p>Calculates gross value, commission, tax, and total transaction value
     * using appropriate {@link TransactionCalculator} implementation.</p>
     *
     * <p>If the transaction cannot be calculated all preview values are reset to zero.</p>
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
     * <p>The method validates the request quantity and ensures that the player either has enough
     * money to buy shares or own enough shares to sell.</p>
     * 
     * <p>After a successful transaction, the main view and trade view are refreshed.</p>
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
            showError(e.getMessage());
        }
    }

    /**
     * Creates a transaction calculator for a stock purchase.
     *
     * @param quantity the quantity of shares to purchase
     * @return a transaction calculator for the purchase
     */
    private TransactionCalculator createPurchaseCalculator(
            final BigDecimal quantity) {

        Share share = new Share(
                stock,
                quantity,
                stock.getSalesPrice()
        );

        return new PurchaseCalculator(share);
    }

    /**
     * Creates a calculator for previewing a sale transaction.
     *
     * @param quantity the quantity of shares to sell
     * @return a transaction calculator for the sale
     * @throws IllegalStateException if the player does not own the stock or tries to sell more than owned
     */
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

    /**
     * Validates the requested trade quantity is positive.
     *
     * @param quantity the quantity of shares to trade
     * @throws IllegalArgumentException if the quantity is null or negative
     */
    private void validateQuantity(final BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }
    }

    /**
     * Validates that the player can afford the requested purchase.
     *
     * @param quantity the quantity of shares to purchase
     * @throws IllegalStateException if the player does not have enough money to complete the purchase
     */
    private void validateCanAfford(final BigDecimal quantity) {
        TransactionCalculator calculator = createPurchaseCalculator(quantity);

        if (player.getMoney().compareTo(calculator.calculateTotal()) < 0) {
            throw new IllegalStateException(
                    "You do not have enough money to complete this purchase."
            );
        }
    }

    /**
     * Validates that the player owns enough shares to complete the sale.
     *
     * @param quantity the quantity of shares to sell
     * @throws IllegalStateException if the player does not own the stock or tries to sell more than owned
     */
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

    /**
     * Updates the view with the current stock, portfolio and player information.
     */
    private void updateView() {
        view.updateCurrentPrice(stock.getSalesPrice());
        view.updateMoney(player.getMoney());

        BigDecimal ownedQuantity =
                player.getPortfolio().getQuantityOwned(stock);

        view.updateOwnedQuantity(ownedQuantity);

        if (!buyMode) {
            view.setMaxQuantity(Math.max(1, ownedQuantity.intValue()));
        }

        updateOrderPreview();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to display
     */
    private void showError(final String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Trade error");
        alert.setHeaderText("Could not complete trade");
        alert.setContentText(message);
        alert.showAndWait();
    }
}