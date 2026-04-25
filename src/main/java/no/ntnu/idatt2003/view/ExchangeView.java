package no.ntnu.idatt2003.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import no.ntnu.idatt2003.controller.ExchangeViewController;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.view.components.PriceFilterComponent;
import no.ntnu.idatt2003.view.components.SearchBarComponent;
import no.ntnu.idatt2003.view.components.StockTableComponent;
import no.ntnu.idatt2003.view.components.WinnersLosersComponent;

public class ExchangeView extends GridPane {

    private final StockTableComponent stockTable;
    private final WinnersLosersComponent winnersLosers;

    public ExchangeView(ExchangeViewController controller) {
        this.setHgap(16);
        this.setVgap(16);
        this.setPadding(new Insets(24));

        SearchBarComponent searchBar = new SearchBarComponent(controller);
        PriceFilterComponent priceFilter = new PriceFilterComponent(controller);
        stockTable = new StockTableComponent(controller.getAllStocks(), controller);
        winnersLosers = new WinnersLosersComponent(controller.getGainers(), controller.getLosers());

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        this.getColumnConstraints().addAll(col0, col1);

        RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.NEVER);
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        this.getRowConstraints().addAll(row0, row1);

        this.add(searchBar, 0, 0);
        this.add(priceFilter, 1, 0);
        this.add(stockTable, 0, 1);
        this.add(winnersLosers, 1, 1);

        controller.setView(this);
    }

    public void updateStocks(List<Stock> stocks) {
        stockTable.update(stocks);
    }

    public void updateWinnersLosers(List<Stock> gainers, List<Stock> losers) {
        winnersLosers.update(gainers, losers);
    }
}
