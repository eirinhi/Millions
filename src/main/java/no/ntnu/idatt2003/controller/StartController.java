package no.ntnu.idatt2003.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.io.FileHandler;
import no.ntnu.idatt2003.model.io.GameFileHandler;
import no.ntnu.idatt2003.model.io.GameLoadResult;
import no.ntnu.idatt2003.model.io.GameSave;
import no.ntnu.idatt2003.model.io.GameSaveException;
import no.ntnu.idatt2003.model.io.StockFileHandler;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.view.StartView;

/**
 * Controller for the start view of the Millions game.
 *
 * <p>Handles validation of user input, player creation, and loading of stock data
 * before the game begins.</p>
 */
public class StartController {

    /** Width of the main game scene in pixels. */
    private static final int SCENE_WIDTH = 1000;

    /** Height of the main game scene in pixels. */
    private static final int SCENE_HEIGHT = 700;

    private final Stage primaryStage;
    private final StartView startView;
    private File selectedFile;

    /**
     * Creates a start screen controller and wires view callbacks.
     *
     * @param primaryStage application stage
     */
    public StartController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.startView = new StartView();
        this.selectedFile = null;

        startView.setSelectFileAction(this::onSelectFile);
        startView.setStartAction(this::onStartGame);
        startView.setLoadAction(this::onLoadGame);
        startView.populateSaveTable(loadSavedGames());
    }

    /**
     * Returns the start screen scene.
     *
     * @return start scene
     */
    public Scene getScene() {
        return startView.getScene();
    }

    /**
     * Loads all valid saved games from disk.
     *
     * @return list of GameSave metadata, skipping corrupted files
     */
    private List<GameSave> loadSavedGames() {
        GameFileHandler handler = new GameFileHandler();
        List<GameSave> saves = new ArrayList<>();
        for (File file : GameFileHandler.getSavedGames()) {
            try {
                saves.add(handler.readSave(file));
            } catch (GameSaveException _) {
                // skip corrupted files
            }
        }
        return saves;
    }

    /**
     * Handles stock file selection.
     */
    private void onSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select stock data file");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File chosen = fileChooser.showOpenDialog(primaryStage);
        if (chosen != null) {
            selectedFile = chosen;
            startView.setSelectedFileName(chosen.getName());
            startView.hideFileError();
        }
    }

    /**
     * Handles start button click and navigates to main scene when valid.
     */
    private void onStartGame() {
        String name = startView.getNameInput();
        int capital = startView.getCapitalInput();

        startView.showNameError(name.isBlank());
        startView.showCapitalError(capital < 1000 || capital > 100000);

        if (selectedFile == null) {
            startView.showFileError("Please select a stock data file.");
        } else {
            startView.hideFileError();
        }

        Scene mainScene = createMainScene(name, capital, selectedFile);
        if (mainScene == null) {
            if (selectedFile != null && validateInput(name, capital, selectedFile)) {
                startView.showFileError("Could not read stock file.");
            }
            return;
        }

        primaryStage.setScene(mainScene);
    }

    /**
     * Handles load game button click.
     * Loads the selected save and navigates to the main scene.
     */
    private void onLoadGame() {
        GameSave selected = startView.getSelectedSave();

        if (selected == null) {
            return;
        }

        try {
            File file = GameFileHandler.getSavedGames().stream()
                .filter(f -> f.getName().startsWith(selected.getSaveName()))
                .findFirst()
                .orElseThrow();

            GameLoadResult result = new GameFileHandler().loadGame(file);

            MainViewController controller = new MainViewController(
                result.getExchange(),
                result.getPlayer(),
                file,
                selected.getSaveName());
            controller.showPortfolioView();
            primaryStage.setScene(
                    new Scene(controller.getView(), SCENE_WIDTH, SCENE_HEIGHT)
            );

        } catch (GameSaveException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load failed");
            alert.setHeaderText("Could not load the saved game.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Validates the player's input on the start screen.
     * Shows an error dialog if any field is invalid.
     *
     * @param name      the player name entered by the user
     * @param capital   the starting capital entered by the user
     * @param stockFile the stock data file selected by the user
     * @return          {@code true} if all inputs are valid, {@code false} otherwise
     */
    public static boolean validateInput(String name, int capital, File stockFile) {
        return name != null && !name.isBlank()
                && capital >= 1000 && capital <= 100000
                && stockFile != null;
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
     * Returns an empty list if the file cannot be read.
     *
     * @param file the stock data file to read
     * @return      a list of stocks, or an empty list if loading failed
     */
    public static List<Stock> loadStocks(File file) {
        try {
            FileHandler<List<Stock>> handler = new StockFileHandler();
            return handler.readFromFile(file.getAbsolutePath());
        } catch (Exception _) {
            return List.of();
        }
    }

    /**
     * Creates a new {@link Exchange} with the given list of stocks.
     *
     * @param stocks the list of stocks available on the exchange
     * @return       a new {@link Exchange} instance
     */
    public static Exchange createExchange(List<Stock> stocks) {
        return new Exchange("Millions Exchange", stocks);
    }

    /**
     * Creates the main game scene from start-screen input.
     *
     * @param name      player name
     * @param capital   starting capital
     * @param stockFile selected stock file
     * @return          the main game scene, or {@code null} if input/file is invalid
     */
    public static Scene createMainScene(String name, int capital, File stockFile) {
        if (!validateInput(name, capital, stockFile)) {
            return null;
        }

        List<Stock> stocks = loadStocks(stockFile);
        if (stocks == null || stocks.isEmpty()) {
            return null;
        }

        Player player = createPlayer(name, capital);
        Exchange exchange = createExchange(stocks);
        MainViewController controller = new MainViewController(exchange, player);
        controller.showPortfolioView();
        return new Scene(
                controller.getView(), SCENE_WIDTH, SCENE_HEIGHT
        );
    }
}
