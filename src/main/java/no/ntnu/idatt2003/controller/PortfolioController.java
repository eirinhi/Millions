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

    private final MainView mainView;
    private final Player   player;
    private final Exchange exchange;
    private final Consumer<Stock> onTradeRequested;

    private PortfolioView view;
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
        this.mainView = mainView;
        this.player   = player;
        this.exchange = exchange;
        this.onTradeRequested = onTradeRequested;
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
            view = new PortfolioView(symbol -> {
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

    public void dispose() {
        exchange.detach(this);
    }
}