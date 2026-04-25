package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.model.observer.Observer;
import no.ntnu.idatt2003.view.ExchangeView;

public class ExchangeViewController implements Observer {

    private final Exchange exchange;
    private ExchangeView exchangeView;

    private String searchQuery = "";
    private BigDecimal minPrice = BigDecimal.ZERO;
    private BigDecimal maxPrice = BigDecimal.valueOf(10000);

    public ExchangeViewController(Exchange exchange) {
        this.exchange = exchange;
        exchange.attach(this);
    }

    public void setView(ExchangeView view) {
        this.exchangeView = view;
    }

    public List<Stock> getAllStocks() {
        return exchange.getAllStocks();
    }

    @Override
    public void update() {
        updateTable();
    }

    public void onSearch(String query) {
        this.searchQuery = query.trim().toLowerCase();
        updateTable();
    }

    public void onPriceFilter(BigDecimal min, BigDecimal max) {
        this.minPrice = min;
        this.maxPrice = max;
        updateTable();
    }

    public void updateTable() {
        exchangeView.updateStocks(exchange.getFilteredStocks(searchQuery, minPrice, maxPrice));
    }
}
