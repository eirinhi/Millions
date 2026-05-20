package no.ntnu.idatt2003.view;

import java.io.File;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.model.io.GameFileHandler;
import no.ntnu.idatt2003.model.io.GameSave;
import no.ntnu.idatt2003.model.io.GameSaveException;

/**
 * StartView is the initial view of the application.
 * The user must input their name, a starting capital, and a stock
 * data file before starting the game.
 */
public class StartView {

    /** The scene for the start view. */
    private final Scene scene;
    private final TextField nameField;
    private final Spinner<Integer> capitalSpinner;
    private final Label nameError;
    private final Label capitalError;
    private final Label fileError;
    private final Label fileNameLabel;
    private final Button fileButton;
    private final Button startButton;
    private final Button loadButton;
    private final TableView<GameSave> loadTable;

    /**
     * Constructs the StartView.
     */
    public StartView() {

        // Welcome label
        Label welcomeLabel = new Label("Welcome to Millions!");
        welcomeLabel.getStyleClass().add("hero-label");
        welcomeLabel.setPadding(new Insets(0, 0, 40, 0));
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);
        welcomeLabel.setAlignment(Pos.CENTER);


        // Name input
        Label nameLabel = new Label("Name : ");
        nameLabel.setMinWidth(140);
        nameField = new TextField();
        nameField.setPrefWidth(250);

        nameError = new Label("Name cannot be empty.");
        nameError.getStyleClass().add("error-label");
        nameError.setVisible(false);
        nameField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                nameError.setVisible(false);
            }
        });

        HBox nameRow = new HBox(10, nameLabel, nameField);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        VBox nameSection = new VBox(2, nameRow, nameError);
        nameSection.setAlignment(Pos.CENTER);


        // Capital input
        Label capitalLabel = new Label("Starting capital : ");
        capitalLabel.setMinWidth(140);
        capitalSpinner = new Spinner<>(1000, 100000, 10000, 1000);
        capitalSpinner.setEditable(true);
        capitalSpinner.setPrefWidth(150);

        capitalError = new Label("Capital must be between 1000 and 100000.");
        capitalError.getStyleClass().add("error-label");
        capitalError.setVisible(false);

        HBox capitalRow = new HBox(10, capitalLabel, capitalSpinner);
        capitalRow.setAlignment(Pos.CENTER_LEFT);
        VBox capitalSection = new VBox(2, capitalRow, capitalError);
        capitalSection.setAlignment(Pos.CENTER);


        // Stock file input
        Label stockLabel = new Label("Stock data : ");
        stockLabel.setMinWidth(140);

        fileButton = new Button("Select file");
        fileNameLabel = new Label("No file selected");
        fileNameLabel.getStyleClass().add("muted-label");

        fileError = new Label("Please select a stock data file.");
        fileError.getStyleClass().add("error-label");
        fileError.setVisible(false);

        HBox stockRow = new HBox(10, stockLabel, fileButton, fileNameLabel);
        stockRow.setAlignment(Pos.CENTER_LEFT);
        VBox stockSection = new VBox(2, stockRow, fileError);
        stockSection.setAlignment(Pos.CENTER);


        // Load game
        Label loadLabel = new Label("Load saved game : ");

        loadButton = new Button("LOAD GAME");
        loadButton.setDisable(true);
        loadButton.getStyleClass().add("small-btn");

        Region loadSpacer = new Region();
        HBox.setHgrow(loadSpacer, Priority.ALWAYS);
        HBox loadHeader = new HBox(loadLabel, loadSpacer, loadButton);
        loadHeader.setAlignment(Pos.CENTER_LEFT);
        loadHeader.setFillHeight(false);

        loadTable = new TableView<>();
        loadTable.setPlaceholder(new Label("No saved games found."));

        TableColumn<GameSave, String> nameCol = new TableColumn<>("Save name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSaveName()));

        TableColumn<GameSave, String> playerCol = new TableColumn<>("Player");
        playerCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPlayerName()));

        TableColumn<GameSave, String> dateCol = new TableColumn<>("Saved");
        dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSavedAt()));

        loadTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        loadTable.setPrefHeight(150);
        loadTable.getColumns().addAll(List.of(nameCol, playerCol, dateCol));

        for (File file : GameFileHandler.getSavedGames()) {
            try {
                loadTable.getItems().add(GameFileHandler.readSave(file));
            } catch (GameSaveException e) {
                // skip corrupted files
            }
        }

        loadTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> loadButton.setDisable(newVal == null)
        );

        loadTable.setRowFactory(tv -> {
            TableRow<GameSave> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) SoundPlayer.playClick();
            });
            return row;
        });

        VBox loadSection = new VBox(6, loadHeader, loadTable);


        // Start button
        startButton = new Button("START NEW GAME");
        startButton.getStyleClass().add("large-btn");


        // Card layout containing all input fields and the start button
        VBox card = new VBox(10, welcomeLabel, nameSection, capitalSection, stockSection, loadSection, startButton);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(560);
        card.setPadding(new Insets(40));

        // Root layout containing the card
        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("start-view");

        // Create the scene and apply the stylesheet
        scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
            getClass().getResource("/no/ntnu/idatt2003/styles.css").toExternalForm()
        );
    }

    /**
     * Returns the scene for the start view.
     *
     * @return the scene for the start view
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Sets the action for the start button.
     *
     * @param action action to run when start is clicked
     */
    public void setStartAction(Runnable action) {
        startButton.setOnAction(e -> {
            SoundPlayer.playClick();
            action.run();
        });
    }

    /**
     * Sets the action for the file selection button.
     *
     * @param action action to run when selecting stock file
     */
    public void setSelectFileAction(Runnable action) {
        fileButton.setOnAction(e -> {
            SoundPlayer.playClick();
            action.run();
        });
    }

    /**
     * Returns trimmed name input.
     *
     * @return entered name
     */
    public String getNameInput() {
        return nameField.getText().trim();
    }

    /**
     * Returns capital spinner value.
     *
     * @return selected capital
     */
    public int getCapitalInput() {
        return capitalSpinner.getValue();
    }

    /**
     * Shows or hides name validation error.
     *
     * @param visible whether the error should be visible
     */
    public void showNameError(boolean visible) {
        nameError.setVisible(visible);
    }

    /**
     * Shows or hides capital validation error.
     *
     * @param visible whether the error should be visible
     */
    public void showCapitalError(boolean visible) {
        capitalError.setVisible(visible);
    }

    /**
     * Shows a file-related error message.
     *
     * @param message message to display
     */
    public void showFileError(String message) {
        fileError.setText(message);
        fileError.setVisible(true);
    }

    /**
     * Hides file-related error message.
     */
    public void hideFileError() {
        fileError.setVisible(false);
    }

    /**
     * Sets the action for the load game button.
     *
     * @param action action to run when load is clicked
     */
    public void setLoadAction(Runnable action) {
        loadButton.setOnAction(e -> {
            SoundPlayer.playClick();
            action.run();
        });
    }

    /**
     * Returns the currently selected save in the load table, or null if none selected.
     *
     * @return the selected GameSave, or null
     */
    public GameSave getSelectedSave() {
        return loadTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Updates selected file name label.
     *
     * @param fileName selected file name, or empty/null to reset
     */
    public void setSelectedFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            fileNameLabel.setText("No file selected");
            if (!fileNameLabel.getStyleClass().contains("muted-label")) {
                fileNameLabel.getStyleClass().add("muted-label");
            }
            return;
        }

        fileNameLabel.setText(fileName);
        fileNameLabel.getStyleClass().remove("muted-label");
    }
}
