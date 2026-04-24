package no.ntnu.idatt2003.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class StatusComponent extends VBox {
    
    private Label rankLabel;
    private Label starsLabel;
    private CheckBox weekGoal;
    private CheckBox growthGoal;

    public StatusComponent() {
        super(5);

        Label statusTitle = new Label("Status");
        statusTitle.getStyleClass().add("status-title");
        VBox.setMargin(statusTitle, new Insets(40, 0, 0, 0));

        rankLabel = new Label("Novice");
        rankLabel.getStyleClass().add("rank-label");

        starsLabel = new Label("★ ☆ ☆");
        starsLabel.getStyleClass().add("stars-label");

        HBox rankRow = new HBox(10, rankLabel, starsLabel);
        rankRow.setAlignment(Pos.BASELINE_LEFT);

        Region line = new Region();
        line.getStyleClass().add("separator-line");

        weekGoal = new CheckBox("Traded for at least 10 weeks");
        growthGoal = new CheckBox("Gained 20% net worth");
        weekGoal.setMouseTransparent(true);
        growthGoal.setMouseTransparent(true);

        this.getChildren().addAll(statusTitle, rankRow, line, weekGoal, growthGoal);
    }

    public void update(String rank, String stars, String goal1Text, boolean goal1Met, String goal2Text, boolean goal2Met) {
        rankLabel.setText(rank);
        starsLabel.setText(stars);
        weekGoal.setText(goal1Text);
        weekGoal.setSelected(goal1Met);
        growthGoal.setText(goal2Text);
        growthGoal.setSelected(goal2Met);
    }
}
