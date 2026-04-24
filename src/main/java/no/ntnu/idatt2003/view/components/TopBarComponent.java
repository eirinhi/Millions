package no.ntnu.idatt2003.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class TopBarComponent extends HBox{
    private final Label headerWeekLabel;
    private final String playerName;
    private Button advanceBtn;

    public TopBarComponent(String playerName, int initialWeek) {
        this.playerName = playerName;

        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("header");

        headerWeekLabel = new Label(playerName + " - Week: " + initialWeek);
        headerWeekLabel.getStyleClass().add("header-week-label");

        advanceBtn = new Button("Advance →");
        advanceBtn.getStyleClass().add("advance-btn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(headerWeekLabel, spacer, advanceBtn);
    }

    public void setAdvanceAction(Runnable action) {
        advanceBtn.setOnAction(e -> action.run());
    }

    public void updateWeek(int week) {
        headerWeekLabel.setText(playerName + " - Week: " + week);
    }
}
