package no.ntnu.idatt2003.view;

import java.math.BigDecimal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import no.ntnu.idatt2003.view.components.SideBarComponent;
import no.ntnu.idatt2003.view.components.TopBarComponent;

/**
 * Main application view.
 *
 * <p>Contains a header bar (player name, current week, advance button),
 * a sidebar (balance, navigation, status display), and a swappable center area
 * for sub-views such as Portfolio and Exchange.<p>
 */
public class MainView extends BorderPane {

    private TopBarComponent topBar;
    private SideBarComponent sideBar;

    /**
     * Constructs the main view and initialises the header and sidebar.
     *
     * @param playerName  name of the current player, shown in the header
     * @param money       starting balance displayed in the sidebar
     * @param initialWeek the week number shown on first render
     */
    public MainView(String playerName, BigDecimal money, int initialWeek) {
        getStylesheets().add(getClass().getResource("/no/ntnu/idatt2003/styles.css").toExternalForm());
        getStyleClass().add("app-view");

        topBar = new TopBarComponent(playerName, initialWeek);
        sideBar = new SideBarComponent(money);
        this.setTop(topBar);
        this.setLeft(sideBar);

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
        topBar.setAdvanceAction(action);
    }

    /**
     * Sets the action triggered when the "Portfolio" button is clicked.
     *
     * @param action callback to run on click
     */
    public void setPortfolioAction(Runnable action) {
        sideBar.setPortfolioAction(action);
    }

    /**
     * Sets the action triggered when the "Exchange" button is clicked.
     *
     * @param action callback to run on click
     */
    public void setExchangeAction(Runnable action) {
        sideBar.setExchangeAction(action);
    }

    /**
     * Sets the action triggered when the "EXIT" button is clicked.
     *
     * @param action callback to run on click
     */
    public void setExitAction(Runnable action) {
        sideBar.setExitAction(action);
    }

    /**
     * Updates the week label in the header.
     *
     * @param week the new week number to display
     */
    public void updateHeader(int week) {
        topBar.updateWeek(week);
    }

    /**
     * Updates the balance label in the sidebar.
     *
     * @param money the new balance, or {@code null} to display "N/A"
     */
    public void updateMoney(BigDecimal money) {
        sideBar.updateMoney(money);
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
        sideBar.updateStatusDisplay(rank, stars, goal1Text, goal1Met, goal2Text, goal2Met);
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
