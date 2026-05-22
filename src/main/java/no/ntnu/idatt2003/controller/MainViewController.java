package no.ntnu.idatt2003.controller;
 
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.io.GameFileHandler;
import no.ntnu.idatt2003.model.io.GameSaveException;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.model.observer.Observer;
import no.ntnu.idatt2003.view.ExchangeView;
import no.ntnu.idatt2003.view.GameSummaryView;
import no.ntnu.idatt2003.view.MainView;
 
/**
 * Controller for the main application view.
 *
 * <p>Mediates between the game model ({@link Exchange}, {@link Player}) and
 * {@link MainView}. Handles week progression, navigation actions, and
 * keeping the header and status display in sync with model state.
 */
public class MainViewController implements Observer {
 
    private final Exchange exchange;
    private final Player player;

    private final MainView mainView;
    private final PortfolioController portfolioController;

    private final ExchangeViewController exchangeViewController;
    private final ExchangeView exchangeView;

    private final File saveFile;

    /**
     * Creates the controller for a new game.
     *
     * @param exchange the market exchange used to advance weeks
     * @param player   the current player whose portfolio and status are displayed
     */
    public MainViewController(Exchange exchange, Player player) {
        this(exchange, player, null, null);
    }

    /**
     * Creates the controller for a game loaded from a save file.
     *
     * @param exchange         the market exchange used to advance weeks
     * @param player           the current player whose portfolio and status are displayed
     * @param saveFile         the file the game was loaded from, or null for a new game
     * @param existingSaveName the original save name, or null for a new game
     */
    public MainViewController(
            Exchange exchange, Player player, File saveFile, String existingSaveName) {
        this.exchange = exchange;
        this.player = player;
        this.saveFile = saveFile;

        this.mainView = new MainView(
            player.getName(),
            player.getMoney(),
            exchange.getWeek()
        );

        this.portfolioController = new PortfolioController(
            mainView,
            player,
            exchange,
            stock -> openTradeView(stock, this::showPortfolioView)
        );

        this.exchangeViewController = new ExchangeViewController(
            exchange,
            stock -> openTradeView(stock, this::showExchangeView)
        );

        this.exchangeView = new ExchangeView(exchangeViewController);

        mainView.setAdvanceAction(this::advanceWeek);
        mainView.setPortfolioAction(portfolioController::showPortfolioView);
        mainView.setExchangeAction(() -> mainView.setView(exchangeView));
        mainView.setSaveAction(this::saveGame);
        mainView.setEndGameAction(this::showGameSummaryView);

        exchange.attach(this);
        updateView();
    }

    /**
     * Called when the Exchange notifies observers of a state change.
     */
    @Override
    public void update() {
        updateView();
    }

    /**
     * Returns the main layout node to be placed in the scene graph.
     *
     * @return the {@link MainView} managed by this controller
     */
    public MainView getView() {
        return mainView;
    }

    /**
     * Shows the portfolio view immediately.
     */
    public void showPortfolioView() {
        portfolioController.showPortfolioView();
    }
 
    /**
     * Advances the simulation by one week, records a chart point,
     * and refreshes the view.
     */
    public void advanceWeek() {
        portfolioController.captureWeekStart();
        player.recordNetWorth();
        exchange.advance();
        portfolioController.recordWeek(); 
        updateView();
    }
 
    /**
     * Refreshes all dynamic UI elements to reflect current model state,
     * including the week counter, player balance, rank, stars, and goals.
     */
    private void updateView() {
        mainView.updateHeader(exchange.getWeek());
        mainView.updateMoney(player.getMoney());

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
                goal1Text = "Traded for at least 20 weeks";
                goal1Met = true;
                goal2Text = "Gained 100% net worth";
                goal2Met = true;
            }
            case "Investor" -> {
                stars = "★ ★ ☆";
                goal1Text = "Traded for at least 20 weeks";
                goal1Met = weeksTraded >= 20;
                goal2Text = "Gained 100% net worth";
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

        mainView.updateStatusDisplay(
            rank, 
            stars, 
            goal1Text, 
            goal1Met, 
            goal2Text, 
            goal2Met);
    }

   /**
     * Calculates the player's net worth gain as a percentage
     * of their starting money.
     *
     * <p>This includes both available money and current portfolio value.</p>
     *
     * @return gain percentage, or {@code 0.0} if starting money is zero
     */
    private double calculateGainPercent() {
        BigDecimal starting = player.getStartingMoney();

        if (starting.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = player.getNetWorth().subtract(starting);

            return diff.multiply(new BigDecimal("100"))
                    .divide(starting, 6, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return 0.0;
    }

    /**
     * Saves the current game state to a file and returns to the start screen.
     * The save name is always generated from the current week.
     * If the game was loaded from a save file, that file is deleted first.
     */
    private void saveGame() {
        String name = player.getName() + " – uke " + exchange.getWeek();
        if (saveFile != null) {
            saveFile.delete();
        }
        performSave(name);
    }

    /**
     * Writes the save file and navigates back to the start screen.
     *
     * @param name the save name to use
     */
    private void performSave(final String name) {
        try {
            GameFileHandler.saveGame(name, player, exchange);
            Stage stage = (Stage) mainView.getScene().getWindow();
            stage.setScene(new StartController(stage).getScene());
        } catch (GameSaveException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save failed");
            alert.setHeaderText("Could not save the game");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Shows the game summary view after the player ends the game.
     * If the game was loaded from a save file, deletes it since the session is over.
     */
    private void showGameSummaryView() {
        if (saveFile != null) {
            saveFile.delete();
        }
        Stage stage = (Stage) mainView.getScene().getWindow();
        GameSummaryView summaryView = new GameSummaryViewController(exchange, player).getView();
        summaryView.setMainMenuAction(() -> stage.setScene(new StartController(stage).getScene()));
        stage.setScene(summaryView.getScene());
    }

    /**
     * Shows the exchange view in the center of the main layout.
     */
    private void showExchangeView() {
        mainView.setView(exchangeView);
    }

    /**
     * Opens the trade view for the given stock, with a callback to return to the previous view.
     *
     * @param stock the stock to trade
     * @param onCancel callback to run if the user presses the back button
     */
    private void openTradeView(final Stock stock, final Runnable onCancel) {
        TradeViewController tradeViewController = new TradeViewController(
            exchange,
            player,
            stock,
            this::updateView,
            onCancel
        );
        mainView.setView(tradeViewController.getView());
    }
}
