package no.ntnu.idatt2003.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import no.ntnu.idatt2003.view.StartView;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Millions");

        StartView startView = new StartView(primaryStage);
        primaryStage.setScene(startView.getScene());
        primaryStage.show();
    }
}