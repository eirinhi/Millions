package no.ntnu.idatt2003.view.components;

import javafx.geometry.Pos;

import no.ntnu.idatt2003.view.SoundPlayer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Component for the top bar of the application.
 * Displays the player's name and current week, and includes a button
 * to advance to the next week.
 */
public class TopBarComponent extends HBox {

    /** The label displaying the current week. */
    private final Label headerWeekLabel;

    /** The name of the player. */
    private final String playerName;

    /** The button to advance to the next week. */
    private Button advanceBtn;

    /**
     * Creates a new TopBarComponent with the given player name
     * and initial week.
     * 
     * @param name        the name of the player to display in the header
     * @param initialWeek the initial week number to display in the header
     */
    public TopBarComponent(final String name, final int initialWeek) {
        this.playerName = name;

        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("header");

        headerWeekLabel = new Label(name + " - Week: " + initialWeek);
        headerWeekLabel.getStyleClass().add("header-week-label");

        advanceBtn = new Button("Advance →");
        advanceBtn.getStyleClass().add("advance-btn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(headerWeekLabel, spacer, advanceBtn);
    }

    /**
     * Sets the action to perform when the "Advance" button is clicked.
     * 
     * @param action the action to perform on click
     */
    public void setAdvanceAction(final Runnable action) {
        advanceBtn.setOnAction(e -> {
            SoundPlayer.playClick();
            action.run();
        });
    }

    /**
     * Updates the week number displayed in the header.
     * 
     * @param week the new week number to display
     */
    public void updateWeek(final int week) {
        headerWeekLabel.setText(playerName + " - Week: " + week);
    }
}
