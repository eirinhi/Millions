package no.ntnu.idatt2003.model.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.entity.Transaction;
import no.ntnu.idatt2003.model.observer.Subject;

/**
 * Represents a stock exchange where players can buy and sell shares.
 */
public class Exchange extends Subject {

    /** The name of the exchange. */
    private final String name;

    /** The current week of the exchange. */
    private int week;

    /** The map of stocks available on the exchange. */
    private final Map<String, Stock> stockMap;

    /** The random number generator for price changes. */
    private final Random random;

    /** The weekly price volatility. */
    private static final double PRICE_VOLATILITY = 0.03;

    /**
     * Creates a new exchange with the given name and list of stocks.
     *
     * @param name the name of the exchange
     * @param stocks the list of stocks available on the exchange
     * @throws IllegalArgumentException if stock/name is null or blank
     */
    public Exchange(final String name, final List<Stock> stocks) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        if (stocks == null) {
            throw new IllegalArgumentException("Stock list cannot be null");
        }

        this.name = name;
        this.week = 1;
        stockMap = new HashMap<>();
        random = new Random();

        for (Stock stock : stocks) {
            stockMap.put(stock.getSymbol(), stock);
        }
    }

    /**
     * Returns the name of the exchange.
     *
     * @return the name of the exchange
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current week of the exchange.
     *
     * @return the current week of the exchange
     */
    public int getWeek() {
        return week;
    }

    /**
     * Checks if the exchange has a stock with the given symbol.
     *
     * @param symbol the stock symbol to check
     * @return true if the stock exists on the exchange, false otherwise
     */
    public boolean hasStock(final String symbol) {
        return stockMap.containsKey(symbol);
    }

    /**
     * Returns the stock with the given symbol, or null if it does not exist.
     *
     * @param symbol the stock symbol
     * @return the stock with the given symbol, or null if it does not exist
     */
    public Stock getStock(final String symbol) {
        return stockMap.get(symbol);
    }

    /**
     * Finds stocks whose symbol or company name contains the given search term.
     *
     * @param searchTerm the term to search for
     * @return a list of stocks that match
     */
    public List<Stock> findStocks(final String searchTerm) {
        if (searchTerm == null) {
            return List.of();
        }

        return stockMap.values().stream()
                .filter(s -> s.getSymbol().toLowerCase()
                    .contains(searchTerm.toLowerCase())
                    || s.getCompany().toLowerCase()
                    .contains(searchTerm.toLowerCase()))
                .toList();
    }

    /**
     * Allows a player to buy shares of a stock on the exchange.
     *
     * @param symbol the symbol of the stock to buy
     * @param quantity the quantity of shares to buy
     * @param player the player making the purchase
     * @return a Transaction representing the purchase
     * @throws IllegalArgumentException if the stock symbol is not found
     */
    public Transaction buy(final String symbol,
                           final BigDecimal quantity,
                           final Player player) {

        if (!stockMap.containsKey(symbol)) {
            throw new IllegalArgumentException("Stock not found");
        }

        Stock stock = getStock(symbol);
        BigDecimal price = stock.getSalesPrice();
        Share share = new Share(stock, quantity, price);

        try {
            Transaction transaction = TransactionFactory.get("purchase", share, week);
            transaction.commit(player);
            notifyObservers();
            return transaction;
        } catch (UnknownTransactionException e) {
            throw new IllegalStateException("Unexpected transaction type", e);
        }
    }

    /**
     * Allows a player to sell shares of a stock on the exchange.
     *
     * @param share the share being sold
     * @param player the player making the sale
     * @return a Transaction representing the sale
     */
    public Transaction sell(final Share share, final Player player) {
        try {
            Transaction transaction = TransactionFactory.get("sale", share, week);
            transaction.commit(player);
            notifyObservers();
            return transaction;
        } catch (UnknownTransactionException e) {
            throw new IllegalStateException("Unexpected transaction type", e);
        }
    }

    /**
     * Allows for a player to sell all shares in their portfolio.
     * All shares in the player's portfolio will be sold at the current price
     * on the exchange, and a list of the sell transactions will be returned.
     *
     * @param player the player selling all their shares
     * @return the list of transactions from selling all shares
     */
    public List<Transaction> sellAll(final Player player) {
        List<Share> shares = new ArrayList<>(player.getPortfolio().getShares());
        List<Transaction> transactions = new ArrayList<>();
        for (Share share : shares) {
            transactions.add(sell(share, player));
        }
        return transactions;
    }

    /**
     * Advances the exchange to the next week, updating stock prices randomly.
     */
    public void advance() {
        for (Stock stock : stockMap.values()) {
            BigDecimal currentPrice = stock.getSalesPrice();

            double changePercent = random.nextGaussian() * PRICE_VOLATILITY + 0.001;

            BigDecimal newPrice = currentPrice
                    .add(currentPrice.multiply(
                        BigDecimal.valueOf(changePercent)))
                    .setScale(2, RoundingMode.HALF_UP);

            stock.addNewSalesPrice(newPrice);
        }

        week++;
        notifyObservers();
    }

    /**
     * Returns the stocks with the highest positive price change.
     * If limit is greater than the number of existing stocks,
     * all available stocks are returned.
     *
     * @param limit the number of stocks to return
     * @return list of top gainers
     */
    public List<Stock> getGainers(final int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }
        return stockMap.values().stream()
                .sorted((s1, s2) -> s2.getWeeklyReturnPercentage()
                .compareTo(s1.getWeeklyReturnPercentage()))
                .limit(limit)
                .toList();
    }

    /**
     * Returns the stocks with the lowest positive price change since last week.
     * If limit is greater than the number of existing stocks,
     * all available stocks are returned.
     *
     * @param limit the number of stocks to return
     * @return list of top losers
     */
    public List<Stock> getLosers(final int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }

        return stockMap.values().stream()
                .sorted((s1, s2) -> s1.getWeeklyReturnPercentage()
                .compareTo(s2.getWeeklyReturnPercentage()))
                .limit(limit)
                .toList();
    }

    /**
     * Returns a list of all stocks available on the exchange.
     *
     * @return a list of all stocks available on the exchange
     */
    public List<Stock> getAllStocks() {
        return stockMap.values().stream().toList();
    }

    /**
     * Returns stocks filtered by search term and price range.
     *
     * @param searchTerm matches against symbol or company name
     * @param minPrice minimum sales price
     * @param maxPrice maximum sales price
     * @return filtered list of stocks
     */
    public List<Stock> getFilteredStocks(
            final String searchTerm,
            final BigDecimal minPrice,
            final BigDecimal maxPrice) {

        return stockMap.values().stream()
            .filter(s -> searchTerm == null || searchTerm.isBlank()
                || s.getSymbol().toLowerCase().contains(searchTerm)
                || s.getCompany().toLowerCase().contains(searchTerm))
            .filter(s -> s.getSalesPrice().compareTo(minPrice) >= 0)
            .filter(s -> s.getSalesPrice().compareTo(maxPrice) <= 0)
            .toList();
    }

    /**
     * Returns stocks filtered by search term and price range,
     * sorted by the given criteria.
     *
     * @param searchTerm matches against symbol or company name
     * @param minPrice minimum sales price
     * @param maxPrice maximum sales price
     * @param sortBy sorting criteria: "priceAsc", "priceDesc", or "name"
     * @return filtered and sorted list of stocks
     */
    public List<Stock> getFilteredAndSortedStocks(
            final String searchTerm,
            final BigDecimal minPrice,
            final BigDecimal maxPrice,
            final String sortBy) {

        List<Stock> filtered = getFilteredStocks(
            searchTerm, minPrice, maxPrice
        );

        return switch (sortBy) {
            case "priceAsc" -> filtered.stream()
                    .sorted((s1, s2) -> s1.getSalesPrice()
                        .compareTo(s2.getSalesPrice()))
                    .toList();
            case "priceDesc" -> filtered.stream()
                    .sorted((s1, s2) -> s2.getSalesPrice()
                        .compareTo(s1.getSalesPrice()))
                    .toList();
            default -> filtered.stream()
                    .sorted((s1, s2) -> s1.getSymbol()
                        .compareTo(s2.getSymbol()))
                    .toList();
        };
    }

    /**
     * Sets the the given week of the exchange.
     *
     * This method is intended for setting the week when loading
     * a saved game.
     *
     * @param week the week to set
     * @throws IllegalArgumentException if week is less than 1
     */
    public void setWeek(final int week) {
        if (week <= 0) {
            throw new IllegalArgumentException("Week cannot be less than 1");
        }
        this.week = week;
    }
}
