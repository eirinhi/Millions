package no.ntnu.idatt2003.controller;
 
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.view.MainView;
import no.ntnu.idatt2003.view.PortfolioView;
 
/**
 * Controller for {@link PortfolioView}.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Create (or reuse) the view</li>
 *   <li>Populate it with model data on every {@link #refresh()}</li>
 *   <li>Record a new chart point on every week advance via {@link #recordWeek()}</li>
 *   <li>Wire up BUY / SELL button actions</li>
 *   <li>Pass the view to {@link MainView} for display</li>
 * </ul>
 */
public class PortfolioController {
 
    // --- Formatters ---
    private static final DecimalFormat MONEY_FMT;
    private static final DecimalFormat PCT_FMT;
 
    static {
        DecimalFormatSymbols nok = new DecimalFormatSymbols(new Locale("nb", "NO"));
        MONEY_FMT = new DecimalFormat("#,##0.00 NOK", nok);
        PCT_FMT   = new DecimalFormat("+0.00%;-0.00%", nok);
    }
 
    private final MainView mainView;
    private final Player player;
    private PortfolioView view;
    private final Exchange exchange;
 
    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public PortfolioController(MainView mainView, Player player, Exchange exchange) {
        this.mainView = mainView;
        this.player   = player;
        this.exchange = exchange;
    }
 
    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------
 
    /**
     * Creates (or reuses) the {@link PortfolioView}, refreshes its content,
     * and displays it in the center of the main layout.
     */
    public void showPortfolioView() {
        if (mainView == null) {
            throw new IllegalStateException("MainView not available");
        }
 
        if (this.view == null) {
            this.view = new PortfolioView();
            wireBuyAction();
            wireSellAction();
        }
 
        refresh();
        mainView.setView(this.view);
    }
 
    /**
     * Pulls fresh data from the model and updates labels, holdings,
     * and transactions. Does NOT touch the chart — chart points are
     * only added via {@link #recordWeek()}.
     */
    public void refresh() {
        if (view == null) return;
 
        BigDecimal money   = safe(player.getMoney());
        BigDecimal equity  = computeEquity();
        double perfPercent = computePerformancePercent();
 
        view.updateStats(
            formatPercent(perfPercent),
            formatMoney(equity),
            formatMoney(money),
            perfPercent >= 0
        );
        view.setHoldings(formatHoldings());
        view.setTransactions(formatTransactions());
    }
 
    /**
     * Appends a new data point to the portfolio growth chart.
     * Should be called by {@link MainViewController#advanceWeek()} each time
     * the week advances, never when the portfolio view is merely opened.
     */
    public void recordWeek() {
        if (view == null) return;
        BigDecimal money  = safe(player.getMoney());
        BigDecimal equity = computeEquity();
        view.addChartPoint(money.add(equity).doubleValue());
    }
 
    /**
     * Releases any resources held by this controller.
     */
    public void dispose() {
        // Unregister observers here if added later
    }
 
    // -----------------------------------------------------------------------
    // Private – button wiring
    // -----------------------------------------------------------------------
 
    private void wireBuyAction() {
        view.setBuyAction(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("BUY");
            ComboBox<String> symbolBox = new ComboBox<>();
            symbolBox.setPromptText("Select a stock");

            TextField quantityField = new TextField();
            quantityField.setPromptText("Enter quantity");

            Label errorLabel = new Label();

            VBox content = new VBox(10,
                new Label("Symbol:"), symbolBox,
                new Label("Quantity:"), quantityField,
                errorLabel  
            );
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(result -> {
                if (result != ButtonType.OK) return;
                try {
                    String symbol = symbolBox.getValue();
                    BigDecimal quantity = new BigDecimal(quantityField.getText().trim());
                    exchange.buy(symbol, quantity, player);
                    refresh();
                } catch (IllegalArgumentException | IllegalStateException e) {
                    errorLabel.setText("Wrong: " + e.getMessage());
                }
            });
        });
    }
 
    private void wireSellAction() {
        view.setSellAction(() -> {
            List<Share> shares = player.getPortfolio().getShares();

            if (shares.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "You do not own any shares.");
                alert.setTitle("Sell share");
                alert.showAndWait();
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("SELL");

            ComboBox<Share> shareBox = new ComboBox<>();
            shareBox.getItems().addAll(shares);

            shareBox.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Share share) {
                    if (share == null) return "";
                    return share.getStock().getSymbol()
                            + " x" + share.getQuantity()
                            + " (Price: " + share.getStock().getSalesPrice() + ")";
                }

                @Override
                public Share fromString(String string) {
                    return null;
            }
        });

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox content = new VBox(10,
            new Label("Choose share:"), shareBox, errorLabel);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
            try {
                Share selected = shareBox.getValue();
                if (selected == null) {
                    errorLabel.setText("Choose stock first.");
                    return;
                }
                exchange.sell(selected, player);
                refresh();

            } catch (IllegalArgumentException | IllegalStateException e) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Error: " + e.getMessage());
            }
        });
    });
}

    // -----------------------------------------------------------------------
    // Private – business logic
    // -----------------------------------------------------------------------
 
    private BigDecimal computeEquity() {
        try {
            return player.getPortfolio().getShares().stream()
                    .map(s -> safe(s.getStock().getSalesPrice()).multiply(s.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
 
    private double computePerformancePercent() {
        BigDecimal starting = player.getStartingMoney() == null
                ? BigDecimal.ZERO : player.getStartingMoney();
        BigDecimal current  = safe(player.getMoney()).add(computeEquity());
        if (starting.compareTo(BigDecimal.ZERO) <= 0) return 0.0;
        return current.subtract(starting)
                .multiply(new BigDecimal("100"))
                .divide(starting, 4, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }
 
    private List<String> formatHoldings() {
        try {
            return player.getPortfolio().getShares().stream()
                    .map(s -> s.getStock().getSymbol()
                            + " x" + s.getQuantity()
                            + " gave " + formatMoney(s.getStock().getSalesPrice()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
 
    private List<String> formatTransactions() {
        try {
            return player.getTransactionArchive().getAll().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
 
    // -----------------------------------------------------------------------
    // Private – formatting helpers
    // -----------------------------------------------------------------------
 
    private BigDecimal safe(BigDecimal v)    { return v == null ? BigDecimal.ZERO : v; }
    private String formatMoney(BigDecimal v) { return MONEY_FMT.format(v); }
    private String formatPercent(double p)   { return PCT_FMT.format(p / 100.0); }
}
 