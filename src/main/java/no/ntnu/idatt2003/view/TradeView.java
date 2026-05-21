package no.ntnu.idatt2003.view;

import java.math.BigDecimal;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.entity.Transaction;
import no.ntnu.idatt2003.view.components.PriceChartComponent;
import no.ntnu.idatt2003.view.components.TransactionReceiptDialog;

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
    private static final int GAP = 16;

    /** Padding around the trade view. */
    private static final int PADDING = 24;

    /** Aspect ratio for the price chart (width / height). */
    private static final double CHART_RATIO = 1.8;

    /** Spacing between the buy and sell mode buttons. */
    private static final int BUTTON_SPACING = 12;

    /** Maximum allowed quantity for a buy order. */
    private static final int MAX_BUY_QUANTITY = 9999;

    /** Preferred and maximum width of the order panel column. */
    private static final int ORDER_PANEL_WIDTH = 300;

    /** The quantity spinner for entering trade amounts. */
    private final Spinner<Integer> quantitySpinner;

    /** Displays the gross transaction value. */
    private final Label grossLabel = new Label();

    /** Displays the commission fee. */
    private final Label commissionLabel = new Label();

    /** Displays the applicable taxes. */
    private final Label taxesLabel = new Label();

    /** Displays the total transaction value. */
    private final Label totalLabel = new Label();

    /** Displays the current trade mode (BUY or SELL). */
    private final Label modeLabel = new Label("BUY");

    /** Displays the current stock price. */
    private final Label priceLabel = new Label();

    /** Displays the player's current balance. */
    private final Label moneyValueLabel = new Label();

    /** Displays the quantity of shares already owned. */
    private final Label ownedQuantityLabel = new Label();

    /** The price chart component. */
    private final PriceChartComponent chart;

    /** The confirm trade button. */
    private final Button confirmButton = new Button("BUY");

    /** The order panel containing trade controls. */
    private final VBox orderPanel = new VBox(10);

    private final VBox buySection = new VBox(4);
    private final VBox sellSection = new VBox(8);
    private final ComboBox<Share> shareCombo = new ComboBox<>();
    private boolean buyMode = true;

    /**
     * Creates a new trade view for the selected stock.
     *
     * @param controller the controller handling trade actions
     * @param stock the stock displayed in the view
     * @param player the current player performing trades
     */
    public TradeView(
            final TradeViewController controller,
            final Stock stock,
            final Player player) {

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

        modeLabel.getStyleClass().add("trade-mode-buy");

        Button buyButton = new Button("BUY");
        Button sellButton = new Button("SELL");

        buyButton.getStyleClass().add("primary-btn");
        sellButton.getStyleClass().add("primary-btn");
        confirmButton.getStyleClass().add("primary-btn");

        buyButton.setOnAction(e -> {
            SoundPlayer.playClick();
            controller.setBuyMode();
        });
        sellButton.setOnAction(e -> {
            SoundPlayer.playClick();
            controller.setSellMode();
        });

        chart = new PriceChartComponent(stock.getHistoricalPrices());

        VBox chartSection = new VBox(GAP);
        chartSection.getStyleClass().add("trade-card");

        Button backButton = new Button("←");
        backButton.getStyleClass().add("back-btn");
        backButton.setOnAction(e -> {
            SoundPlayer.playClick();
            controller.cancelTrade();
        });

        HBox modeButtons = new HBox(BUTTON_SPACING, buyButton, sellButton);
        modeButtons.setAlignment(Pos.CENTER_RIGHT);

        chartSection.getChildren().addAll(
                backButton,
                symbol,
                title,
                priceLabel,
                modeButtons,
                chart
        );

        chartSection.widthProperty().addListener((obs, old, newVal) -> {
            Insets insets = chartSection.getInsets();
            double insetW = insets.getLeft() + insets.getRight();
            double w = newVal.doubleValue() - insetW;
            if (w > 0) {
                chart.setWidth(w);
                chart.setHeight(w / CHART_RATIO);
            }
        });

        quantitySpinner = new Spinner<>(1, MAX_BUY_QUANTITY, 1);
        quantitySpinner.valueProperty().addListener(
                (obs, oldVal, newVal) -> controller.updateOrderPreview()
        );

        confirmButton.setOnAction(e -> {
            if (buyMode) {
                int val = quantitySpinner.getValue();
                BigDecimal quantity = BigDecimal.valueOf(val);
                controller.executeTrade(quantity);
            } else {
                controller.executeSell();
            }
        });

        orderPanel.setPadding(new Insets(GAP));
        orderPanel.getStyleClass().add("trade-card");

        Label companyLabel = new Label(stock.getCompany());
        companyLabel.getStyleClass().add("trade-company");

        Label moneyTitle = new Label("Balance");
        moneyTitle.getStyleClass().add("trade-small-label");
        moneyValueLabel.setText(player.getMoney() + " NOK");
        moneyValueLabel.getStyleClass().add("trade-value-label");

        BigDecimal ownedQuantity =
            player.getPortfolio().getQuantityOwned(stock);
        ownedQuantityLabel.setText(
            "My quantity: " + ownedQuantity + " stocks"
        );
        ownedQuantityLabel.getStyleClass().add("trade-small-label");

        Label quantityTitle = new Label("Quantity");
        quantityTitle.getStyleClass().add("trade-small-label");
        buySection.getChildren().addAll(quantityTitle, quantitySpinner);

        shareCombo.setMaxWidth(Double.MAX_VALUE);
        shareCombo.setPromptText("Select a share ...");
        shareCombo.getStyleClass().add("share-combo");
        shareCombo.setCellFactory(lv ->new ListCell<>() {
            @Override
            protected void updateItem(final Share item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                    : item.getQuantity()
                    + (item.getQuantity().intValue() == 1 ? " stock" : " stocks")
                    + " - "
                    + item.getPurchasePrice()+ " /stock");
            }
        });
        shareCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(final Share item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select a share ..."
                    : item.getQuantity()
                    + (item.getQuantity().intValue() == 1 ? " stock" : " stocks")
                    + " - "
                    + item.getPurchasePrice() + " /stock");
            }
        });
        shareCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) SoundPlayer.playClick();
            controller.updateOrderPreview();
        });

        Label sellListLabel = new Label("Select a share to sell");
        sellListLabel.getStyleClass().add("trade-small-label");
        sellSection.getChildren().addAll(sellListLabel, shareCombo);
        sellSection.setVisible(false);
        sellSection.setManaged(false);

        Label grossTitle = new Label("Gross");
        grossTitle.getStyleClass().add("trade-small-label");

        Label commissionTitle = new Label("Commission");
        commissionTitle.getStyleClass().add("trade-small-label");

        Label taxesTitle = new Label("Taxes");
        taxesTitle.getStyleClass().add("trade-small-label");

        Label totalTitle = new Label("Total");
        totalTitle.getStyleClass().add("summary-label");

        grossLabel.getStyleClass().add("trade-row-value");
        commissionLabel.getStyleClass().add("trade-row-value");
        taxesLabel.getStyleClass().add("trade-row-value");
        totalLabel.getStyleClass().add("trade-total-label");

        confirmButton.setMaxWidth(Double.MAX_VALUE);
        HBox buttonBox = new HBox(confirmButton);
        HBox.setHgrow(confirmButton, Priority.ALWAYS);

        orderPanel.getChildren().addAll(
                modeLabel,
                companyLabel,
                moneyTitle,
                moneyValueLabel,
                ownedQuantityLabel,

                buySection,
                sellSection,

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

        setupGridConstraints();
        add(chartSection, 0, 0);
        add(orderPanel, 1, 0);
    }

    /**
     * Configures the two-column, one-row grid constraints for this view.
     */
    private void setupGridConstraints() {
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setPrefWidth(ORDER_PANEL_WIDTH);
        col1.setMaxWidth(ORDER_PANEL_WIDTH);
        getColumnConstraints().addAll(col0, col1);

        RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.ALWAYS);
        getRowConstraints().add(row0);
    }

    /**
     * Returns the quantity currently selected in the quantity spinner.
     *
     * @return the selected share quantity
     */
    public BigDecimal getQuantity() {
        return BigDecimal.valueOf(quantitySpinner.getValue());
    }

    public Share getSelectedShare() {
        return shareCombo.getValue();
    }

    /**
     * Updates the order panel to reflect buy or sell mode.
     *
     * @param isBuyMode true for buy mode, false for sell mode
     */
    public void updateOrderPanelMode(final boolean isBuyMode) {
        this.buyMode = isBuyMode;
        modeLabel.getStyleClass()
            .removeAll("trade-mode-buy", "trade-mode-sell");
        String modeClass = isBuyMode ? "trade-mode-buy" : "trade-mode-sell";
        modeLabel.getStyleClass().add(modeClass);
        modeLabel.setText(isBuyMode ? "BUY" : "SELL");
        confirmButton.setText(isBuyMode ? "BUY" : "SELL");

        buySection.setVisible(isBuyMode);
        buySection.setManaged(isBuyMode);
        sellSection.setVisible(!isBuyMode);
        sellSection.setManaged(!isBuyMode);
        shareCombo.setValue(null);
    }

    /**
     * Populates the combobox with shares available for selling.
     *
     * @param shares the list of shares available
     * @param onSelectionChanged callback to run when the selected share changes
     */
    public void showSharesForSale(
        final List<Share> shares,
        final Runnable onSelectionChanged) {
        shareCombo.getItems().setAll(shares);
        shareCombo.setValue(null);
    }

    /**
     * Updates the stock price displayed in the trade view.
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
        if (maxQuantity <= 0) {
            quantitySpinner.setValueFactory(
                new SpinnerValueFactory
                    .IntegerSpinnerValueFactory(0, 0, 0)
            );
            quantitySpinner.setDisable(true);
        } else {
            quantitySpinner.setValueFactory(
                    new SpinnerValueFactory
                            .IntegerSpinnerValueFactory(1, maxQuantity, 1)
            );
            quantitySpinner.setDisable(false);
        }
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
     * <p>Displays gross value, commission, taxes,
     * and total transaction value.</p>
     *
     * @param gross the gross transaction value
     * @param commission the commission fee
     * @param taxes the calculated taxes
     * @param total the total transaction value
     */
    public void updateOrderPreview(
            final BigDecimal gross,
            final BigDecimal commission,
            final BigDecimal taxes,
            final BigDecimal total) {

        grossLabel.setText(gross + " NOK");
        commissionLabel.setText(commission + " NOK");
        taxesLabel.setText(taxes + " NOK");
        totalLabel.setText(total + " NOK");
    }

    /**
     * Updates the displayed quantity of shares currently owned by the player.
     *
     * @param quantity the owned share quantity
     */
    public void updateOwnedQuantity(final BigDecimal quantity) {
        ownedQuantityLabel.setText(
                "My quantity: " + quantity + " stocks"
        );
    }

    /**
     * Displays an error dialog with the given message.
     *
     * @param message the error message to display
     */
    public void showError(final String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(getScene().getWindow());
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

    /**
     * Displays a receipt dialog for the given transaction.
     *
     * @param transaction the completed transaction to display
     */
    public void showReceipt(final Transaction transaction) {
        TransactionReceiptDialog dialog =
            new TransactionReceiptDialog(transaction);
        dialog.initOwner(getScene().getWindow());
        dialog.showAndWait();
    }
}
