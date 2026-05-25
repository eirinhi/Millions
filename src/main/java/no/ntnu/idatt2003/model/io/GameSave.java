package no.ntnu.idatt2003.model.io;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Represents the data structure for saving and loading a game state.
 */
public class GameSave implements Serializable {

    /** The name of the save. */
    private final String saveName;
    /** The timestamp when the save was created. */
    private final String savedAt;
    /** The name of the player. */
    private final String playerName;
    /** The current money balance of the player. */
    private final BigDecimal balance;
    /** The starting capital of the player. */
    private final BigDecimal startingBalance;
    /** The current week in the game. */
    private final int week;
    /** The history of the player's net worth. */
    private final List<BigDecimal> netWorthHistory;
    /** The list of watched stock symbols. */
    private final List<String> watchlistSymbols;
    /** The list of stocks in the game. */
    private final List<StockSave> stocks;
    /** The list of shares in the player's portfolio. */
    private final List<ShareSave> portfolio;
    /** The list of transactions in the game. */
    private final List<TransactionSave> transactions;


    /**
     * Creates a new game save object with the specified parameters.
     *
     * @param saveName the name of the save
     * @param savedAt the timestamp when the save was created
     * @param playerName the name of the player
     * @param balance the current money balance of the player
     * @param startingBalance the starting capital of the player
     * @param week the current week in the game
     * @param netWorthHistory the history of the player's net worth
     * @param watchlistSymbols the stock symbols in the player's watchlist
     * @param stocks the list of stocks in the game
     * @param portfolio the list of shares in the player's portfolio
     * @param transactions the list of transactions in the game
     */
    public GameSave(
        final String saveName,
        final String savedAt,
        final String playerName,
        final BigDecimal balance,
        final BigDecimal startingBalance,
        final int week,
        final List<BigDecimal> netWorthHistory,
        final List<String> watchlistSymbols,
        final List<StockSave> stocks,
        final List<ShareSave> portfolio,
        final List<TransactionSave> transactions
    ) {
        this.saveName = saveName;
        this.savedAt = savedAt;
        this.playerName = playerName;
        this.balance = balance;
        this.startingBalance = startingBalance;
        this.week = week;
        this.netWorthHistory = netWorthHistory;
        this.watchlistSymbols = watchlistSymbols;
        this.stocks = stocks;
        this.portfolio = portfolio;
        this.transactions = transactions;
    }

    /** Returns the name of the save. */
    public String getSaveName() { return saveName; }
    
    /** Returns the timestamp when the save was created. */
    public String getSavedAt() { return savedAt; }
    
    /** Returns the name of the player. */
    public String getPlayerName() { return playerName; }
    
    /** Returns the starting capital of the player. */
    public BigDecimal getStartingBalance() { return startingBalance; }
    
    /** Returns the current money balance of the player. */
    public BigDecimal getBalance() { return balance; }
    
    /** Returns the current week in the game. */
    public int getWeek() { return week; }
    
    /** Returns the history of the player's net worth. */
    public List<BigDecimal> getNetWorthHistory() { return netWorthHistory; }

    /** Returns the stock symbols in the player's watchlist. */
    public List<String> getWatchlistSymbols() { return watchlistSymbols; }
    
    /** Returns the list of stocks in the game. */
    public List<StockSave> getStocks() { return stocks; }
    
    /** Returns the list of shares in the player's portfolio. */
    public List<ShareSave> getPortfolio() { return portfolio; }
    
    /** Returns the list of transactions in the game. */
    public List<TransactionSave> getTransactions() { return transactions; }


    /** Represents the data structure for saving and loading a stock. */
    public static class StockSave implements Serializable {
        /** The symbol of the stock. */
        private final String symbol;
        /** The company of the stock. */
        private final String company;
        /** The list of prices for the stock. */
        private final List<BigDecimal> prices;

        /**
         * Creates a new stock save object.
         *
         * @param symbol the symbol of the stock
         * @param company the company of the stock
         * @param prices the list of prices for the stock
         */
        public StockSave(
            final String symbol,
            final String company,
            final List<BigDecimal> prices
        ) {
            this.symbol = symbol;
            this.company = company;
            this.prices = prices;
        }

        /** Returns the symbol of the stock. */
        public String getSymbol() { return symbol; }
        
        /** Returns the company of the stock. */
        public String getCompany() { return company; }
        
        /** Returns the list of prices for the stock. */
        public List<BigDecimal> getPrices() { return prices; }
    }


    /** Represents the data structure for saving and loading a share. */
    public static class ShareSave implements Serializable {
        /** The symbol of the share. */
        private final String symbol;
        /** The quantity of shares. */
        private final BigDecimal quantity;
        /** The purchase price per share. */
        private final BigDecimal purchasePrice;

        /**
         * Creates a new share save object.
         *
         * @param symbol the symbol of the share
         * @param quantity the quantity of shares
         * @param purchasePrice the purchase price per share
         */
        public ShareSave(
            final String symbol,
            final BigDecimal quantity,
            final BigDecimal purchasePrice
        ) {
            this.symbol = symbol;
            this.quantity = quantity;
            this.purchasePrice = purchasePrice;
        }

        /** Returns the symbol of the share. */
        public String getSymbol() { return symbol; }
        
        /** Returns the quantity of shares. */
        public BigDecimal getQuantity() { return quantity; }
        
        /** Returns the purchase price per share. */
        public BigDecimal getPurchasePrice() { return purchasePrice; }
    }


    /** Represents the data structure for saving and loading a transaction. */
    public static class TransactionSave implements Serializable {
        /** The type of the transaction. */
        private final String type;
        /** The symbol of the stock involved in the transaction. */
        private final String symbol;
        /** The quantity of shares involved in the transaction. */
        private final BigDecimal quantity;
        /** The price per share of the transaction. */
        private final BigDecimal price;
        /** The week when the transaction occurred. */
        private final int week;

        /**
         * Creates a new transaction save object.
         *
         * @param type the type of the transaction
         * @param symbol the symbol of the stock involved in the transaction
         * @param quantity the quantity of shares involved in the transaction
         * @param price the price per share of the transaction
         * @param week the week when the transaction occurred
         */
        public TransactionSave(
            final String type,
            final String symbol,
            final BigDecimal quantity,
            final BigDecimal price,
            final int week
        ) {
            this.type = type;
            this.symbol = symbol;
            this.quantity = quantity;
            this.price = price;
            this.week = week;
        }

        /** Returns the type of the transaction. */
        public String getType() { return type; }
        
        /** Returns the symbol of the stock involved in the transaction. */
        public String getSymbol() { return symbol; }
        
        /** Returns the quantity of shares involved in the transaction. */
        public BigDecimal getQuantity() { return quantity; }
        
        /** Returns the price per share of the transaction. */
        public BigDecimal getPrice() { return price; }
        
        /** Returns the week when the transaction occurred. */
        public int getWeek() { return week; }
    }
}
