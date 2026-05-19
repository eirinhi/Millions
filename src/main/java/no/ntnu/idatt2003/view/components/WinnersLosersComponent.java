package no.ntnu.idatt2003.view.components;

import java.util.List;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.ExchangeViewController;
import no.ntnu.idatt2003.model.entity.Stock;

/**
 * Component for displaying the winners and losers in the stock exchange.
 */
public class WinnersLosersComponent extends VBox {

    /** Spacing between elements. */
    private static final int SPACING = 10;

    /** Spacing between elements in the winners/losers boxes. */
    private static final int BOX_SPACING = 6;

    /** The list displaying the winning stocks. */
    private VBox winnersList;

    /** The list displaying the losing stocks. */
    private VBox losersList;

    /** The controller notified when a stock is selected. */
    private final ExchangeViewController controller;

    /**
     * Creates a new WinnersLosersComponent with the given gainers and losers.
     * @param gainers the list of stocks that gained the most last week
     * @param losers the list of stocks that lost the most last week
     * @param controller the controller handling stock selections
     */
    public WinnersLosersComponent(
        final List<Stock> gainers,
        final List<Stock> losers,
        final ExchangeViewController controller) {
        super(SPACING);

        this.controller = controller;

        Label winnersLabel = new Label("Winners");
        winnersLabel.getStyleClass().add("winners-title");
        winnersList = new VBox(BOX_SPACING);
        VBox winnersBox = new VBox(BOX_SPACING, winnersLabel, winnersList);
        winnersBox.getStyleClass().add("winners-box");

        Label losersLabel = new Label("Losers");
        losersLabel.getStyleClass().add("losers-title");
        losersList = new VBox(BOX_SPACING);
        VBox losersBox = new VBox(BOX_SPACING, losersLabel, losersList);
        losersBox.getStyleClass().add("losers-box");

        this.getChildren().addAll(winnersBox, losersBox);
        update(gainers, losers);
    }

    /**
     * Updates the winners and losers lists with new data.
     * @param gainers the new list of winning stocks to display
     * @param losers the new list of losing stocks to display
     */
    public void update(
        final List<Stock> gainers, final List<Stock> losers) {
        winnersList.getChildren().clear();
        losersList.getChildren().clear();

        for (Stock stock : gainers) {
            winnersList.getChildren().add(createStockRow(stock, true));
        }

        for (Stock stock : losers) {
            losersList.getChildren().add(createStockRow(stock, false));
        }
    }

    /**
     * Creates a row for a stock in the winners or losers list.
     * @param stock the stock to create a row for
     * @param winner whether the stock is a winner or loser
     * @return an HBox representing the row for the stock
     */
    private HBox createStockRow(final Stock stock, final boolean winner) {
        Label nameLabel = new Label(stock.getSymbol());
        nameLabel.getStyleClass().add("stock-row-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String returnText =
            stock.getWeeklyReturnPercentage().toPlainString() + "%";
        if (winner) {
            returnText = "+" + returnText;
        }
        Label returnLabel = new Label(returnText);

        String returnStyle = "return-negative";
        if (winner) {
            returnStyle = "return-positive";
        }
        returnLabel.getStyleClass().addAll("return-label", returnStyle);

        HBox row = new HBox(nameLabel, spacer, returnLabel);
        row.getStyleClass().add("stock-row");
        row.setCursor(Cursor.HAND);
        row.setOnMouseClicked(event -> controller.onStockSelected(stock));
        return row;
    }
}
