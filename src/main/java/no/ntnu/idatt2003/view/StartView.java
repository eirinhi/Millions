package no.ntnu.idatt2003.view;

import java.io.File;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import no.ntnu.idatt2003.controller.StartController;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;

/**
 * StartView is the initial view of the application.
 * The user must input their name, a starting capital, and a stock
 * data file before starting the game.
 */
public class StartView {

    /** The scene for the start view. */
    private final Scene scene;

    /**
     * Constructs the StartView with the given primary stage.
     *
     * @param primaryStage the primary stage of the application
     */
    public StartView(Stage primaryStage) {

        // Welcome label
        Label welcomeLabel = new Label("Welcome to Millions!");
        welcomeLabel.setStyle("-fx-font-size: 40px;");
        welcomeLabel.setPadding(new Insets(0, 0, 40, 0));
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);
        welcomeLabel.setAlignment(Pos.CENTER);


        // Name input
        Label nameLabel = new Label("Name : ");
        nameLabel.setMinWidth(140);
        TextField nameField = new TextField();
        nameField.setPrefWidth(250);

        Label nameError = new Label("Name cannot be empty.");
        nameError.setStyle("-fx-text-fill: #810505; -fx-font-weight: bold; -fx-font-size: 12px;");
        nameError.setVisible(false);

        HBox nameRow = new HBox(10, nameLabel, nameField);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        VBox nameSection = new VBox(2, nameRow, nameError);
        nameSection.setAlignment(Pos.CENTER);


        // Capital input
        Label capitalLabel = new Label("Starting capital : ");
        capitalLabel.setMinWidth(140);
        Spinner<Integer> capitalSpinner = new Spinner<>(1000, 100000, 10000, 1000);
        capitalSpinner.setEditable(true);
        capitalSpinner.setPrefWidth(150);

        Label capitalError = new Label("Capital must be between 1000 and 100000.");
        capitalError.setStyle("-fx-text-fill: #810505; -fx-font-weight: bold; -fx-font-size: 12px;");
        capitalError.setVisible(false);

        HBox capitalRow = new HBox(10, capitalLabel, capitalSpinner);
        capitalRow.setAlignment(Pos.CENTER_LEFT);
        VBox capitalSection = new VBox(2, capitalRow, capitalError);
        capitalSection.setAlignment(Pos.CENTER);


        // Stock file input
        Label stockLabel = new Label("Stock data : ");
        stockLabel.setMinWidth(140);

        final File[] selectedFile = {null};

        Button fileButton = new Button("Select file");
        Label fileNameLabel = new Label("No file selected");
        fileNameLabel.setStyle("-fx-text-fill: gray;");
        fileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select stock data file");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            File choosen = fileChooser.showOpenDialog(primaryStage);
            if (choosen != null) {
                selectedFile[0] = choosen;
                fileNameLabel.setText(choosen.getName());
                fileNameLabel.setStyle("-fx-text-fill: black;");
            }
        });

        Label fileError = new Label("Please select a stock data file.");
        fileError.setStyle("-fx-text-fill: #810505; -fx-font-weight: bold; -fx-font-size: 12px;");
        fileError.setVisible(false);

        HBox stockRow = new HBox(10, stockLabel, fileButton, fileNameLabel);
        stockRow.setAlignment(Pos.CENTER_LEFT);
        VBox stockSection = new VBox(2, stockRow, fileError);
        stockSection.setAlignment(Pos.CENTER);


        // Start button
        Button startButton = new Button("START GAME");
        startButton.setPrefWidth(500);
        startButton.setStyle("-fx-font-size: 26px;");
        startButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            int capital = capitalSpinner.getValue();

            nameError.setVisible(name.isBlank());
            capitalError.setVisible(capital < 1000 || capital > 100000);
            fileError.setVisible(selectedFile[0] == null);

            if (StartController.validateInput(name, capital, selectedFile[0])) {
                List<Stock> stocks = StartController.loadStocks(selectedFile[0]);
                if (stocks == null) {
                    fileError.setText("Could not read stock file.");
                    fileError.setVisible(true);
                } else {
                    Player player = StartController.createPlayer(name, capital);
                    MainLayout mainLayout = new MainLayout(player, stocks);
                    primaryStage.setScene(new Scene(mainLayout, 1000, 700));
                }
            }
        });


        // Card layout containing all input fields and the start button
        VBox card = new VBox(20, welcomeLabel, nameSection, capitalSection, stockSection, startButton);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(560);
        card.setPadding(new Insets(40));

        // Root layout containing the card
        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f07ab3;");

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
}
