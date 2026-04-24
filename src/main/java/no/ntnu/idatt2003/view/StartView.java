package no.ntnu.idatt2003.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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


        // Start button
        startButton = new Button("START GAME");
        startButton.getStyleClass().add("large-btn");


        // Card layout containing all input fields and the start button
        VBox card = new VBox(20, welcomeLabel, nameSection, capitalSection, stockSection, startButton);
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
        startButton.setOnAction(e -> action.run());
    }

    /**
     * Sets the action for the file selection button.
     *
     * @param action action to run when selecting stock file
     */
    public void setSelectFileAction(Runnable action) {
        fileButton.setOnAction(e -> action.run());
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
