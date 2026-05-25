package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Consumer;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.model.observer.Observer;
import no.ntnu.idatt2003.view.MainView;
import no.ntnu.idatt2003.view.PortfolioView;

/**
 * Controller responsible for managing the {@link PortfolioView}.
 *
 * <p>Mediatates between the model layer ({@link Player}, {@link Exchange})
 * and the portfolio UI.
 *
 * <p>The controller implements {@link Observer} so it can react
 * automatically to updates from the {@link Exchange}, such as
 * when time advances or trades are executed.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Initializing and displaying the portfolio view</li>
 *     <li>Refreshing UI data when the model changes</li>
 *     <li>Delegating formatting logic to {@link PortfolioFormatter}</li>
 *     <li>Delegating trade dialogs to {@link TradeViewController}</li>
 *     <li>Updating performance history for chart visualization</li>
 * </ul>
 */
public class PortfolioController implements Observer {

    private final MainViewPort mainView;
    private final Player   player;
    private final Exchange exchange;
    private final Consumer<Stock> onTradeRequested;
    private final PortfolioViewFactory viewFactory;

    private PortfolioViewPort view;
    private final PortfolioFormatter formatter = new PortfolioFormatter();
    private BigDecimal weekStartNetWorth;

    private List<ReceiptData> allReceipts = List.of();
    private String searchText = "";
    private String activeFilter = "all";

    /**
     * Creates a new PortfolioController and registers it as an observer
     * of the exchange.
     *
     * @param mainView the main application view used for view switching
     * @param player   the currently active player whose portfolio is displayed
     * @param exchange the stock exchange providing market updates
     */
    public PortfolioController(
        final MainView mainView,
        final Player player,
        final Exchange exchange,
        final Consumer<Stock> onTradeRequested) {
        this(
            mainView == null ? null : new MainViewAdapter(mainView),
            player,
            exchange,
            onTradeRequested,
            new JavaFxPortfolioViewFactory()
        );
    }

    /**
     * <p>This constructor is package-private so tests can exercise controller
     * logic without starting JavaFX.</p>
     *
     * <p>This was introduced through AI assistance to facilitate testing coverage.
     * In production, the controller uses real {@code PortfolioView}. In test, it uses {@code PortfolioViewPort}.</p>
     *
     * @param mainView          the main application view used for view switching
     * @param player            the currently active player whose portfolio is displayed
     * @param exchange          the stock exchange providing market updates
     * @param onTradeRequested  callback invoked when a trade is requested
     * @param viewFactory       factory for creating portfolio views
     */
    PortfolioController(
        final MainViewPort mainView,
        final Player player,
        final Exchange exchange,
        final Consumer<Stock> onTradeRequested,
        final PortfolioViewFactory viewFactory) {
        this.mainView = mainView;
        this.player   = player;
        this.exchange = exchange;
        this.onTradeRequested = onTradeRequested;
        this.viewFactory = viewFactory;
        this.exchange.attach(this);
    }

    /**
     * Called automatically when the {@link Exchange} notifies observers
     * of a state change. Triggers a refresh of the portfolio view.
     */
    @Override
    public void update() {
        refresh();
    }

    /**
     * Displays the portfolio view in the main application window.
     * The view is created lazily if it does not already exist.
     *
     * @throws IllegalStateException if {@code MainView} has not been set
     */
    public void showPortfolioView() {
        if (mainView == null) {
            throw new IllegalStateException("MainView is not initialized");
        }

        if (view == null) {
            view = viewFactory.create(symbol -> {
                Stock stock = exchange.getStock(symbol);
                if (stock != null) {
                    onTradeRequested.accept(stock);
                }
            });
            List<Double> history = player.getNetWorthHistory().stream()
                .map(BigDecimal::doubleValue)
                .toList();
            view.setChartHistory(history);
            if (history.isEmpty()) {
                recordWeek();
            }
            view.setOnTransactionSearch(this::onSearch);
            view.setOnTransactionFilter(this::onFilter);
        }

        refresh();
        mainView.setView(view);
    }

    /**
     * Refreshes all displayed portfolio data.
     *
     * <p>Safe to call even before the view has been created;
     * in that case the call is silently ignored.
     */
    public void refresh() {
        if (view == null) return;

        double performance = 0.0;
        if (weekStartNetWorth != null
                && weekStartNetWorth.compareTo(BigDecimal.ZERO) != 0) {
            performance = player.getNetWorth()
                .subtract(weekStartNetWorth)
                .multiply(BigDecimal.valueOf(100))
                .divide(weekStartNetWorth, 4, RoundingMode.HALF_UP)
                .doubleValue();
        }

        view.updateStats(
            String.format("%+.2f%%", performance),
            String.format("%.2f $", player.getNetWorth()),
            String.format("%.2f $", player.getMoney()),
            performance >= 0
        );

        view.setHoldingRows(formatter.holdingRows(player));
        allReceipts = formatter.receipts(player);
        view.setReceipts(filteredReceipts());
    }

    /**
     * Handles transaction search input and updates the transaction display.
     *
     * @param text the search text entered
     */
    public void onSearch(final String text) {
        searchText = text;
        if (view != null) view.setReceipts(filteredReceipts());
    }

    /**
     * Handles transaction filter changes and updates the transaction display.
     *
     * @param filter the selected filter
     */
    public void onFilter(final String filter) {
        activeFilter = filter;
        if (view != null) view.setReceipts(filteredReceipts());
    }

    /**
     * Applies the current search text and filter to the full list of receipts.
     *
     * @return filtered list of receipts matching the search and filter
     */
    private List<ReceiptData> filteredReceipts() {
        return allReceipts.stream()
            .filter(r -> activeFilter.equals("all") || r.type().equals(activeFilter))
            .filter(r -> searchText.isEmpty()
                || r.symbol().toLowerCase().contains(searchText)
                || r.company().toLowerCase().contains(searchText)
                || String.valueOf(r.week()).contains(searchText))
            .toList();
    }

    /**
     * Records the current total portfolio value as a new data point
     * in the performance chart.
     *
     * <p>Should be called once per simulated time step,
     * typically when a new week is advanced in the application.
     */
    public void recordWeek() {
        if (view == null) return;

        view.addChartPoint(player.getNetWorth().doubleValue());
    }

    /**
     * Removes this controller from the exchange observer list.
     *
     * <p>Should be called when the controller is no longer needed
     * to prevent memory leaks.
     */
    public void captureWeekStart() {
        weekStartNetWorth = player.getNetWorth();
    }

    /**
     * Cleans up resources used by this controller.
     */
    public void dispose() {
        exchange.detach(this);
    }

    /**
     * Interface for the main view port.
     */
    interface MainViewPort {
        void setView(PortfolioViewPort view);
    }

    /**
     * Interface for the portfolio view port.
     */
    interface PortfolioViewPort {
        void updateStats(String performance, String equity, String money, boolean positive);

        void setHoldingRows(List<PortfolioFormatter.HoldingRow> rows);

        void setReceipts(List<ReceiptData> receipts);

        void addChartPoint(double netWorth);

        void setChartHistory(List<Double> values);

        void setOnTransactionSearch(Consumer<String> handler);

        void setOnTransactionFilter(Consumer<String> handler);
    }

    /**
     * Factory interface for creating portfolio view ports.
     */
    interface PortfolioViewFactory {
        PortfolioViewPort create(Consumer<String> onStockSelected);
    }

    /**
     * Adapter for the main view port.
     */
    private record MainViewAdapter(MainView delegate) implements MainViewPort {

        @Override
        public void setView(final PortfolioViewPort view) {
            delegate.setView(((PortfolioViewAdapter) view).delegate);
        }
    }

    /**
     * Factory for creating portfolio view ports.
     */
    private static class JavaFxPortfolioViewFactory implements PortfolioViewFactory {

        @Override
        public PortfolioViewPort create(final Consumer<String> onStockSelected) {
            return new PortfolioViewAdapter(new PortfolioView(onStockSelected));
        }
    }

    /**
     * Adapter for the portfolio view port.
     * This class was discussed with AI for help with the testing of JavaFX.
     */
    private record PortfolioViewAdapter(PortfolioView delegate) implements PortfolioViewPort {

        /**
         * Updates the portfolio statistics displayed in the view.
         *
         * @param performance the performance string to display
         * @param equity      the equity string to display
         * @param money       the money string to display
         */
        @Override
        public void updateStats(
                final String performance,
                final String equity,
                final String money,
                final boolean positive) {
            delegate.updateStats(performance, equity, money, positive);
        }

        /**
         * Sets the holding rows displayed in the view.
         *
         * @param rows the list of holding rows to display
         */
        @Override
        public void setHoldingRows(final List<PortfolioFormatter.HoldingRow> rows) {
            delegate.setHoldingRows(rows);
        }

        /**
         * Sets the receipts displayed in the view.
         *
         * @param receipts the list of receipts to display
         */
        @Override
        public void setReceipts(final List<ReceiptData> receipts) {
            delegate.setReceipts(receipts);
        }

        /**
         * Adds a chart point to the view.
         *
         * @param netWorth the net worth value to display
         */
        @Override
        public void addChartPoint(final double netWorth) {
            delegate.addChartPoint(netWorth);
        }

        /**
         * Sets the chart history displayed in the view.
         *
         * @param values the list of historical chart values to display
         */
        @Override
        public void setChartHistory(final List<Double> values) {
            delegate.setChartHistory(values);
        }

        /**
         * Sets the on transaction search handler.
         *
         * @param handler the handler to invoke when a transaction search is requested
         */
        @Override
        public void setOnTransactionSearch(final Consumer<String> handler) {
            delegate.setOnTransactionSearch(handler);
        }

        /**
         * Sets the on transaction filter handler.
         *
         * @param handler the handler to invoke when a transaction filter is requested
         */
        @Override
        public void setOnTransactionFilter(final Consumer<String> handler) {
            delegate.setOnTransactionFilter(handler);
        }
    }
}
