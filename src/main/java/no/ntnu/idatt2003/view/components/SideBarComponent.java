package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Component for the sidebar of the application.
 * Displays the player's current money, buttons for navigating to
 * portfolio and exchange views, and a status display with rank and goals.
 */
public class SideBarComponent extends VBox {

    /** Spacing between elements. */
    private static final int SPACING = 10;

    /** Top margin for the portfolio button. */
    private static final int PORTFOLIO_TOP = 80;

    /** The label displaying the player's current money. */
    private Label moneyLabel;

    /** The button for navigating to the portfolio view. */
    private Button portfolioBtn;

    /** The button for navigating to the exchange view. */
    private Button exchangeBtn;

    /** The button for exiting the application. */
    private Button exitButton;

    /** The component for displaying status information. */
    private StatusComponent statusComponent;

    /**
     * Creates a new SideBarComponent with the given starting money.
     * @param money the initial amount of money to display
     */
    public SideBarComponent(final BigDecimal money) {
        super(SPACING);
        this.getStyleClass().add("sidebar");

        Label moneyTitle = new Label("$ Money");
        moneyTitle.getStyleClass().add("money-title");

        moneyLabel = new Label(money.toString() + " NOK");
        moneyLabel.getStyleClass().add("money-label");

        portfolioBtn = new Button("Portfolio");
        portfolioBtn.getStyleClass().add("primary-btn");
        VBox.setMargin(portfolioBtn, new Insets(PORTFOLIO_TOP, 0, 0, 0));

        exchangeBtn = new Button("Exchange");
        exchangeBtn.getStyleClass().add("primary-btn");

        statusComponent = new StatusComponent();

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        exitButton = new Button("EXIT");
        exitButton.getStyleClass().add("exit-btn");

        this.getChildren().addAll(
            moneyTitle, moneyLabel, portfolioBtn, exchangeBtn,
            statusComponent, spacer, exitButton);
    }

    /**
     * Sets the action to perform when the "Portfolio" button is clicked.
     * @param action the action to perform on click
     */
    public void setPortfolioAction(final Runnable action) {
        portfolioBtn.setOnAction(e -> action.run());
    }

    /**
     * Sets the action to perform when the "Exchange" button is clicked.
     * @param action the action to perform on click
     */
    public void setExchangeAction(final Runnable action) {
        exchangeBtn.setOnAction(e -> action.run());
    }

    /**
     * Sets the action to perform when the "EXIT" button is clicked.
     * @param action the action to perform on click
     */
    public void setExitAction(final Runnable action) {
        exitButton.setOnAction(e -> action.run());
    }

    /**
     * Updates the money label with the new amount of money.
     * @param money the new amount of money to display
     */
    public void updateMoney(final BigDecimal money) {
        moneyLabel.setText(money.toString() + " NOK");
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
    public void updateStatusDisplay(
        final String rank, final String stars,
        final String goal1Text, final boolean goal1Met,
        final String goal2Text, final boolean goal2Met) {
        statusComponent.update(
            rank, stars, goal1Text, goal1Met, goal2Text, goal2Met);
    }
}
