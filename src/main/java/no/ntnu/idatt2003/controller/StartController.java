package no.ntnu.idatt2003.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.io.StockFileHandler;

/**
 * Controller for the start view of the Millions game.
 *
 * <p>Handles validation of user input, player creation, and loading of stock data
 * before the game begins.</p>
 */
public class StartController {

    /** Private constructor to prevent instantiation. */
    private StartController() {}

    /**
     * Validates the player's input on the start screen.
     * Shows an error dialog if any field is invalid.
     *
     * @param name the player name entered by the user
     * @param capital the starting capital entered by the user
     * @param stockFile the stock data file selected by the user
     * @return {@code true} if all inputs are valid, {@code false} otherwise
     */
    public static boolean validateInput(String name, int capital, File stockFile) {
        if (name.isBlank()) {
            showError("Name cannot be empty.");
            return false;
        }
        if (capital < 1000 || capital > 100000) {
            showError("Starting capital must be between 1000 and 100000.");
            return false;
        }
        if (stockFile == null) {
            showError("Please select a stock data file.");
            return false;
        }
        return true;
    }

    /**
     * Creates a new {@link Player} with the given name and starting capital.
     *
     * @param name the player's name
     * @param capital the starting capital
     * @return a new {@link Player} instance
     */
    public static Player createPlayer(String name, int capital) {
        return new Player(name, new BigDecimal(capital));
    }

    /**
     * Loads a list of {@link Stock} objects from the given file.
     * Shows an error dialog and returns {@code null} if the file cannot be read.
     *
     * @param file the stock data file to read
     * @return a list of stocks, or {@code null} if loading failed
     */
    public static List<Stock> loadStocks(File file) {
        try {
            return StockFileHandler.readStocksFromFile(file.getAbsolutePath());
        } catch (Exception e) {
            showError("Could not read stock file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Displays an error alert dialog with the given message.
     *
     * @param message the error message to display
     */
    private static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
