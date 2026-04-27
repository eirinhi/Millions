package no.ntnu.idatt2003.controller;
 
import java.math.BigDecimal;
import java.math.RoundingMode;
 
import javafx.scene.control.Label;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.view.MainView;
 
/**
 * Controller for the main application view.
 *
 * <p>Mediates between the game model ({@link Exchange}, {@link Player}) and
 * {@link MainView}. Handles week progression, navigation actions, and
 * keeping the header and status display in sync with model state.
 */
public class MainViewController {
 
    private final Exchange exchange;
    private final Player player;
    private final MainView mainLayout;
    private PortfolioController portfolioController;
 
    /**
     * Creates the controller and wires up the main view.
     *
     * @param exchange the market exchange used to advance weeks
     * @param player   the current player whose portfolio and status are displayed
     */
    public MainViewController(Exchange exchange, Player player) {
        this.exchange = exchange;
        this.player = player;
        this.mainLayout = new MainView(player.getName(), player.getMoney(), exchange.getWeek());
        
        this.portfolioController = new PortfolioController(mainLayout, player, exchange);

        mainLayout.setAdvanceAction(this::advanceWeek);
        mainLayout.setPortfolioAction(portfolioController::showPortfolioView);
        mainLayout.setExchangeAction(() -> mainLayout.setView(new Label("Exchange trykket")));
        mainLayout.setExitAction(mainLayout::showExitView);
 
        updateView();
    }
 
    /**
     * Returns the main layout node to be placed in the scene graph.
     *
     * @return the {@link MainView} managed by this controller
     */
    public MainView getView() {
        return mainLayout;
    }
 
    /**
     * Advances the simulation by one week, records a chart point,
     * and refreshes the view.
     */
    public void advanceWeek() {
        exchange.advance();
        portfolioController.recordWeek(); // legg til nytt punkt på grafen
        updateView();
    }
 
    /**
     * Refreshes all dynamic UI elements to reflect current model state,
     * including the week counter, player balance, rank, stars, and goals.
     */
    private void updateView() {
        mainLayout.updateHeader(exchange.getWeek());
        mainLayout.updateMoney(player.getMoney());
 
        String rank = player.getStatus();
        int weeksTraded = player.getTransactionArchive().countDistinctWeeks();
        double gainPercent = calculateGainPercent();
 
        String stars;
        String goal1Text;
        boolean goal1Met;
        String goal2Text;
        boolean goal2Met;
 
        switch (rank) {
            case "Speculator" -> {
                stars = "★ ★ ★";
                goal1Text = "Traded for 20 weeks";
                goal1Met = true;
                goal2Text = "Doubled net worth (100%)";
                goal2Met = true;
            }
            case "Investor" -> {
                stars = "★ ★ ☆";
                goal1Text = "Goal: 20 weeks";
                goal1Met = weeksTraded >= 20;
                goal2Text = "Goal: 100% gain";
                goal2Met = gainPercent >= 100;
            }
            default -> {
                stars = "★ ☆ ☆";
                goal1Text = "Traded for at least 10 weeks";
                goal1Met = weeksTraded >= 10;
                goal2Text = "Gained 20% net worth";
                goal2Met = gainPercent >= 20;
            }
        }
 
        mainLayout.updateStatusDisplay(rank, stars, goal1Text, goal1Met, goal2Text, goal2Met);
    }
 
    /**
     * Calculates the player's net worth gain as a percentage of their starting money.
     *
     * @return gain percentage, or {@code 0.0} if starting money is zero
     */
    private double calculateGainPercent() {
        BigDecimal starting = player.getStartingMoney();
        if (starting.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = player.getMoney().subtract(starting);
            return diff.multiply(new BigDecimal("100"))
                       .divide(starting, 6, RoundingMode.HALF_UP)
                       .doubleValue();
        }
        return 0.0;
    }
}