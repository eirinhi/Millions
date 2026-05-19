package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.model.observer.Observer;
import no.ntnu.idatt2003.view.MainView;
import no.ntnu.idatt2003.view.PortfolioView;

/**
 * Controller responsible for managing the {@link PortfolioView}.
 *
 * <p>This class acts as a mediator between the model layer
 * ({@link Player}, {@link Exchange}) and the portfolio UI.
 * It ensures that the view is always synchronized with the
 * latest portfolio state.
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
 *     <li>Delegating trade dialogs to {@link TradeController}</li>
 *     <li>Updating performance history for chart visualization</li>
 * </ul>
 */
public class PortfolioController implements Observer {

    private final MainView mainView;
    private final Player   player;
    private final Exchange exchange;

    private PortfolioView view;
    private final PortfolioFormatter formatter = new PortfolioFormatter();

    /**
     * Creates a new PortfolioController and registers it as an observer
     * of the exchange.
     *
     * @param mainView the main application view used for view switching
     * @param player   the currently active player whose portfolio is displayed
     * @param exchange the stock exchange providing market updates
     */
    public PortfolioController(MainView mainView, Player player, Exchange exchange) {
        this.mainView = mainView;
        this.player   = player;
        this.exchange = exchange;
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
            view = new PortfolioView();
            List<Double> history = player.getNetWorthHistory().stream()
                .map(BigDecimal::doubleValue)
                .toList();
            view.setChartHistory(history);
            if (history.isEmpty()) {
                recordWeek();
            }
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

        double performance = formatter.performancePercent(player);

        view.updateStats(
            String.format("%+.2f%%", performance),
            String.format("%.2f NOK", formatter.equity(player)),
            String.format("%.2f NOK", player.getMoney()),
            performance >= 0
        );

        view.setHoldingRows(formatter.holdingRows(player));
        view.setReceipts(formatter.receipts(player));
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

        BigDecimal totalValue = player.getMoney()
            .add(formatter.equity(player));

        player.recordNetWorth();
        view.addChartPoint(totalValue.doubleValue());
    }

    /**
     * Removes this controller from the exchange observer list.
     *
     * <p>Should be called when the controller is no longer needed
     * to prevent memory leaks.
     */
    public void dispose() {
        exchange.detach(this);
    }
}