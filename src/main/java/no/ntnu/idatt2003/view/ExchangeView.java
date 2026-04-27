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

/**
 * View for the stock exchange.
 * Displays a search bar, price filter, stock table, and winners/losers list.
 */
public class ExchangeView extends GridPane {

    /** Gap between grid cells. */
    private static final int GAP = 16;

    /** Padding around the grid. */
    private static final int PADDING = 24;

    /** The stock table component. */
    private final StockTableComponent stockTable;

    /** The winners/losers component. */
    private final WinnersLosersComponent winnersLosers;

    /**
     * Constructs the ExchangeView with the given controller.
     * @param controller the controller for this view
     */
    public ExchangeView(final ExchangeViewController controller) {
        this.setHgap(GAP);
        this.setVgap(GAP);
        this.setPadding(new Insets(PADDING));

        // Initialize components
        SearchBarComponent searchBar = new SearchBarComponent(controller);
        PriceFilterComponent priceFilter =
            new PriceFilterComponent(controller);
        stockTable = new StockTableComponent(
            controller.getAllStocks(), controller);
        winnersLosers = new WinnersLosersComponent(
            controller.getGainers(), controller.getLosers());

        // Set up layout constraints
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

        // Add components to the grid
        this.add(searchBar, 0, 0);
        this.add(priceFilter, 1, 0);
        this.add(stockTable, 0, 1);
        this.add(winnersLosers, 1, 1);

        controller.setView(this);
    }

    /**
     * Updates the stock table with the given list of stocks.
     * @param stocks the list of stocks to display
     */
    public void updateStocks(final List<Stock> stocks) {
        stockTable.update(stocks);
    }

    /**
     * Updates the winners and losers lists.
     * @param gainers the list of gaining stocks
     * @param losers the list of losing stocks
     */
    public void updateWinnersLosers(
        final List<Stock> gainers, final List<Stock> losers) {
        winnersLosers.update(gainers, losers);
    }
}
