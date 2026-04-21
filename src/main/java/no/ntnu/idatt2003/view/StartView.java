package no.ntnu.idatt2003.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartView {
    public final Scene scene;

    public StartView(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome!");

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            MainLayout mainLayout = new MainLayout(null);
            primaryStage.setScene(new Scene(mainLayout, 1000, 700));
        });

        VBox root = new VBox(20, welcomeLabel, startButton);
        root.setAlignment(Pos.CENTER);

        scene = new Scene(root, 1000, 700);
    }

    public Scene getScene() {
        return scene;
    }
}
