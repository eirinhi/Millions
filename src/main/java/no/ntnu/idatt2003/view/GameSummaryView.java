package no.ntnu.idatt2003.view;

import java.math.BigDecimal;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
        root.getStyleClass().add("game-summary-view");

        Label statusLabel = new Label("Achieved Status");
        statusLabel.getStyleClass().add("status-label");

        VBox content = new VBox(20);
        content.setPrefWidth(400);
        Label achievedStatus = new Label(status);
        achievedStatus.setMaxWidth(Double.MAX_VALUE);
        achievedStatus.setAlignment(Pos.CENTER);
        achievedStatus.getStyleClass().add("achieved-status");

        Image image = new Image(getClass().getResourceAsStream(imagePath(status)));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(achievedStatus, imageView);
        content.getStyleClass().add("content-box");

        VBox contentWrapper = new VBox(8, statusLabel, content);
        contentWrapper.setAlignment(Pos.TOP_CENTER);

        VBox summary = new VBox(20);
        summary.setPrefWidth(500);
        HBox.setHgrow(summary, Priority.ALWAYS);
        VBox startingMoney = summaryRow("Starting Money", startMoney + " NOK", null);
        VBox endingCapital = summaryRow("Ending Capital", endMoney + " NOK", null);
        VBox totalGainLoss = summaryRow("Total Gain/Loss", gain + " NOK", gain.compareTo(BigDecimal.ZERO) >= 0);
        VBox totalReturn = summaryRow("Total Return", returnPct + "%", returnPct.compareTo(BigDecimal.ZERO) >= 0);
                
        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);
        Region spacer3 = new Region();
        VBox.setVgrow(spacer3, Priority.ALWAYS);
        Region spacer4 = new Region();
        VBox.setVgrow(spacer4, Priority.ALWAYS);
        Region spacer5 = new Region();
        VBox.setVgrow(spacer5, Priority.ALWAYS);    

        summary.getChildren().addAll(
            spacer1,
            startingMoney,
            spacer2,
            endingCapital,
            spacer3,
            totalGainLoss,
            spacer4,
            totalReturn,
            spacer5
        );
        summary.getStyleClass().add("summary-box");

        root.setFillHeight(false);
        root.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().addAll(contentWrapper, summary);
        scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
            getClass().getResource("/no/ntnu/idatt2003/styles.css").toExternalForm()
        );
        content.prefHeightProperty().bind(scene.heightProperty().multiply(0.4));
        summary.prefHeightProperty().bind(scene.heightProperty().multiply(0.6));
        imageView.fitWidthProperty().bind(scene.widthProperty().multiply(0.15));
        imageView.fitHeightProperty().bind(scene.heightProperty().multiply(0.25));
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

    private VBox summaryRow(String labelText, String valueText, Boolean positive) {
        Label label = new Label(labelText);
        label.getStyleClass().add("summary-label");

        Label value = new Label(valueText);
        value.getStyleClass().add("summary-value");
        if (positive != null) {
            value.getStyleClass().add(positive ? "return-positive" : "return-negative");
        }

        return new VBox(2, label, value);
    }
}
