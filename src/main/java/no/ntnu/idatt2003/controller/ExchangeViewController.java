package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.model.observer.Observer;
import no.ntnu.idatt2003.view.ExchangeView;

/**
 * Controller for the exchange view.
 * Handles user interactions and updates the view.
 */
public class ExchangeViewController implements Observer {

    /** The exchange. */
    private final Exchange exchange;

    /** The view for the exchange. */
    private ExchangeView exchangeView;


    /** The search query. */
    private String searchQuery = "";

    /** The minimum price for filtering. */
    private BigDecimal minPrice = BigDecimal.ZERO;

    /** The maximum price for filtering. */
    private BigDecimal maxPrice = BigDecimal.valueOf(DEFAULT_MAX_PRICE);

    /** The sorting criteria. */
    private String sortBy = "name";

    /** The limit for the top lists. */
    private static final int TOP_LIST_LIMIT = 5;

    /** The default maximum price for filtering. */
    private static final long DEFAULT_MAX_PRICE = 10000;

    /**
     * Creates a new controller for the exchange view.
     *
     * @param initialExchange the exchange to control
     */
    public ExchangeViewController(final Exchange initialExchange) {
        this.exchange = initialExchange;
        this.exchange.attach(this);
    }

    /**
     * Sets the view for this controller.
     *
     * @param view the view to set
     */
    public void setView(final ExchangeView view) {
        this.exchangeView = view;
    }

    /**
     * Returns the list of all stocks from the exchange.
     * @return the list of all stocks
     */
    public List<Stock> getAllStocks() {
        return exchange.getAllStocks();
    }

    /**
     * Updates the view when the exchange changes.
     */
    @Override
    public void update() {
        updateTable();
    }

    /**
     * Handles the search action by updating the search query
     * and refreshing the table.
     * @param query the search query entered by the user
     */
    public void onSearch(final String query) {
        this.searchQuery = query.trim().toLowerCase();
        updateTable();
    }

    /**
     * Handles the price filter action by updating the price range
     * and refreshing the table.
     * @param min the minimum price
     * @param max the maximum price
     */
    public void onPriceFilter(final BigDecimal min, final BigDecimal max) {
        this.minPrice = min;
        this.maxPrice = max;
        updateTable();
    }

    /**
     * Handles the sort action by updating the sorting criteria
     * and refreshing the table.
     */
    public void updateTable() {
        List<Stock> stocks = exchange.getFilteredStocks(
                searchQuery, minPrice, maxPrice);

        List<Stock> sorted = switch (sortBy) {
            case "priceAsc" -> stocks.stream()
                    .sorted((s1, s2) -> s1.getSalesPrice()
                        .compareTo(s2.getSalesPrice()))
                    .toList();
            case "priceDesc" -> stocks.stream()
                    .sorted((s1, s2) -> s2.getSalesPrice()
                        .compareTo(s1.getSalesPrice()))
                    .toList();
            default -> stocks.stream()
                    .sorted((s1, s2) -> s1.getSymbol()
                        .compareTo(s2.getSymbol()))
                    .toList();
        };

        exchangeView.updateStocks(sorted);
        exchangeView.updateWinnersLosers(
                exchange.getGainers(TOP_LIST_LIMIT),
                exchange.getLosers(TOP_LIST_LIMIT));
    }

    /**
     * Returns the top gainers from the exchange.
     * @return the list of top gainers
     */
    public List<Stock> getGainers() {
        return exchange.getGainers(TOP_LIST_LIMIT);
    }

    /**
     * Returns the top losers from the exchange.
     * @return the list of top losers
     */
    public List<Stock> getLosers() {
        return exchange.getLosers(TOP_LIST_LIMIT);
    }

    /**
     * Handles the sort action by updating the sorting criteria
     * and refreshing the table.
     * @param sort the sorting criteria selected by the user
     */
    public void onSort(final String sort) {
        this.sortBy = sort;
        updateTable();
    }
}
