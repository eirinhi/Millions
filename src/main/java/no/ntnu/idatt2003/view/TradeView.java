package no.ntnu.idatt2003.view;

import java.math.BigDecimal;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.TradeViewController;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.view.components.PriceChartComponent;

/**
 * View for trading a selected stock.
 *
 * <p>Displays stock information, price history,
 * transaction preview, and controls for buying
 * and selling shares.</p>
 *
 * <p>The view communicates user actions to
 * {@link TradeViewController} and updates dynamically
 * based on the current trade mode and portfolio state.</p>
 */
public class TradeView extends GridPane {

    /** Gap between grid cells. */
    private static final int GAP = 32;

    /** Padding around the trade view. */
    private static final int PADDING = 24;

    private final Spinner<Integer> quantitySpinner;

    private final Label grossLabel = new Label();
    private final Label commissionLabel = new Label();
    private final Label taxesLabel = new Label();
    private final Label totalLabel = new Label();

    private final Label modeLabel = new Label("BUY MODE");
    private final Label priceLabel = new Label();
    private final Label moneyValueLabel = new Label();
    private final Label ownedQuantityLabel = new Label();
    private final PriceChartComponent chart;

    private final Button confirmButton = new Button("BUY");

    /**
     * Creates a new trade view for the selected stock.
     *
     * @param controller the controller handling trade actions
     * @param stock the stock displayed in the view
     * @param player the current player performing trades
     */
    public TradeView(
            TradeViewController controller,
            Stock stock,
            Player player) {

        setHgap(GAP);
        setVgap(GAP);
        setPadding(new Insets(PADDING));
        getStyleClass().add("trade-view");

        Label title = new Label(stock.getCompany());
        title.getStyleClass().add("trade-title");

        Label symbol = new Label(stock.getSymbol());
        symbol.getStyleClass().add("trade-symbol");

        priceLabel.setText(stock.getSalesPrice() + " NOK");
        priceLabel.getStyleClass().add("trade-price");

        modeLabel.getStyleClass().add("trade-mode-label");

        Button buyButton = new Button("BUY");
        Button sellButton = new Button("SELL");

        buyButton.getStyleClass().add("primary-btn");
        sellButton.getStyleClass().add("primary-btn");
        confirmButton.getStyleClass().add("primary-btn");

        buyButton.setOnAction(e -> controller.setBuyMode());
        sellButton.setOnAction(e -> controller.setSellMode());

        chart = new PriceChartComponent(stock.getHistoricalPrices());

        VBox chartSection = new VBox(16);
        chartSection.getStyleClass().add("trade-card");

        chartSection.getChildren().addAll(
                title,
                symbol,
                modeLabel,
                priceLabel,
                new HBox(12, buyButton, sellButton),
                chart
        );

        quantitySpinner = new Spinner<>(1, 9999, 1);
        quantitySpinner.valueProperty().addListener(
                (obs, oldVal, newVal) -> controller.updateOrderPreview()
        );

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("primary-btn");
        cancelButton.setOnAction(e -> controller.cancelTrade());

        confirmButton.setOnAction(e -> {
            BigDecimal quantity = BigDecimal.valueOf(quantitySpinner.getValue());
            controller.executeTrade(quantity);
        });

        VBox orderPanel = new VBox(10);
        orderPanel.setPadding(new Insets(16));
        orderPanel.getStyleClass().add("trade-card");

        Label moneyTitle = new Label("Money");
        moneyTitle.getStyleClass().add("trade-small-label");

        moneyValueLabel.setText(player.getMoney() + " NOK");
        moneyValueLabel.getStyleClass().add("trade-value-label");

        BigDecimal ownedQuantity =
                player.getPortfolio().getQuantityOwned(stock);

        ownedQuantityLabel.setText(
                "My quantity: " + ownedQuantity + " shares"
        );
        ownedQuantityLabel.getStyleClass().add("trade-small-label");

        Label quantityTitle = new Label("Quantity");
        quantityTitle.getStyleClass().add("trade-small-label");

        Label grossTitle = new Label("Gross");
        grossTitle.getStyleClass().add("trade-small-label");

        Label commissionTitle = new Label("Commission");
        commissionTitle.getStyleClass().add("trade-small-label");

        Label taxesTitle = new Label("Taxes");
        taxesTitle.getStyleClass().add("trade-small-label");

        Label totalTitle = new Label("Total");
        totalTitle.getStyleClass().add("summary-label");

        grossLabel.getStyleClass().add("trade-small-label");
        commissionLabel.getStyleClass().add("trade-small-label");
        taxesLabel.getStyleClass().add("trade-small-label");

        totalLabel.getStyleClass().add("trade-total-label");

        HBox buttonBox = new HBox(8, cancelButton, confirmButton);

        orderPanel.getChildren().addAll(
                new Label(stock.getCompany()),
                moneyTitle,
                moneyValueLabel,
                ownedQuantityLabel,

                quantityTitle,
                quantitySpinner,

                grossTitle,
                grossLabel,

                commissionTitle,
                commissionLabel,

                taxesTitle,
                taxesLabel,

                totalTitle,
                totalLabel,

                buttonBox
        );

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        getColumnConstraints().addAll(col0, col1);

        RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.ALWAYS);
        getRowConstraints().add(row0);

        add(chartSection, 0, 0);
        add(orderPanel, 1, 0);
    }

    /**
     * Returns the quantity currently selected in the quantity spinner.
     *
     * @return the selected share quantity
     */
    public BigDecimal getQuantity() {
        return BigDecimal.valueOf(quantitySpinner.getValue());
    }

    /**
     * Updates the mode label in the trade view.
     *
     * @param text the new mode label text
     */
    public void updateModeLabel(final String text) {
        modeLabel.setText(text);
    }

    /**
     * Updates the stock price displayed in the trade view
     *
     * @param price price the current stock price
     */
    public void updateCurrentPrice(final BigDecimal price) {
        priceLabel.setText(price + " NOK");
    }

    /**
     * Updates the maximum selectable quantity in the quantity spinner.
     *
     * @param maxQuantity the maximum allowed quantity
     */
    public void setMaxQuantity(final int maxQuantity) {
        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1,
                        Math.max(1, maxQuantity),
                        1
                )
        );
    }

    /**
     * Updates the displayed player balance.
     *
     * @param money the player's current money balance
     */
    public void updateMoney(final BigDecimal money) {
        moneyValueLabel.setText(money + " NOK");
    }

    /**
     * Updates the transaction preview section.
     *
     * <p>Displays calculated gross value, commission, taxes and total transaction value.</p>
     *
     * @param gross the gross transaction value
     * @param commission the commission fee
     * @param taxes the calculated taxes
     * @param total the total transaction value
     */
    public void updateOrderPreview(
            BigDecimal gross,
            BigDecimal commission,
            BigDecimal taxes,
            BigDecimal total) {

        grossLabel.setText(gross + " NOK");
        commissionLabel.setText(commission + " NOK");
        taxesLabel.setText(taxes + " NOK");
        totalLabel.setText(total + " NOK");
    }

    /**
     * Updates the trade button text based on the current mode.
     *
     * @param buyMode {@code true} if the current mode is buy, {@code false} if sell
     */
    public void updateTradeButtonText(final boolean buyMode) {
        confirmButton.setText(buyMode ? "BUY" : "SELL");
    }

    /**
     * Updates the displayed quantity of shares currently owned by the player.
     *
     * @param quantity the owned share quantity
     */
    public void updateOwnedQuantity(final BigDecimal quantity) {
        ownedQuantityLabel.setText(
                "My quantity: " + quantity + " shares"
        );
    }

    /**
     * Displays an error dialog with the given message.
     *
     * @param message the error message to display
     */ 
    public void showError(final String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Trade error");
        alert.setHeaderText("Could not complete trade");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Updates the displayed price chart.
     *
     * @param prices the updated historical stock prices
     */
    public void updatePriceChart(final List<BigDecimal> prices) {
        chart.updatePrices(prices);
    }
}
