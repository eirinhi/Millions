package no.ntnu.idatt2003.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;

public class MainLayout extends BorderPane {
    private final Player player;
    private final List<Stock> stocks;

    public MainLayout(Player player, List<Stock> stocks) {
        this.player = player;
        this.stocks = stocks;

        // Tydelige farger og størrelser for testing
        this.setTop(createHeader());
        this.setLeft(createSidebar());
        Label center = new Label("Welcome to Millions!");
        center.setStyle("-fx-font-size:20px; -fx-background-color: #ffffff; -fx-padding: 20;");
        this.setCenter(center);
        this.setPadding(new Insets(5));
        this.setStyle("-fx-background-color: #eeeeee;");
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10));
        header.setPrefHeight(60);
        header.setStyle("-fx-background-color: linear-gradient(#ff9ac6, #f07ab3); -fx-border-color: black; -fx-border-width: 0 0 1 0;");

        String title = "Ingen spiller tilgjengelig";
        if (player != null) {
            try {
                title = player.getName() + " - Week: " + (player.getTransactionArchive().countDistinctWeeks() + 1);
            } catch (Exception ignored) {}
        }
        Label weekLabel = new Label(title);
        weekLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        header.getChildren().add(weekLabel);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #fcdeecff; -fx-border-color: black; -fx-border-width: 0 1 0 0;");

        Label moneyTitle = new Label("$ Money");
        moneyTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        Label moneyLabel = new Label(player != null && player.getMoney() != null ? player.getMoney().toString() + " NOK" : "N/A");
        moneyLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button b = new Button("Test-knapp");
        // Gjør knappen større
        b.setPrefWidth(180);
        b.setPrefHeight(44);
        b.setStyle("-fx-font-size: 16px; -fx-padding: 8 16;");

        b.setOnAction(e -> setCenter(new Label("Knapp trykket")));
        VBox.setMargin(b, new Insets(100, 0, 0, 0)); // flytter knappen 20px ned

        sidebar.getChildren().addAll(moneyTitle, moneyLabel, b);
        return sidebar;
    }

    public void setView(Node node){
        this.setCenter(node);
    }
}