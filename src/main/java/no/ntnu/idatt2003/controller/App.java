package no.ntnu.idatt2003.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import no.ntnu.idatt2003.view.StartView;

/**
 * Main application class for the Millions game.
 *
 * <p>Initializes the JavaFX application and displays the start view.</p>
 */
public class App extends Application {

    /**
     * Main method to launch the JavaFX application.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application.
     * Sets the title of the primary stage and displays the start view.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Millions");

        StartView startView = new StartView(primaryStage);
        primaryStage.setScene(startView.getScene());
        primaryStage.show();
    }
}