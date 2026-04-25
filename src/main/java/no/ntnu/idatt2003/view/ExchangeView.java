package no.ntnu.idatt2003.view;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import no.ntnu.idatt2003.controller.ExchangeViewController;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.view.components.PriceFilterComponent;
import no.ntnu.idatt2003.view.components.SearchBarComponent;
import no.ntnu.idatt2003.view.components.StockTableComponent;

public class ExchangeView extends GridPane {

    private final StockTableComponent stockTable;

    public ExchangeView(ExchangeViewController controller) {
        SearchBarComponent searchBar = new SearchBarComponent(controller);
        PriceFilterComponent priceFilter = new PriceFilterComponent(controller);
        stockTable = new StockTableComponent(controller.getAllStocks(), controller);

        this.add(searchBar, 0, 0);
        this.add(priceFilter, 1, 0);
        this.add(stockTable, 0, 1);
        this.add(new Label("Winners and losers"), 1, 1);

        controller.setView(this);
    }

    public void updateStocks(List<Stock> stocks) {
        stockTable.update(stocks);
    }
}
