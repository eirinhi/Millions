package no.ntnu.idatt2003.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.PortfolioFormatter.HoldingRow;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;
import no.ntnu.idatt2003.view.components.HoldingsTableComponent;
import no.ntnu.idatt2003.view.components.TransactionsPanelComponent;

/**
 * Portfolio view shown when the player clicks the "Portfolio" button.
 *
 * <p>Data population is delegated to {@link HoldingsTableComponent}
 * and {@link TransactionsPanelComponent}.
 */
public class PortfolioView extends VBox {

    // --- Summary labels ---
    private final Label performanceValue = new Label("--");
    private final Label equityValue      = new Label("-- NOK");
    private final Label moneyValue       = new Label("-- NOK");

    // --- Chart ---
    private final XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();
    private int chartWeek = 0;

    // --- Components ---
    private final HoldingsTableComponent     holdingsTable;
    private final TransactionsPanelComponent transactionsPanel;

    // --- Button callbacks ---
    private Runnable buyAction;
    private Runnable sellAction;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

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

    // -----------------------------------------------------------------------
    // Public API (called by PortfolioController)
    // -----------------------------------------------------------------------

    /**
     * Updates the three summary labels.
     *
     * @param performance formatted string, e.g. "+3,50%"
     * @param equity      formatted equity
     * @param money       formatted cash balance
     * @param positive    true → green, false → red for the performance label
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
     * Replaces all holdings rows.
     *
     * @param rows one row per share currently owned
     */
    public void setHoldingRows(List<HoldingRow> rows) {
        holdingsTable.update(rows);
    }

    /**
     * Replaces all transaction receipt cards.
     *
     * @param receipts all transactions; newest will appear at the top
     */
    public void setReceipts(List<ReceiptData> receipts) {
        transactionsPanel.update(receipts);
    }

    /** Appends one data point to the growth chart. */
    public void addChartPoint(double netWorth) {
        chartSeries.getData().add(new XYChart.Data<>(++chartWeek, netWorth));
    }

    /** Replaces the entire chart history (e.g. on initial load). */
    public void setChartHistory(List<Double> values) {
        chartSeries.getData().clear();
        chartWeek = 0;
        values.forEach(v ->
            chartSeries.getData().add(new XYChart.Data<>(++chartWeek, v)));
    }

    public void setBuyAction(Runnable r)  { this.buyAction  = r; }
    public void setSellAction(Runnable r) { this.sellAction = r; }

    // -----------------------------------------------------------------------
    // Layout builders (private)
    // -----------------------------------------------------------------------

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

    private VBox statBox(String title, Label value) {
        Label t = new Label(title);
        t.getStyleClass().add("stat-title");
        value.getStyleClass().add("stat-value");
        return new VBox(4, t, value);
    }

    /** Chart (left, grows) + transactions panel (right, fixed width). */
    private HBox buildCenterRow() {
        HBox row = new HBox(16, buildChart(), transactionsPanel);
        VBox.setVgrow(row, Priority.ALWAYS);
        return row;
    }

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

    /** Holdings table + BUY / SELL buttons. */
    private VBox buildBottomRow() {
        Button buyBtn  = new Button("BUY");
        Button sellBtn = new Button("SELL");
        buyBtn.getStyleClass().add("buy-btn");
        sellBtn.getStyleClass().add("sell-btn");
        buyBtn.setPrefWidth(90);
        sellBtn.setPrefWidth(90);
        buyBtn.setOnAction(e ->  { if (buyAction  != null) buyAction.run();  });
        sellBtn.setOnAction(e -> { if (sellAction != null) sellAction.run(); });

        HBox buttons = new HBox(10, buyBtn, sellBtn);
        buttons.setPadding(new Insets(8, 0, 0, 0));

        return new VBox(8, holdingsTable, buttons);
    }
}