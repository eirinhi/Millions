package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SideBarComponent extends VBox{
    
    private Label moneyLabel;
    private Button portfolioBtn;
    private Button exchangeBtn;
    private Button exitButton;
    private StatusComponent statusComponent;

    public SideBarComponent(BigDecimal money) {
        super(10);
        this.getStyleClass().add("sidebar");

        Label moneyTitle = new Label("$ Money");
        moneyTitle.getStyleClass().add("money-title");

        moneyLabel = new Label(money.toString() + " NOK");
        moneyLabel.getStyleClass().add("money-label");

        portfolioBtn = new Button("Portfolio");
        portfolioBtn.getStyleClass().add("primary-btn");
        VBox.setMargin(portfolioBtn, new Insets(80, 0, 0, 0));
        
        exchangeBtn = new Button("Exchange");
        exchangeBtn.getStyleClass().add("primary-btn");

        statusComponent = new StatusComponent();

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        exitButton = new Button("EXIT");
        exitButton.getStyleClass().add("exit-btn");

        this.getChildren().addAll(moneyTitle, moneyLabel, portfolioBtn, exchangeBtn, statusComponent, spacer, exitButton);
    }

    public void setPortfolioAction(Runnable action) {
        portfolioBtn.setOnAction(e -> action.run());
    }

    public void setExchangeAction(Runnable action) {
        exchangeBtn.setOnAction(e -> action.run());
    }

    public void setExitAction(Runnable action) {
        exitButton.setOnAction(e -> action.run());
    }

    public void updateMoney(BigDecimal money) {
        moneyLabel.setText(money.toString() + " NOK");
    }

    public void updateStatusDisplay(String rank, String stars, String goal1Text, boolean goal1Met, String goal2Text, boolean goal2Met) {
        statusComponent.update(rank, stars, goal1Text, goal1Met, goal2Text, goal2Met);
    }
}
