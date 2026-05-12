package no.ntnu.idatt2003.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.PortfolioFormatter.HoldingRow;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;
import no.ntnu.idatt2003.view.components.HoldingsTableComponent;
import no.ntnu.idatt2003.view.components.TransactionsPanelComponent;

/**
 * View representing the users portfolio screen.
 *
 * <p>This view is responsible for displaying the entire portfolio UI, including:
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

    private final Label performanceValue = new Label("--");
    private final Label equityValue      = new Label("-- NOK");
    private final Label moneyValue       = new Label("-- NOK");

    private final XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();
    private int chartWeek = 0;

    private final HoldingsTableComponent     holdingsTable;
    private final TransactionsPanelComponent transactionsPanel;

    /**
     * Constructs the portfolio view and initializes all UI components.
     */
    public PortfolioView() {
        setSpacing(16);
        setPadding(new Insets(20));
        getStyleClass().add("portfolio-view");

        chartSeries.setName("Net Worth");
        holdingsTable     = new HoldingsTableComponent();
        transactionsPanel = new TransactionsPanelComponent();

        getChildren().addAll(
            buildSummaryBar(),
            buildCenterRow(),
            buildBottomRow()
        );
    }

    /**
     * Updates the three summary statistics shown at the top of the view.
     *
     * @param performance formatted string, e.g. "+3,50%"
     * @param equity      formatted total equity value
     * @param money       formatted cash balance
     * @param positive    positive true if performance is positive, false otherwise
     */
    public void updateStats(String performance, String equity,
                            String money, boolean positive) {
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
    public void setHoldingRows(List<HoldingRow> rows) {
        holdingsTable.update(rows);
    }

    /**
     * Updates the transaction history panel.
     *
     * @param receipts receipts list of transaction receipts
     */
    public void setReceipts(List<ReceiptData> receipts) {
        transactionsPanel.update(receipts);
    }

    /**
     * Adds a new point to the portfolio growth chart.
     *
     * @param netWorth current net worth value
     */
    public void addChartPoint(double netWorth) {
        chartSeries.getData().add(new XYChart.Data<>(++chartWeek, netWorth));
    }

    /** Replaces the entire chart history.
     * 
     * @param values list of net worth values for each week, in chronological order
     */
    public void setChartHistory(List<Double> values) {
        chartSeries.getData().clear();
        chartWeek = 0;
        values.forEach(v ->
            chartSeries.getData().add(new XYChart.Data<>(++chartWeek, v)));
    }

    /**
     * Builds the summary bar at the top of the view.
     *
     * @return configured HBox containing summary labels
     */
    private HBox buildSummaryBar() {
        HBox bar = new HBox(40);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 16, 12, 16));
        bar.getStyleClass().add("summary-bar");
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
    private VBox statBox(String title, Label value) {
        Label t = new Label(title);
        t.getStyleClass().add("stat-title");
        value.getStyleClass().add("stat-value");
        return new VBox(4, t, value);
    }

    /**
     * Builds the center section containing chart and transactions panel.
     *
     * @return HBox containing chart and transaction panel
     */
    private HBox buildCenterRow() {
        HBox row = new HBox(16, buildChart(), transactionsPanel);
        VBox.setVgrow(row, Priority.ALWAYS);
        return row;
    }

    /**
     * Builds the portfolio growth chart.
     *
     * @return VBox containing configured LineChart
     */
    private VBox buildChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Week");
        yAxis.setLabel("NOK");
        xAxis.setForceZeroInRange(false);
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number v) { return String.valueOf(v.intValue()); }
        });

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Portfolio Growth");
        chart.getData().add(chartSeries);
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.getStyleClass().add("portfolio-chart");
        HBox.setHgrow(chart, Priority.ALWAYS);

        VBox wrapper = new VBox(chart);
        VBox.setVgrow(chart, Priority.ALWAYS);
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    /**
     * Builds the bottom section containing the holdings table.
     *
     * <p>Trade actions (buy/sell) are no longer initiated from this view.
     * They are handled by the dedicated TradeView instead.
     *
     * @return VBox containing the holdings table
     */
    private VBox buildBottomRow() {
        return new VBox(8, holdingsTable);
    }
}