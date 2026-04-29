package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;

/**
 * Builds and shows the BUY and SELL dialogs.
 *
 * <p>BUY: searchable stock list, live total-cost preview (0.5% commission).
 * <p>SELL: share picker with quantity spinner (1 to max owned),
 *          live revenue preview (1% commission, 30% tax on profit).
 *
 * TODO: fix so a player can sell shares they own, not only everything.
 */
class TradeDialogHelper {

    private final Exchange exchange;
    private final Player   player;
    private final Runnable onTradeComplete;

    TradeDialogHelper(Exchange exchange, Player player, Runnable onTradeComplete) {
        this.exchange        = exchange;
        this.player          = player;
        this.onTradeComplete = onTradeComplete;
    }

    /**
     * Displays the BUY dialog where the user can select a stock,
    * enter quantity, and preview total cost including commission.
     *
     * <p>On confirmation, executes a buy order through {@link Exchange}.
     */
    void showBuyDialog() {
        List<Stock> allStocks = exchange.getAllStocks();
        if (allStocks.isEmpty()) {
            info("Buy share", "No stocks available on the exchange.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Buy share");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or symbol…");

        // Stock dropdown
        ObservableList<Stock> filtered = FXCollections.observableArrayList(allStocks);
        ComboBox<Stock> stockBox = new ComboBox<>(filtered);
        stockBox.setPromptText("Select a stock");
        stockBox.setMaxWidth(Double.MAX_VALUE);
        stockBox.setConverter(stockConverter());
        stockBox.setCellFactory(lv -> stockCell());

        // Live filter as user types
        searchField.textProperty().addListener((obs, old, query) -> {
            String q = query.trim().toLowerCase();
            filtered.setAll(
                allStocks.stream()
                    .filter(s -> s.getSymbol().toLowerCase().contains(q)
                              || s.getCompany().toLowerCase().contains(q))
                    .collect(Collectors.toList())
            );
            if (!filtered.contains(stockBox.getValue())) {
                stockBox.setValue(null);
            }
        });

        TextField qtyField = new TextField();
        qtyField.setPromptText("e.g. 10");

        // Info label (.muted-label CSS)
        Label infoLabel = new Label();
        infoLabel.getStyleClass().add("muted-label");

        // Total cost label
        Label totalLabel = new Label("Total cost: –");
        totalLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");

        // Update cost preview live
        Runnable updateTotal = () -> {
            Stock stock   = stockBox.getValue();
            String qtyTxt = qtyField.getText().trim();
            if (stock == null || qtyTxt.isBlank()) {
                infoLabel.setText("");
                totalLabel.setText("Total cost: –");
                return;
            }
            try {
                BigDecimal qty        = new BigDecimal(qtyTxt);
                BigDecimal price      = stock.getSalesPrice();
                BigDecimal gross      = price.multiply(qty);
                BigDecimal commission = gross.multiply(new BigDecimal("0.005"))
                                            .setScale(2, RoundingMode.HALF_UP);
                BigDecimal total      = gross.add(commission)
                                            .setScale(2, RoundingMode.HALF_UP);
                infoLabel.setText(
                    "Price per share: " + fmt(price)
                    + "   Commission (0.5%): " + fmt(commission)
                );
                totalLabel.setText("Total cost: " + fmt(total));
            } catch (NumberFormatException e) {
                infoLabel.setText("");
                totalLabel.setText("Total cost: –");
            }
        };

        stockBox.valueProperty().addListener((obs, o, n) -> updateTotal.run());
        qtyField.textProperty().addListener((obs, o, n) -> updateTotal.run());

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        VBox content = new VBox(10,
            styledLabel("Search:"),   searchField,
            styledLabel("Stock:"),    stockBox,
            styledLabel("Quantity:"), qtyField,
            infoLabel,
            totalLabel,
            errorLabel
        );
        content.setPrefWidth(380);
        content.setPadding(new Insets(8));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        styleDialogButtons(dialog);

        // Validate before closing
        Node okBtn = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(ActionEvent.ACTION, event -> {
            String err = validateBuy(stockBox.getValue(), qtyField.getText());
            if (err != null) { errorLabel.setText(err); event.consume(); }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
            try {
                exchange.buy(
                    stockBox.getValue().getSymbol(),
                    new BigDecimal(qtyField.getText().trim()),
                    player
                );
                onTradeComplete.run();
            } catch (IllegalArgumentException | IllegalStateException e) {
                error("Purchase failed", e.getMessage());
            }
        });
    }

    /**
     * Displays the SELL dialog where the user can select owned shares,
     * choose quantity, and preview expected revenue, commission, and tax.
     *
     * <p>On confirmation, executes a sell order through {@link Exchange}.
     */
    void showSellDialog() {
        List<Share> shares = player.getPortfolio().getShares();
        if (shares.isEmpty()) {
            info("Sell share", "You do not own any shares.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sell share");

        // Share dropdown
        ComboBox<Share> shareBox = new ComboBox<>();
        shareBox.getItems().addAll(shares);
        shareBox.setPromptText("Choose a share");
        shareBox.setMaxWidth(Double.MAX_VALUE);
        shareBox.setConverter(shareConverter());
        shareBox.setCellFactory(lv -> shareCell());

        Spinner<Integer> qtySpinner = new Spinner<>();
        qtySpinner.setEditable(true);
        qtySpinner.setDisable(true);
        qtySpinner.setMaxWidth(Double.MAX_VALUE);

        Label infoLabel = new Label();
        infoLabel.getStyleClass().add("muted-label");

        // Total revenue label
        Label totalLabel = new Label("You receive: –");
        totalLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");

        shareBox.valueProperty().addListener((obs, old, share) -> {
            if (share == null) {
                qtySpinner.setDisable(true);
                infoLabel.setText("");
                totalLabel.setText("You receive: –");
                return;
            }
            int max = share.getQuantity().intValue();
            qtySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, max, 1));
            qtySpinner.setDisable(false);
            updateSellPreview(share, 1, infoLabel, totalLabel);
        });

        qtySpinner.valueProperty().addListener((obs, old, qty) -> {
            Share share = shareBox.getValue();
            if (share != null && qty != null) {
                updateSellPreview(share, qty, infoLabel, totalLabel);
            }
        });

        // Also react when user types directly in the spinner editor
        qtySpinner.getEditor().textProperty().addListener((obs, old, text) -> {
            Share share = shareBox.getValue();
            if (share == null) return;
            try {
                int qty = Integer.parseInt(text.trim());
                int max = share.getQuantity().intValue();
                if (qty >= 1 && qty <= max) {
                    updateSellPreview(share, qty, infoLabel, totalLabel);
                }
            } catch (NumberFormatException ignored) {}
        });

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        VBox content = new VBox(10,
            styledLabel("Share:"),    shareBox,
            styledLabel("Quantity:"), qtySpinner,
            infoLabel,
            totalLabel,
            errorLabel
        );
        content.setPrefWidth(380);
        content.setPadding(new Insets(8));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        styleDialogButtons(dialog);

        Node okBtn = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(ActionEvent.ACTION, event -> {
            String err = validateSell(shareBox.getValue(), qtySpinner);
            if (err != null) { errorLabel.setText(err); event.consume(); }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
            try {
                Share selected = shareBox.getValue();
                int qty = Integer.parseInt(qtySpinner.getEditor().getText().trim());
                exchange.sell(partialShare(selected, qty), player);
                onTradeComplete.run();
            } catch (IllegalArgumentException | IllegalStateException e) {
                error("Sale failed", e.getMessage());
            }
        });
    }

    /**
     * Updates the live preview for a sell transaction.
    *
    * <p>Calculates gross value, commission, profit, tax, and final payout
     * based on selected share and quantity.
    */
    private void updateSellPreview(Share share, int qty,
                                   Label infoLabel, Label totalLabel) {
        BigDecimal salePrice     = safe(share.getStock().getSalesPrice());
        BigDecimal purchasePrice = safe(share.getPurchasePrice());
        BigDecimal quantity      = BigDecimal.valueOf(qty);

        BigDecimal gross      = salePrice.multiply(quantity)
                                         .setScale(2, RoundingMode.HALF_UP);
        BigDecimal commission = gross.multiply(new BigDecimal("0.01"))
                                    .setScale(2, RoundingMode.HALF_UP);
        BigDecimal profit     = gross.subtract(commission)
                                    .subtract(purchasePrice.multiply(quantity));
        BigDecimal tax        = profit.compareTo(BigDecimal.ZERO) > 0
            ? profit.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        BigDecimal total      = gross.subtract(commission).subtract(tax)
                                    .setScale(2, RoundingMode.HALF_UP);

        infoLabel.setText(
            "Gross: " + fmt(gross)
            + "  Commission (1%): " + fmt(commission)
            + "  Tax: " + fmt(tax)
        );
        totalLabel.setText("You receive: " + fmt(total));
    }

    /**
     * Creates a partial share from the original share with the specified quantity.
     *
     * @param original the original share
     * @param qty the quantity for the partial share
     * @return a new Share instance representing the partial share
     */
    private Share partialShare(Share original, int qty) {
        if (original.getQuantity().intValue() == qty) return original;
        return new Share(
            original.getStock(),
            BigDecimal.valueOf(qty),
            original.getPurchasePrice()
        );
    }

    /**
     * Validates the input for a buy transaction.
     *
     * @param stock the selected stock
     * @param qtyText the quantity text input
     * @return an error message if validation fails, or null if it succeeds
     */
    private String validateBuy(Stock stock, String qtyText) {
        if (stock == null) return "Please select a stock.";
        if (qtyText == null || qtyText.isBlank()) return "Please enter a quantity.";
        try {
            BigDecimal qty = new BigDecimal(qtyText.trim());
            if (qty.compareTo(BigDecimal.ZERO) <= 0)
                return "Quantity must be greater than zero.";
        } catch (NumberFormatException e) {
            return "Quantity must be a valid number.";
        }
        return null;
    }

    /**
     * Validates the input for a sell transaction.
     *
     * @param share the selected share
     * @param spinner the quantity spinner
     * @return an error message if validation fails, or null if it succeeds
     */
    private String validateSell(Share share, Spinner<Integer> spinner) {
        if (share == null) return "Please choose a share to sell.";
        try {
            int qty = Integer.parseInt(spinner.getEditor().getText().trim());
            int max = share.getQuantity().intValue();
            if (qty < 1)   return "Quantity must be at least 1.";
            if (qty > max) return "You only own " + max + " of this share.";
        } catch (NumberFormatException e) {
            return "Quantity must be a valid number.";
        }
        return null;
    }

    /**
     * Creates a StringConverter for Stock objects.
     *
     * @return a StringConverter for Stock objects
     */
    private StringConverter<Stock> stockConverter() {
        return new StringConverter<>() {
            @Override public String toString(Stock s) {
                if (s == null) return "";
                return s.getSymbol() + " – " + s.getCompany()
                        + "  (" + fmt(s.getSalesPrice()) + ")";
            }
            @Override public Stock fromString(String str) { return null; }
        };
    }

    /**
     * Creates a ListCell for displaying stock information in dropdown menus.
     *
     * @return a ListCell for Stock objects
     */
    private ListCell<Stock> stockCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Stock s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setGraphic(null); return; }
                setText(s.getSymbol() + " – " + s.getCompany()
                        + "  (" + fmt(s.getSalesPrice()) + ")");
            }
        };
    }

    /**
     * Converts Share into readable string format for ComboBox display.
     * @return a StringConverter for Share objects
     */
    private StringConverter<Share> shareConverter() {
        return new StringConverter<>() {
            @Override public String toString(Share sh) {
                if (sh == null) return "";
                return sh.getStock().getSymbol()
                        + " – " + sh.getStock().getCompany()
                        + "  (owns: " + sh.getQuantity().intValue() + ")";
            }
            @Override public Share fromString(String str) { return null; }
        };
    }

    /**
     * Creates a custom ListCell for displaying owned shares with quantity and pricing.
     * @return a ListCell for Share objects
     */
    private ListCell<Share> shareCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Share sh, boolean empty) {
                super.updateItem(sh, empty);
                if (empty || sh == null) { setText(null); setGraphic(null); return; }
                Label main = new Label(sh.getStock().getSymbol()
                        + " – " + sh.getStock().getCompany());
                main.setStyle("-fx-font-weight:bold;");
                Label sub = new Label(
                    "Owns: " + sh.getQuantity().intValue()
                    + "   Bought at: " + fmt(sh.getPurchasePrice())
                    + "   Now: " + fmt(sh.getStock().getSalesPrice())
                );
                sub.getStyleClass().add("muted-label");
                setGraphic(new VBox(2, main, sub));
                setText(null);
            }
        };
    }

    /**
     * Creates a label using the global .label CSS style (16px from stylesheet).
     */
    private Label styledLabel(String text) {
        return new Label(text);
    }

    /**
     * Applies the .button CSS class to OK and Cancel buttons in the dialog.
     * The Cancel button also gets .danger-btn for visual distinction.
     */
    private void styleDialogButtons(Dialog<?> dialog) {
        dialog.getDialogPane().lookupButton(ButtonType.OK)
              .getStyleClass().add("primary-btn");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL)
              .getStyleClass().add("danger-btn");
    }

    /**
     * Safely retrieves a BigDecimal value, defaulting to BigDecimal.ZERO if null.
     *  
     * @param v the BigDecimal value to check
     * @return the safe BigDecimal value
     */
    private BigDecimal safe(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    /**
     * Formats a BigDecimal value into a readable NOK currency string.
     * 
     * @param v the BigDecimal value to format
     * @return the formatted string
     */
    private String fmt(BigDecimal v) {
        return v == null ? "–" : String.format("%,.2f NOK", v);
    }

    /**
     * Displays an informational alert dialog.
     * 
     * @param title the title of the dialog
     * @param msg the message to display
     */
    private void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }

    /**
     * Displays an error alert dialog.
     * @param title the title of the dialog
     * @param msg the message to display
     */
    private void error(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}