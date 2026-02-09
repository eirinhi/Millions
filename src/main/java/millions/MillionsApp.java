package millions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MillionsApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Velkommen til Millions!");
        Scene scene = new Scene(label, 300, 200);

        primaryStage.setTitle("Millions");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
