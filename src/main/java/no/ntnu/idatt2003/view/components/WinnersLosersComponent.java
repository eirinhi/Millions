package no.ntnu.idatt2003.view.components;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.model.entity.Stock;

public class WinnersLosersComponent extends VBox {

    VBox winnersList;
    VBox losersList;

    public WinnersLosersComponent(List<Stock> gainers, List<Stock> losers) {
        super(10);
        Label winnersLabel = new Label("Winners");
        winnersLabel.getStyleClass().add("winners-title");
        winnersList = new VBox(6);
        VBox winnersBox = new VBox(6, winnersLabel, winnersList);
        winnersBox.getStyleClass().add("winners-box");

        Label losersLabel = new Label("Losers");
        losersLabel.getStyleClass().add("losers-title");
        losersList = new VBox(6);
        VBox losersBox = new VBox(6, losersLabel, losersList);
        losersBox.getStyleClass().add("losers-box");

        this.getChildren().addAll(winnersBox, losersBox);
        update(gainers, losers);
    }

    public void update(List<Stock> gainers, List<Stock> losers) {
        winnersList.getChildren().clear();
        losersList.getChildren().clear();

        for (Stock stock : gainers) {
            winnersList.getChildren().add(createStockRow(stock, true));
        }

        for (Stock stock : losers) {
            losersList.getChildren().add(createStockRow(stock, false));
        }
    }

    private HBox createStockRow(Stock stock, boolean winner) {
        Label nameLabel = new Label(stock.getSymbol());
        nameLabel.getStyleClass().add("stock-row-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String returnText = stock.getWeeklyReturnPercentage().toPlainString() + "%";
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
        return row;
    }
}
