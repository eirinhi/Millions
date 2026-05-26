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


    private GameSave(final Builder builder) {
        this.saveName = builder.saveName;
        this.savedAt = builder.savedAt;
        this.playerName = builder.playerName;
        this.balance = builder.balance;
        this.startingBalance = builder.startingBalance;
        this.week = builder.week;
        this.netWorthHistory = builder.netWorthHistory;
        this.watchlistSymbols = builder.watchlistSymbols;
        this.stocks = builder.stocks;
        this.portfolio = builder.portfolio;
        this.transactions = builder.transactions;
    }

    /**
     * Builder for constructing a GameSave instance.
     */
    public static class Builder {
        private String saveName;
        private String savedAt;
        private String playerName;
        private BigDecimal balance;
        private BigDecimal startingBalance;
        private int week;
        private List<BigDecimal> netWorthHistory;
        private List<String> watchlistSymbols;
        private List<StockSave> stocks;
        private List<ShareSave> portfolio;
        private List<TransactionSave> transactions;

        /** Sets the save name. */
        public Builder saveName(final String saveName) {
            this.saveName = saveName;
            return this;
        }

        /** Sets the timestamp. */
        public Builder savedAt(final String savedAt) {
            this.savedAt = savedAt;
            return this;
        }

        /** Sets the player name. */
        public Builder playerName(final String playerName) {
            this.playerName = playerName;
            return this;
        }

        /** Sets the player balance. */
        public Builder balance(final BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        /** Sets the starting balance. */
        public Builder startingBalance(final BigDecimal startingBalance) {
            this.startingBalance = startingBalance;
            return this;
        }

        /** Sets the current week. */
        public Builder week(final int week) {
            this.week = week;
            return this;
        }

        /** Sets the net worth history. */
        public Builder netWorthHistory(final List<BigDecimal> netWorthHistory) {
            this.netWorthHistory = netWorthHistory;
            return this;
        }

        /** Sets the watchlist symbols. */
        public Builder watchlistSymbols(final List<String> watchlistSymbols) {
            this.watchlistSymbols = watchlistSymbols;
            return this;
        }

        /** Sets the stocks. */
        public Builder stocks(final List<StockSave> stocks) {
            this.stocks = stocks;
            return this;
        }

        /** Sets the portfolio. */
        public Builder portfolio(final List<ShareSave> portfolio) {
            this.portfolio = portfolio;
            return this;
        }

        /** Sets the transactions. */
        public Builder transactions(final List<TransactionSave> transactions) {
            this.transactions = transactions;
            return this;
        }

        /**
         * Builds and returns the GameSave.
         *
         * @return a new GameSave instance
         */
        public GameSave build() {
            return new GameSave(this);
        }
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
         * @param symbol  the symbol of the stock
         * @param company the company of the stock
         * @param prices the list of prices for the stock
         * @throws IllegalArgumentException if symbol or company is null/blank, or prices is null
         */
        public StockSave(
            final String symbol,
            final String company,
            final List<BigDecimal> prices
        ) {
            if (symbol == null || symbol.isBlank()) {
                throw new IllegalArgumentException("Symbol cannot be null or blank");
            }
            if (company == null || company.isBlank()) {
                throw new IllegalArgumentException("Company cannot be null or blank");
            }
            if (prices == null) {
                throw new IllegalArgumentException("Prices cannot be null");
            }
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
         * @param symbol        the symbol of the share
         * @param quantity      the quantity of shares
         * @param purchasePrice the purchase price per share
         * @throws IllegalArgumentException if symbol is null/blank, or quantity/purchasePrice is null or non-positive
         */
        public ShareSave(
            final String symbol,
            final BigDecimal quantity,
            final BigDecimal purchasePrice
        ) {
            if (symbol == null || symbol.isBlank()) {
                throw new IllegalArgumentException("Symbol cannot be null or blank");
            }
            if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Purchase price must be positive");
            }
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
         * @param type     the type of the transaction
         * @param symbol   the symbol of the stock involved in the transaction
         * @param quantity the quantity of shares involved in the transaction
         * @param price the price per share of the transaction
         * @param week the week when the transaction occurred
         * @throws IllegalArgumentException if type or symbol is null/blank, quantity/price is null or non-positive, or week is less than 1
         */
        public TransactionSave(
            final String type,
            final String symbol,
            final BigDecimal quantity,
            final BigDecimal price,
            final int week
        ) {
            if (type == null || type.isBlank()) {
                throw new IllegalArgumentException("Type cannot be null or blank");
            }
            if (symbol == null || symbol.isBlank()) {
                throw new IllegalArgumentException("Symbol cannot be null or blank");
            }
            if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price must be positive");
            }
            if (week < 1) {
                throw new IllegalArgumentException("Week must be at least 1");
            }
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
