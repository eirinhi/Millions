package no.ntnu.idatt2003.view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.model.entity.Player;

public class GameSummaryView {

    private final Scene scene;

    public GameSummaryView(final Player player) {
        HBox root = new HBox(50);

        VBox content = new VBox(20);
        Label statusLabel = new Label("Achived Status: ");
        String path = imagePath(player.getStatus());
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        content.getChildren().addAll(statusLabel, imageView);

        VBox summary = new VBox(20);
        Label startingMoneyLabel = new Label("Starting Money: ");
        Label endingCapitalLabel = new Label("Ending Capital: ");
        Label totalGainLossLabel = new Label("Total Gain/Loss: ");
        Label totalReturnLabel = new Label("Total Return: ");
        summary.getChildren().addAll(startingMoneyLabel, endingCapitalLabel, totalGainLossLabel, totalReturnLabel);

        root.getChildren().addAll(content, summary);
        scene = new Scene(root, 1000, 700);
    }

    public Scene getScene() {
        return scene;
    }

    private String imagePath(String status) {
        return switch (status) {
            case "Speculator" -> "/no/ntnu/idatt2003/images/three-stars-medal.png";
            case "Investor" -> "/no/ntnu/idatt2003/images/two-stars-medal.png";
            default -> "/no/ntnu/idatt2003/images/one-star-medal.png";
        };
    }
}
