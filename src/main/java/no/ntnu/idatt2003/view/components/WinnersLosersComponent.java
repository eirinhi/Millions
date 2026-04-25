package no.ntnu.idatt2003.view.components;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.model.entity.Stock;

public class WinnersLosersComponent extends VBox {

    VBox winnersList;
    VBox losersList;

    public WinnersLosersComponent(List<Stock> gainers, List<Stock> losers) {
        super(10);

        Label winnersLabel = new Label("Winners");
        winnersList = new VBox(5);
        VBox winnersBox = new VBox(5, winnersLabel, winnersList);

        Label losersLabel = new Label("Losers");
        losersList = new VBox(5);
        VBox losersBox = new VBox(5, losersLabel, losersList);

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
        Label returnLabel = new Label(stock.getWeeklyReturnPercentage() + "%");

        HBox row = new HBox(nameLabel, returnLabel);
        return row;
    }
}
