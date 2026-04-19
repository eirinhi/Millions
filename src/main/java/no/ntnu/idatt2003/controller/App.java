package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.view.MainLayout;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Player player = new Player("Testspiller", new BigDecimal("10000"));
        MainLayout mainLayout = new MainLayout(player);
        Scene scene = new Scene(mainLayout, 900, 600);

        primaryStage.setTitle("Millions");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
