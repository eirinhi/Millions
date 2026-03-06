package no.ntnu.idatt2003.model.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Purchase;
import no.ntnu.idatt2003.model.entity.Sale;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.entity.Transaction;

/**
 * Represents a stock exchange where players can buy and sell shares.
 */
public class Exchange {

    /** The name of the exchange. */
    private final String name;

    /** The current week of the exchange. */
    private int week;

    /** The map of stocks available on the exchange. */
    private final Map<String, Stock> stockMap;

    /** The random number generator for price changes. */
    private final Random random;

    /** The maximum percentage change in price for a stock. */
    private static final double MAX_PRICE_CHANGE_PERCENT = 0.1;

    /** The scale for random price changes. */
    private static final double RANDOM_SCALE = 2.0;

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
        Transaction transaction = new Purchase(share, week);

        transaction.commit(player);

        return transaction;
    }

    /**
     * Allows a player to sell shares of a stock on the exchange.
     *
     * @param share the share being sold
     * @param player the player making the sale
     * @return a Transaction representing the sale
     */
    public Transaction sell(final Share share, final Player player) {
        Transaction transaction = new Sale(share, week);
        transaction.commit(player);
        return transaction;
    }

    /**
     * Advances the exchange to the next week, updating stock prices randomly.
     */
    public void advance() {
        for (Stock stock : stockMap.values()) {
            BigDecimal currentPrice = stock.getSalesPrice();

            double changePercent = (random.nextDouble()
                                    * RANDOM_SCALE
                                    * MAX_PRICE_CHANGE_PERCENT)
                                    - MAX_PRICE_CHANGE_PERCENT;

            BigDecimal newPrice = currentPrice
                    .add(currentPrice.multiply(
                        BigDecimal.valueOf(changePercent)))
                    .setScale(2, RoundingMode.HALF_UP);

            stock.addNewSalesPrice(newPrice);
        }

        week++;
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
                .sorted((s1, s2) -> s2.getLatestPriceChange()
                .compareTo(s1.getLatestPriceChange()))
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
                .sorted((s1, s2) -> s1.getLatestPriceChange()
                .compareTo(s2.getLatestPriceChange()))
                .limit(limit)
                .toList();
    }
}
