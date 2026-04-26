package no.ntnu.idatt2003.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Component for displaying the player's current status.
 * Shows the player's rank, star rating, and progress towards goals.
 */
public class StatusComponent extends VBox {

    /** Spacing between elements. */
    private static final int SPACING = 5;

    /** Top margin for the status title. */
    private static final int STATUS_TOP = 40;

    /** Spacing between rank label and stars label. */
    private static final int RANK_SPACING = 10;

    /** The label displaying the player's rank. */
    private Label rankLabel;

    /** The label displaying the player's star rating. */
    private Label starsLabel;

    /** The checkbox for the weekly trading goal. */
    private CheckBox weekGoal;

    /** The checkbox for the net worth growth goal. */
    private CheckBox growthGoal;

    /**
     * Creates a new StatusComponent with default values
     * for rank, stars, and goals.
     */
    public StatusComponent() {
        super(SPACING);

        Label statusTitle = new Label("Status");
        statusTitle.getStyleClass().add("status-title");
        VBox.setMargin(statusTitle, new Insets(STATUS_TOP, 0, 0, 0));

        rankLabel = new Label("Novice");
        rankLabel.getStyleClass().add("rank-label");

        starsLabel = new Label("★ ☆ ☆");
        starsLabel.getStyleClass().add("stars-label");

        HBox rankRow = new HBox(RANK_SPACING, rankLabel, starsLabel);
        rankRow.setAlignment(Pos.BASELINE_LEFT);

        Region line = new Region();
        line.getStyleClass().add("separator-line");

        weekGoal = new CheckBox("Traded for at least 10 weeks");
        growthGoal = new CheckBox("Gained 20% net worth");
        weekGoal.setMouseTransparent(true);
        growthGoal.setMouseTransparent(true);

        this.getChildren().addAll(
            statusTitle, rankRow, line, weekGoal, growthGoal);
    }

    /**
     * Updates the status display with rank, stars, and goal information.
     * @param rank the player's current rank
     * @param stars the player's current star rating
     * @param goal1Text the description of the first goal
     * @param goal1Met whether the first goal has been met
     * @param goal2Text the description of the second goal
     * @param goal2Met whether the second goal has been met
     */
    public void update(
        final String rank, final String stars,
        final String goal1Text, final boolean goal1Met,
        final String goal2Text, final boolean goal2Met) {
        rankLabel.setText(rank);
        starsLabel.setText(stars);
        weekGoal.setText(goal1Text);
        weekGoal.setSelected(goal1Met);
        growthGoal.setText(goal2Text);
        growthGoal.setSelected(goal2Met);
    }
}
