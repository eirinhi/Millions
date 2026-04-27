package no.ntnu.idatt2003.view;
 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
 
/**
 * Portfolio view shown when the player clicks the "Portfolio" button.
 *
 * <p>Displays:
 * <ul>
 *   <li>A summary bar (performance, equity, cash)</li>
 *   <li>A line chart showing portfolio growth over time</li>
 *   <li>A holdings list on the left</li>
 *   <li>BUY / SELL buttons</li>
 *   <li>A transaction list on the right</li>
 * </ul>
 */
public class PortfolioView extends VBox {
 
    private final Label performanceValue;
    private final Label equityValue;
    private final Label moneyValue;
 
    private final ListView<String> holdingsList;
    private final ListView<String> transactionList;
 
    private final XYChart.Series<Number, Number> chartSeries;
    private int chartWeekCounter = 0;
 
    private Runnable buyAction;
    private Runnable sellAction;
 
    public PortfolioView() {
        setSpacing(20);
        setPadding(new Insets(20));
        getStyleClass().add("portfolio-view");
 
        performanceValue = new Label("--");
        equityValue      = new Label("-- NOK");
        moneyValue       = new Label("-- NOK");
        holdingsList     = new ListView<>();
        transactionList  = new ListView<>();
        chartSeries      = new XYChart.Series<>();
        chartSeries.setName("Net Worth");
 
        getChildren().addAll(createSummaryBar(), createContentArea());
    }
 
    private HBox createSummaryBar() {
        HBox bar = new HBox(40);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(15));
        bar.getStyleClass().add("summary-bar");
 
        bar.getChildren().addAll(
            createStatBox("Performance this week", performanceValue),
            createStatBox("Equity",                equityValue),
            createStatBox("Money",                 moneyValue)
        );
        return bar;
    }
 
    private VBox createStatBox(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");
        valueLabel.getStyleClass().add("stat-value");
        return new VBox(5, titleLabel, valueLabel);
    }
 
    private HBox createContentArea() {
        HBox content = new HBox(20);
        VBox.setVgrow(content, Priority.ALWAYS);
 
        // Left: Holdings + BUY/SELL
        Label holdingsTitle = new Label("Holdings");
        holdingsTitle.getStyleClass().add("section-title");
 
        Button buyBtn  = new Button("BUY");
        Button sellBtn = new Button("SELL");
        buyBtn.getStyleClass().add("buy-btn");
        sellBtn.getStyleClass().add("sell-btn");
        buyBtn.setPrefWidth(90);
        sellBtn.setPrefWidth(90);
 
        buyBtn.setOnAction(e -> { if (buyAction  != null) buyAction.run(); });
        sellBtn.setOnAction(e -> { if (sellAction != null) sellAction.run(); });
 
        HBox btnRow = new HBox(10, buyBtn, sellBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);
 
        VBox left = new VBox(10, holdingsTitle, holdingsList, btnRow);
        left.setPrefWidth(220);
 
        // Middle: Line chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Week");
        yAxis.setLabel("NOK");
        xAxis.setForceZeroInRange(false);
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number value) {
                return String.valueOf(value.intValue());
            }
        });

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Portfolio Growth");
        chart.getData().add(chartSeries);
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.getStyleClass().add("portfolio-chart");
        HBox.setHgrow(chart, Priority.ALWAYS);
 
        VBox middle = new VBox(chart);
        VBox.setVgrow(chart, Priority.ALWAYS);
        HBox.setHgrow(middle, Priority.ALWAYS);
 
        // Right: Transactions
        Label txTitle = new Label("Transactions");
        txTitle.getStyleClass().add("section-title");
 
        VBox right = new VBox(10, txTitle, transactionList);
        right.setPrefWidth(220);
 
        content.getChildren().addAll(left, middle, right);
        return content;
    }
 
    /**
     * Updates the three summary labels at the top.
     *
     * @param performance formatted performance string (e.g. "+3.50%")
     * @param equity      formatted equity string
     * @param money       formatted cash string
     * @param isPositive  true → green, false → red for performance label
     */
    public void updateStats(String performance, String equity, String money, boolean isPositive) {
        performanceValue.setText(performance);
        performanceValue.setStyle("-fx-text-fill: " + (isPositive ? "#27ae60" : "#e74c3c") + ";");
        equityValue.setText(equity);
        moneyValue.setText(money);
    }
 
    /**
     * Replaces the holdings list items.
     *
     * @param items formatted strings like "AAPL x5, gave 1 234,00 NOK"
     */
    public void setHoldings(List<String> items) {
        holdingsList.getItems().setAll(items);
    }
 
    /**
     * Replaces the transaction list items.
     *
     * @param items formatted transaction strings
     */
    public void setTransactions(List<String> items) {
        transactionList.getItems().setAll(items);
    }
 
    /**
     * Appends a new data point to the portfolio growth chart.
     * Call once per refresh with the player's current net worth.
     *
     * @param netWorthValue the current net worth to plot
     */
    public void addChartPoint(double netWorthValue) {
        chartSeries.getData().add(
            new XYChart.Data<>(++chartWeekCounter, netWorthValue)
        );
    }
 
    /**
     * Replaces all chart data (e.g. on initial load from history).
     *
     * @param weeklyNetWorths ordered list of net worth values, index 0 = week 1
     */
    public void setChartHistory(List<Double> weeklyNetWorths) {
        chartSeries.getData().clear();
        chartWeekCounter = 0;
        for (double v : weeklyNetWorths) {
            chartSeries.getData().add(new XYChart.Data<>(++chartWeekCounter, v));
        }
    }
 
    /**
     * Registers the action to run when the BUY button is clicked.
     *
     * @param action the callback
     */
    public void setBuyAction(Runnable action)  { this.buyAction  = action; }
 
    /**
     * Registers the action to run when the SELL button is clicked.
     *
     * @param action the callback
     */
    public void setSellAction(Runnable action) { this.sellAction = action; }
}