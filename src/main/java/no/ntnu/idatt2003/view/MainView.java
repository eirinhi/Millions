package no.ntnu.idatt2003.view;

import java.math.BigDecimal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * Main application view.
 *
 * <p>Contains a header bar (player name, current week, advance button),
 * a sidebar (balance, navigation, status display), and a swappable center area
 * for sub-views such as Portfolio and Exchange.<p>
 */
public class MainView extends BorderPane {

    private final String playerName;
    private Label headerWeekLabel;
    private Label moneyLabel;
    private Label rankLabel;
    private Label starsLabel;
    private CheckBox weekGoal;
    private CheckBox growthGoal;
    private Button nextWeekBtn;
    private Button portfolioBtn;
    private Button exchangeBtn;
    private Button exitButton;

    /**
     * Constructs the main view and initialises the header and sidebar.
     *
     * @param playerName  name of the current player, shown in the header
     * @param money       starting balance displayed in the sidebar
     * @param initialWeek the week number shown on first render
     */
    public MainView(String playerName, BigDecimal money, int initialWeek) {
        this.playerName = playerName;
        getStylesheets().add(getClass().getResource("/no/ntnu/idatt2003/styles.css").toExternalForm());
        getStyleClass().add("app-view");

        this.setTop(createHeader(initialWeek));
        this.setLeft(createSidebar(money));

        Label center = new Label("Welcome to Millions!");
        center.getStyleClass().add("welcome-label");
        this.setCenter(center);
    }

    /**
     * Sets the action triggered when the "Advance" button is clicked.
     *
     * @param action callback to run on click
     */
    public void setAdvanceAction(Runnable action) {
        nextWeekBtn.setOnAction(e -> action.run());
    }

    /**
     * Sets the action triggered when the "Portfolio" button is clicked.
     *
     * @param action callback to run on click
     */
    public void setPortfolioAction(Runnable action) {
        portfolioBtn.setOnAction(e -> action.run());
    }

    /**
     * Sets the action triggered when the "Exchange" button is clicked.
     *
     * @param action callback to run on click
     */
    public void setExchangeAction(Runnable action) {
        exchangeBtn.setOnAction(e -> action.run());
    }

    /**
     * Sets the action triggered when the "EXIT" button is clicked.
     *
     * @param action callback to run on click
     */
    public void setExitAction(Runnable action) {
        exitButton.setOnAction(e -> action.run());
    }

    /**
     * Builds the top header bar containing the player/week label and the advance button.
     *
     * @param initialWeek the week number to display initially
     * @return the constructed header node
     */
    private HBox createHeader(int initialWeek) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header");

        headerWeekLabel = new Label(playerName + " - Week: " + initialWeek);
        headerWeekLabel.getStyleClass().add("header-week-label");

        nextWeekBtn = new Button("Advance ->");
        nextWeekBtn.getStyleClass().add("advance-btn");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerWeekLabel, spacer, nextWeekBtn);
        return header;
    }

    /**
     * Builds the left sidebar containing balance, navigation buttons, and status display.
     *
     * @param money the player's current balance shown on construction
     * @return the constructed sidebar node
     */
    private VBox createSidebar(BigDecimal money) {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");

        Label moneyTitle = new Label("$ Money");
        moneyTitle.getStyleClass().add("money-title");

        moneyLabel = new Label(money != null ? money.toString() + " NOK" : "N/A");
        moneyLabel.getStyleClass().add("money-label");

        portfolioBtn = new Button("Portfolio");
        portfolioBtn.getStyleClass().add("primary-btn");
        VBox.setMargin(portfolioBtn, new Insets(80, 0, 0, 0));

        exchangeBtn = new Button("Exchange");
        exchangeBtn.getStyleClass().add("primary-btn");

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

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        exitButton = new Button("EXIT");
        exitButton.getStyleClass().add("danger-btn");

        sidebar.getChildren().addAll(moneyTitle, moneyLabel, portfolioBtn, exchangeBtn, statusTitle, rankRow, line, weekGoal, growthGoal, spacer, exitButton);
        return sidebar;
    }

    /**
     * Updates the week label in the header.
     *
     * @param week the new week number to display
     */
    public void updateHeader(int week) {
        headerWeekLabel.setText(playerName + " - Week: " + week);
    }

    /**
     * Updates the balance label in the sidebar.
     *
     * @param money the new balance, or {@code null} to display "N/A"
     */
    public void updateMoney(BigDecimal money) {
        moneyLabel.setText(money != null ? money.toString() + " NOK" : "N/A");
    }

    /**
     * Updates the rank, stars, and goal checkboxes in the sidebar status section.
     *
     * @param rank      the player's current rank label (e.g. "Novice", "Investor")
     * @param stars     star string reflecting the rank (e.g. "★ ★ ☆")
     * @param goal1Text description text for the first goal
     * @param goal1Met  whether the first goal has been reached
     * @param goal2Text description text for the second goal
     * @param goal2Met  whether the second goal has been reached
     */
    public void updateStatusDisplay(String rank, String stars, String goal1Text, boolean goal1Met, String goal2Text, boolean goal2Met) {
        rankLabel.setText(rank);
        starsLabel.setText(stars);
        weekGoal.setText(goal1Text);
        weekGoal.setSelected(goal1Met);
        growthGoal.setText(goal2Text);
        growthGoal.setSelected(goal2Met);
    }

    /**
     * Replaces the center area with the exit/results view.
     */
    public void showExitView() {
        setView(createExitView());
    }

    /**
     * Replaces the center area with the given node.
     *
     * @param node the node to display in the center
     */
    public void setView(Node node) {
        this.setCenter(node);
    }

    /**
     * Builds a placeholder exit view shown when the player ends the game.
     *
     * @return the constructed exit view node
     */
    private Node createExitView() {
        VBox box = new VBox(10);
        box.getStyleClass().add("exit-view");
        Label title = new Label("Avslutningsside");
        title.getStyleClass().add("exit-view-title");
        Label info = new Label("Resultater kommer her. Implementer senere for å vise spillerens statistikk.");
        info.setWrapText(true);
        box.getChildren().addAll(title, info);
        return box;
    }
}
