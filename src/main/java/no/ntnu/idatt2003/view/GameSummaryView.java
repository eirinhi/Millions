package no.ntnu.idatt2003.view;

import java.math.BigDecimal;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameSummaryView {

    private final Scene scene;

    public GameSummaryView(
        String status,
        BigDecimal startMoney,
        BigDecimal endMoney,
        BigDecimal gain,
        BigDecimal returnPct)
        {

        HBox root = new HBox(50);

        VBox content = new VBox(20);
        Label statusLabel = new Label("Achieved Status: ");
        Label achievedStatus = new Label(status);
        
        Image image = new Image(getClass().getResourceAsStream(imagePath(status)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        content.getChildren().addAll(statusLabel, achievedStatus, imageView);

        VBox summary = new VBox(20);
        Label startingMoneyLabel = new Label("Starting Money: ");
        Label startingMoney = new Label(startMoney + " NOK");

        Label endingCapitalLabel = new Label("Ending Capital: ");
        Label endingCapital = new Label(endMoney + " NOK");

        Label totalGainLossLabel = new Label("Total Gain/Loss: ");
        Label totalGainLoss = new Label(gain + " NOK");
        
        Label totalReturnLabel   = new Label("Total Return: ");
        Label totalReturn = new Label(returnPct + "%");
        
        summary.getChildren().addAll(
            startingMoneyLabel, startingMoney,
            endingCapitalLabel, endingCapital,
            totalGainLossLabel, totalGainLoss,
            totalReturnLabel, totalReturn);

        root.getChildren().addAll(content, summary);
        scene = new Scene(root, 1000, 700);
    }

    public Scene getScene() {
        return scene;
    }

    private String imagePath(String status) {
        return switch (status) {
            case "Speculator" -> "/no/ntnu/idatt2003/images/three-stars-medal.png";
            case "Investor"   -> "/no/ntnu/idatt2003/images/two-stars-medal.png";
            default           -> "/no/ntnu/idatt2003/images/one-star-medal.png";
        };
    }
}
