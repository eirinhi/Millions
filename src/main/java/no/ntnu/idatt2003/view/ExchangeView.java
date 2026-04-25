package no.ntnu.idatt2003.view;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
        SearchBarComponent searchBar = new SearchBarComponent(controller);
        PriceFilterComponent priceFilter = new PriceFilterComponent(controller);
        stockTable = new StockTableComponent(controller.getAllStocks(), controller);
        winnersLosers = new WinnersLosersComponent(controller.getGainers(), controller.getLosers());

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
