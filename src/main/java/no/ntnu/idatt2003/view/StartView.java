package no.ntnu.idatt2003.view;

import java.io.File;

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

public class StartView {
    public final Scene scene;

    public StartView(Stage primaryStage) {

        Label welcomeLabel = new Label("Welcome to Millions!");
        welcomeLabel.setStyle("-fx-font-size: 40px;");
        welcomeLabel.setPadding(new Insets(0, 0, 40, 0));
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);
        welcomeLabel.setAlignment(Pos.CENTER);


        Label nameLabel = new Label("Name : ");
        nameLabel.setMinWidth(140);
        TextField nameField = new TextField();
        nameField.setPrefWidth(250);
        HBox nameRow = new HBox(10, nameLabel, nameField);
        nameRow.setAlignment(Pos.CENTER_LEFT);


        Label capitalLabel = new Label("Starting capital : ");
        capitalLabel.setMinWidth(140);
        Spinner<Integer> capitalSpinner = new Spinner<>(1000, 100000, 10000, 1000);
        capitalSpinner.setEditable(true);
        capitalSpinner.setPrefWidth(150);
        HBox capitalRow = new HBox(10, capitalLabel, capitalSpinner);
        capitalRow.setAlignment(Pos.CENTER_LEFT);


        Label stockLabel = new Label("Stock data : ");
        stockLabel.setMinWidth(140);
        Button fileButton = new Button("Select file");
        Label fileNameLabel = new Label("No file selected");
        fileNameLabel.setStyle("-fx-text-fill: gray;");
        fileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select stock data file");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileNameLabel.setText(selectedFile.getName());
                fileNameLabel.setStyle("-fx-text-fill: black;");
            }
        });
        HBox stockRow = new HBox(10, stockLabel, fileButton, fileNameLabel);
        stockRow.setAlignment(Pos.CENTER_LEFT);


        Button startButton = new Button("START GAME");
        startButton.setPrefWidth(500);
        startButton.setStyle("-fx-font-size: 26px;");
        startButton.setOnAction(e -> {
            MainLayout mainLayout = new MainLayout(null);
            primaryStage.setScene(new Scene(mainLayout, 1000, 700));
        });


        VBox card = new VBox(20, welcomeLabel, nameRow, capitalRow, stockRow, startButton);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(560);
        card.setPadding(new Insets(40));

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f07ab3;");

        scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
            getClass().getResource("/no/ntnu/idatt2003/styles.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }
}
