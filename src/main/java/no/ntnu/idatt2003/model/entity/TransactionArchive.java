package no.ntnu.idatt2003.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an archive of transactions in the stock game.
 * The archive allows storing and retrieving transactions
 * based on the week they occurred.
 */
public class TransactionArchive {

    /** List of transactions in the archive. */
    private final List<Transaction> transactions;

    /** Creates an empty transaction archive. */
    public TransactionArchive() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Adds a transaction to the archive.
     *
     * @param transaction the transaction to add
     * @return true if the transaction was added successfully, false otherwise
     */
    public boolean add(final Transaction transaction) {
        if (transaction == null || !transaction.isCommitted()) {
            return false;
        }

        return transactions.add(transaction);
    }

    /**
     * Checks if the archive is empty.
     *
     * @return true if the archive is empty, false otherwise
     */
    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    /**
     * Retrieves all transactions that occurred in the specified week.
     *
     * @param week the week to filter transactions by
     * @return a list of transactions that occurred in the specified week
     */
    public List<Transaction> getTransactions(final int week) {
        return transactions.stream()
                .filter(t -> t.getWeek() == week)
                .toList();
    }

    /**
     * Retrieves all purchase transactions that occurred in the specified week.
     *
     * @param week the week to filter transactions by
     * @return a list of purchase transactions that occurred the specified week
     */
    public List<Purchase> getPurchases(final int week) {
        return transactions.stream()
                .filter(Purchase.class::isInstance)
                .filter(t -> t.getWeek() == week)
                .map(t -> (Purchase) t)
                .toList();
    }

    /**
     * Retrieves all sale transactions that occurred in the specified week.
     *
     * @param week the week to filter transactions by
     * @return a list of sale transactions that occurred the specified week
     */
    public List<Sale> getSales(final int week) {
        return transactions.stream()
                .filter(Sale.class::isInstance)
                .filter(t -> t.getWeek() == week)
                .map(t -> (Sale) t)
                .toList();
    }

    /**
     * Counts the number of distinct weeks in which transactions occurred.
     *
     * @return the number of distinct weeks with transactions
     */
    public int countDistinctWeeks() {
        return (int) transactions.stream()
                .map(Transaction::getWeek)
                .distinct()
                .count();
    }
}
