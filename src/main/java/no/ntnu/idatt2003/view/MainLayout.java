package no.ntnu.idatt2003.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Stock;

public class MainLayout extends BorderPane {
    private final Player player;
    private Label rankLabel;
    private Label starsLabel;
    private CheckBox weekGoal;
    private CheckBox growthGoal;

    // Header label og lokal uke-offset (brukes hvis modellen ikke har metode for å gå til neste uke)
    private Label headerWeekLabel;
    private int weekOffset = 0;
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

        // Header label (tilgjengelig for oppdatering)
        headerWeekLabel = new Label();
        headerWeekLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Next week-knapp til høyre
        Button nextWeekBtn = new Button("Advance ->");
        nextWeekBtn.setStyle("-fx-font-size: 12px;");
        nextWeekBtn.setOnAction(e -> {
            weekOffset++;
            updateHeader();
            updateStatusDisplay();
            // Hvis modellen har en metode for å avanser uke, kall den her i stedet:
            // player.advanceWeek();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerWeekLabel, spacer, nextWeekBtn);
        updateHeader();
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #fcdeecff; -fx-border-color: black; -fx-border-width: 0 1 0 0;");

        // Money selection
        Label moneyTitle = new Label("$ Money");
        moneyTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        Label moneyLabel = new Label(player != null && player.getMoney() != null ? player.getMoney().toString() + " NOK" : "N/A");
        moneyLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Portfolio Button
        Button b = new Button("Portfolio");
        b.setPrefWidth(180);
        b.setPrefHeight(44);
        b.setStyle("-fx-font-size: 16px; -fx-padding: 8 16; -fx-background-color: #f5629dff; -fx-text-fill: white;");
        b.setOnAction(e -> setCenter(new Label("Portfolio trykket")));
        VBox.setMargin(b, new Insets(80, 0, 0, 0));

        // Exchange Button
        Button b2 = new Button("Exchange");
        b2.setPrefWidth(180);
        b2.setPrefHeight(44);
        b2.setStyle("-fx-font-size: 16px; -fx-padding: 8 16; -fx-background-color: #f5629dff; -fx-text-fill: white;");
        b2.setOnAction(e -> setCenter(new Label("Exchange trykket")));

        // Status selection
        Label statusTitle = new Label("Status");
        statusTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-font-family: 'Arial';");
        VBox.setMargin(statusTitle, new Insets(40, 0, 0, 0));

        this.rankLabel = new Label("Novice");
        this.rankLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        this.starsLabel = new Label("★ ☆ ☆");
        this.starsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #f07ab3;");

        HBox rankRow = new HBox(10, rankLabel, starsLabel);
        rankRow.setAlignment(Pos.BASELINE_LEFT);

        Region line = new Region();
        line.setPrefHeight(2);
        line.setStyle("-fx-background-color: #f07ab3;");
        line.setPrefWidth(180);

        this.weekGoal = new CheckBox("Traded for at least 10 weeks");
        this.growthGoal = new CheckBox("Gained 20% net worth");

        this.weekGoal.setMouseTransparent(true);
        this.growthGoal.setMouseTransparent(true);

        // Spacer for å skyve EXIT-knappen til bunnen
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // EXIT button
        Button exitButton = new Button("EXIT");
        exitButton.setPrefWidth(180);
        exitButton.setPrefHeight(44);
        exitButton.setStyle("-fx-font-size: 16px; -fx-padding: 8 16; -fx-background-color: #d9534f; -fx-text-fill: white;");
        exitButton.setOnAction(e -> setCenter(createExitView()));

        sidebar.getChildren().addAll(moneyTitle, moneyLabel, b, b2, statusTitle, rankRow, line, weekGoal, growthGoal, spacer, exitButton);
        updateStatusDisplay();

        return sidebar;
    }

    public void updateStatusDisplay() {
        if (player == null || rankLabel == null) return;

        int weeks = 0;
        try {
            weeks = player.getTransactionArchive().countDistinctWeeks();
        } catch (Exception ignored) {}

        java.math.BigDecimal starting = player.getStartingMoney() != null ? player.getStartingMoney() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal current = player.getMoney() != null ? player.getMoney() : java.math.BigDecimal.ZERO;

        double gainPercent = 0.0;
        if (starting.compareTo(java.math.BigDecimal.ZERO) > 0) {
            java.math.BigDecimal diff = current.subtract(starting);
            java.math.BigDecimal percent = diff.multiply(new java.math.BigDecimal("100"))
                                               .divide(starting, 6, java.math.RoundingMode.HALF_UP);
            gainPercent = percent.doubleValue();
        }

        String rank = "Novice";
        // Oppdater stjerner og mål basert på ukers og prosentvilkår
        boolean tenWeeksMet = weeks >= 10;
        boolean twentyPercentMet = gainPercent >= 20;
        boolean twentyWeeksMet = weeks >= 20;
        boolean hundredPercentMet = gainPercent >= 100;

        if (twentyWeeksMet && hundredPercentMet) {
           rank = "Speculator";
           starsLabel.setText("★ ★ ★");
           weekGoal.setText("Traded for 20 weeks");
           weekGoal.setSelected(true);
           growthGoal.setText("Doubled net worth (100%)");
           growthGoal.setSelected(true);
        } else if (tenWeeksMet && twentyPercentMet) {
            rank = "Investor";
            starsLabel.setText("★ ★ ☆");
            weekGoal.setText("Goal: 20 weeks");
            weekGoal.setSelected(twentyWeeksMet);
            growthGoal.setText("Goal: 100% gain");
            growthGoal.setSelected(hundredPercentMet);
        } else {
            rank = "Novice";
            starsLabel.setText("★ ☆ ☆");
            weekGoal.setSelected(tenWeeksMet);
            growthGoal.setSelected(twentyPercentMet);
        }

        rankLabel.setText(rank);
    }

    public void setView(Node node){
        this.setCenter(node);
    }

    // Legg til denne hjelpe-metoden for plassholderavslutningsside
    private Node createExitView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        Label title = new Label("Avslutningsside");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label info = new Label("Resultater kommer her. Implementer senere for å vise spillerens statistikk.");
        info.setWrapText(true);
        box.getChildren().addAll(title, info);
        return box;
    }

    private void updateHeader() {
        String name = (player != null && player.getName() != null) ? player.getName() : "Ingen spiller tilgjengelig";
        int modelWeeks = 0;
        if (player != null) {
            try {
                modelWeeks = player.getTransactionArchive().countDistinctWeeks();
            } catch (Exception ignored) {}
        }
        int displayWeek = modelWeeks + 1 + weekOffset;
        headerWeekLabel.setText(name + " - Week: " + displayWeek);
    }
}