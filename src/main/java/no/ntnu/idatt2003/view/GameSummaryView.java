package no.ntnu.idatt2003.view;

import java.io.InputStream;
import java.math.BigDecimal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * View for displaying a summary of the player's performance.
 *
 * Displays the achieved status, starting money, ending capital, total
 * gain/loss, and total return percentage, along with a corresponding
 * medal image based on the achieved status.
 */
public class GameSummaryView {

    /** The scene for the game summary view. */
    private final Scene scene;

    /** The button for returning to the main menu. */
    private final Button mainMenuButton;

    /** Spacing between the two main columns. */
    private static final int ROOT_SPACING = 50;

    /** Spacing between elements in the content box. */
    private static final int CONTENT_SPACING = 20;

    /** Preferred width of the content box. */
    private static final int CONTENT_PREF_WIDTH = 400;

    /** Spacing between status label and content box in the wrapper. */
    private static final int WRAPPER_SPACING = 8;

    /** Spacing between elements in the summary box. */
    private static final int SUMMARY_SPACING = 20;

    /** Preferred width of the summary box. */
    private static final int SUMMARY_PREF_WIDTH = 500;

    /** Initial scene width. */
    private static final int SCENE_WIDTH = 1000;

    /** Initial scene height. */
    private static final int SCENE_HEIGHT = 700;

    /** Content box height as a fraction of scene height. */
    private static final double CONTENT_HEIGHT_FACTOR = 0.4;

    /** Summary box height as a fraction of scene height. */
    private static final double SUMMARY_HEIGHT_FACTOR = 0.6;

    /** Image width as a fraction of scene width. */
    private static final double IMAGE_WIDTH_FACTOR = 0.15;

    /** Image height as a fraction of scene height. */
    private static final double IMAGE_HEIGHT_FACTOR = 0.25;

    /**
     * Creates a new game summary view with the given performance.
     *
     * @param status the achieved status
     * @param startMoney the starting money
     * @param endMoney the ending capital
     * @param gain the total gain or loss
     * @param returnPct the total return percentage
     * @param gainPositive whether the gain is positive or negative
     * @param returnPositive whether the return is positive or negative
     */
    public GameSummaryView(
        final String status,
        final BigDecimal startMoney,
        final BigDecimal endMoney,
        final BigDecimal gain,
        final BigDecimal returnPct,
        final boolean gainPositive,
        final boolean returnPositive) {

        HBox root = new HBox(ROOT_SPACING);

        Label statusLabel = new Label("Achieved Status");
        statusLabel.getStyleClass().add("status-label");

        VBox content = new VBox(CONTENT_SPACING);
        content.setPrefWidth(CONTENT_PREF_WIDTH);
        Label achievedStatus = new Label(status);
        achievedStatus.setMaxWidth(Double.MAX_VALUE);
        achievedStatus.setAlignment(Pos.CENTER);
        achievedStatus.getStyleClass().add("achieved-status");

        InputStream imageStream =
            getClass().getResourceAsStream(imagePath(status));
        ImageView imageView = imageStream != null
            ? new ImageView(new Image(imageStream))
            : new ImageView();
        imageView.setPreserveRatio(true);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(achievedStatus, imageView);
        content.getStyleClass().add("content-box");

        VBox contentWrapper = new VBox(WRAPPER_SPACING, statusLabel, content);
        contentWrapper.setAlignment(Pos.TOP_CENTER);

        VBox summary = new VBox(SUMMARY_SPACING);
        summary.setPrefWidth(SUMMARY_PREF_WIDTH);
        HBox.setHgrow(summary, Priority.ALWAYS);
        VBox startingMoney = summaryRow(
            "Starting Money", startMoney + " $", null);
        VBox endingCapital = summaryRow(
            "Ending Capital", endMoney + " $", null);
        VBox totalGainLoss = summaryRow(
            "Total Gain/Loss", gain + " $", gainPositive);
        VBox totalReturn = summaryRow(
            "Total Return", returnPct + "%", returnPositive);

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

        mainMenuButton = new Button("MAIN MENU");
        mainMenuButton.getStyleClass().add("large-btn");
        mainMenuButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(mainMenuButton, new Insets(30, 0, 20, 0));

        summary.getChildren().addAll(
            spacer1, startingMoney,
            spacer2, endingCapital,
            spacer3, totalGainLoss,
            spacer4, totalReturn,
            spacer5
        );
        summary.getStyleClass().add("summary-box");

        root.setFillHeight(false);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setMaxWidth(Double.MAX_VALUE);
        root.getChildren().addAll(contentWrapper, summary);
        VBox.setVgrow(root, Priority.ALWAYS);

        VBox outer = new VBox(10, root, mainMenuButton);
        outer.setAlignment(Pos.CENTER);
        outer.getStyleClass().add("game-summary-view");

        scene = new Scene(outer, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().addAll(
            getClass().getResource("/no/ntnu/idatt2003/styles/variables.css").toExternalForm(),
            getClass().getResource("/no/ntnu/idatt2003/styles/layout.css").toExternalForm(),
            getClass().getResource("/no/ntnu/idatt2003/styles/exchange.css").toExternalForm(),
            getClass().getResource("/no/ntnu/idatt2003/styles/trade.css").toExternalForm(),
            getClass().getResource("/no/ntnu/idatt2003/styles/summary.css").toExternalForm()
        );

        content.prefHeightProperty().bind(
            scene.heightProperty().multiply(CONTENT_HEIGHT_FACTOR));
        summary.prefHeightProperty().bind(
            scene.heightProperty().multiply(SUMMARY_HEIGHT_FACTOR));
        imageView.fitWidthProperty().bind(
            scene.widthProperty().multiply(IMAGE_WIDTH_FACTOR));
        imageView.fitHeightProperty().bind(
            scene.heightProperty().multiply(IMAGE_HEIGHT_FACTOR));
    }

    /**
     * Returns the scene for this game summary view.
     *
     * @return the scene to be displayed
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Sets the action for the main menu button.
     *
     * @param action action to run when the button is clicked
     */
    public void setMainMenuAction(Runnable action) {
        mainMenuButton.setOnAction(e -> {
            SoundPlayer.playClick();
            action.run();
        });
    }

    /**
     * Returns the image path corresponding to the given achieved status.
     *
     * @param status the achieved status
     * @return the image path
     */
    private String imagePath(final String status) {
        return switch (status) {
            case "Speculator" ->
                "/no/ntnu/idatt2003/img/three-stars-medal.png";
            case "Investor" ->
                "/no/ntnu/idatt2003/img/two-stars-medal.png";
            default ->
                "/no/ntnu/idatt2003/img/one-star-medal.png";
        };
    }

    /**
     * Creates a summary row with a label and value.
     *
     * @param labelText the text for the label
     * @param valueText the text for the value
     * @param positive whether the value is positive, negative, or neutral
     * @return the VBox containing the summary row
     */
    private VBox summaryRow(
        final String labelText,
        final String valueText,
        final Boolean positive) {
        Label label = new Label(labelText);
        label.getStyleClass().add("summary-label");

        Label value = new Label(valueText);
        value.getStyleClass().add("summary-value");
        if (positive != null) {
            value.getStyleClass().add(
                positive ? "return-positive" : "return-negative");
        }

        return new VBox(2, label, value);
    }
}
