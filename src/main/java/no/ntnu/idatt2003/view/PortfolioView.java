package no.ntnu.idatt2003.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.PortfolioFormatter.HoldingRow;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;
import no.ntnu.idatt2003.view.components.HoldingsTableComponent;
import no.ntnu.idatt2003.view.components.PriceChartComponent;
import no.ntnu.idatt2003.view.components.TransactionReceiptDialog;
import no.ntnu.idatt2003.view.components.TransactionsPanelComponent;

/**
 * View representing the users portfolio screen.
 *
 * <p>This view is responsible for displaying the entire portfolio UI,
 * including:
 * <ul>
 *     <li>Summary statistics (performance, equity, cash balance)</li>
 *     <li>A line chart showing portfolio growth over time</li>
 *     <li>A table of owned shares (holdings)</li>
 *     <li>A transaction history panel</li>
 * </ul>
 *
 * <p>The class contains only UI logic. All business logic and data
 * manipulation is handled externally (controller/services).
 *
 * <p>Trade actions (buy/sell) are handled by the dedicated TradeView,
 * not from this view.
 */
public class PortfolioView extends VBox {

    /** Aspect ratio for the portfolio growth chart (width / height). */
    private static final double CHART_RATIO = 1.8;

    /** Maximum height of the scrollable holdings area in pixels. */
    private static final double HOLDINGS_SCROLL_HEIGHT = 200;

    /** Outer spacing between child nodes. */
    private static final int VIEW_SPACING = 16;

    /** Outer padding around the view. */
    private static final int VIEW_PADDING = 20;

    /** Spacing between stat boxes in the summary bar. */
    private static final int BAR_SPACING = 40;

    /** Vertical padding inside the summary bar. */
    private static final int BAR_PAD_V = 12;

    /** Horizontal padding inside the summary bar. */
    private static final int BAR_PAD_H = 16;

    /** Spacing between title and value inside a stat box. */
    private static final int STAT_SPACING = 4;

    /** Spacing between chart and holdings in the left column. */
    private static final int LEFT_SPACING = 16;

    /** Spacing between left column and transactions panel. */
    private static final int CENTER_SPACING = 16;

    /** Label showing portfolio performance percentage. */
    private final Label performanceValue = new Label("--");

    /** Label showing total portfolio equity. */
    private final Label equityValue = new Label("-- $");

    /** Label showing available cash balance. */
    private final Label moneyValue = new Label("-- $");

    /** Historical net worth data points for the growth chart. */
    private final List<BigDecimal> chartData = new ArrayList<>();

    /** Chart displaying portfolio growth over time. */
    private final PriceChartComponent chart =
        new PriceChartComponent(chartData);

    /** Table showing current share holdings. */
    private final HoldingsTableComponent holdingsTable;

    /** Scrollable panel showing transaction history. */
    private final TransactionsPanelComponent transactionsPanel;

    /**
     * Constructs the portfolio view and initializes all UI components.
     */
    public PortfolioView(final Consumer<String> onStockSelected) {
        setSpacing(VIEW_SPACING);
        setPadding(new Insets(VIEW_PADDING));

        holdingsTable     = new HoldingsTableComponent(onStockSelected);
        transactionsPanel = new TransactionsPanelComponent();
        transactionsPanel.setOnReceiptClick(r -> {
            TransactionReceiptDialog dialog = new TransactionReceiptDialog(r);
            dialog.initOwner(getScene().getWindow());
            dialog.showAndWait();
        });

        getChildren().addAll(
            buildSummaryBar(),
            buildCenterRow()
        );
    }

    /**
     * Updates the three summary statistics shown at the top of the view.
     *
     * @param performance formatted string, e.g. "+3,50%"
     * @param equity      formatted total equity value
     * @param money       formatted cash balance
     * @param positive    true if performance is positive, false otherwise
     */
    public void updateStats(
            final String performance,
            final String equity,
            final String money,
            final boolean positive) {
        performanceValue.setText(performance);
        performanceValue.setStyle(
            "-fx-text-fill:" + (positive ? "#27ae60" : "#e74c3c") + ";"
        );
        equityValue.setText(equity);
        moneyValue.setText(money);
    }

    /**
     * Updates the holdings table with new data.
     *
     * @param rows list of holdings currently owned by the player
     */
    public void setHoldingRows(final List<HoldingRow> rows) {
        holdingsTable.update(rows);
    }

    /**
     * Updates the transaction history panel.
     *
     * @param receipts receipts list of transaction receipts
     */
    public void setReceipts(final List<ReceiptData> receipts) {
        transactionsPanel.update(receipts);
    }

    /**
     * Sets the callback invoked when the user searches transactions.
     *
     * @param handler called with the lowercase search text
     */
    public void setOnTransactionSearch(final Consumer<String> handler) {
        transactionsPanel.setOnSearch(handler);
    }

    /**
     * Sets the callback invoked when the user clicks a transaction filter button.
     *
     * @param handler called with "all", "Purchase", or "Sale"
     */
    public void setOnTransactionFilter(final Consumer<String> handler) {
        transactionsPanel.setOnFilter(handler);
    }

    /**
     * Adds a new point to the portfolio growth chart.
     *
     * @param netWorth current net worth value
     */
    public void addChartPoint(final double netWorth) {
        chartData.add(BigDecimal.valueOf(netWorth));
        chart.updatePrices(chartData);
    }

    /**
     * Replaces the entire chart history.
     *
     * @param values list of net worth values per week, in chronological order
     */
    public void setChartHistory(final List<Double> values) {
        chartData.clear();
        values.forEach(v -> chartData.add(BigDecimal.valueOf(v)));
        chart.updatePrices(chartData);
    }

    /**
     * Builds the summary bar at the top of the view.
     *
     * @return configured HBox containing summary labels
     */
    private HBox buildSummaryBar() {
        HBox bar = new HBox(BAR_SPACING);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(BAR_PAD_V, BAR_PAD_H, BAR_PAD_V, BAR_PAD_H));
        bar.getChildren().addAll(
            statBox("Performance this week", performanceValue),
            statBox("Equity",                equityValue),
            statBox("Money",                 moneyValue)
        );

        return bar;
    }

    /**
     * Creates a single statistic box with title and value.
     *
     * @param title label title
     * @param value value label
     * @return VBox containing formatted stat block
     */
    private VBox statBox(final String title, final Label value) {
        Label t = new Label(title);
        return new VBox(STAT_SPACING, t, value);
    }

    /**
     * Builds the center section containing chart, holdings, and transactions.
     *
     * @return HBox with chart and holdings on the left,
     *         transactions on the right
     */
    private HBox buildCenterRow() {
        ScrollPane holdingsScroll = new ScrollPane(holdingsTable);
        holdingsScroll.setFitToWidth(true);
        holdingsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        holdingsScroll.setPrefHeight(HOLDINGS_SCROLL_HEIGHT);
        holdingsScroll.setStyle(
            "-fx-background:transparent;-fx-background-color:transparent;"
        );

        VBox left = new VBox(LEFT_SPACING, buildChart(), holdingsScroll);
        HBox.setHgrow(left, Priority.ALWAYS);

        return new HBox(CENTER_SPACING, left, transactionsPanel);
    }

    /**
     * Builds the portfolio growth chart.
     *
     * @return VBox containing the price chart component
     */
    private VBox buildChart() {
        VBox wrapper = new VBox(chart);
        HBox.setHgrow(wrapper, Priority.ALWAYS);

        wrapper.widthProperty().addListener((obs, old, newVal) -> {
            double w = newVal.doubleValue();
            if (w > 0) {
                chart.setWidth(w);
                chart.setHeight(w / CHART_RATIO);
            }
        });

        return wrapper;
    }

}
