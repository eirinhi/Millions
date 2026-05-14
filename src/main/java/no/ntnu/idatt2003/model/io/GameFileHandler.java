package no.ntnu.idatt2003.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Portfolio;
import no.ntnu.idatt2003.model.entity.Purchase;
import no.ntnu.idatt2003.model.entity.Sale;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.entity.Transaction;
import no.ntnu.idatt2003.model.entity.TransactionArchive;
import no.ntnu.idatt2003.model.logic.Exchange;

/**
 * Handles saving and loading of game state to and from files using serialization.
 */
public class GameFileHandler {

    /** The directory where files are stored. */
    private static final String SAVES_DIR = "saves";

    /** The formatter for timestamp in save file names. */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Saves the current game state to a file in the saves/ directory.
     *
     * @param saveName the name given to the save file by the user
     * @param player the player whose state is being saved
     * @param exchange the current exchange
     * @throws GameSaveException if the file could not be written
     */
    public static void saveGame(
        final String saveName,
        final Player player,
        final Exchange exchange
    ) throws GameSaveException {
        File dir = new File(SAVES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = LocalDate.now().format(FORMATTER);

        // Builds the list of stocks
        List<GameSave.StockSave> stocks = new ArrayList<>();
        for (Stock stock : exchange.getAllStocks()) {
            stocks.add( new GameSave.StockSave(
                stock.getSymbol(),
                stock.getCompany(),
                stock.getHistoricalPrices()
            ));
        }

        // Builds the portfolio
        List<GameSave.ShareSave> portfolio = new ArrayList<>();
        for (Share share : player.getPortfolio().getShares()) {
            portfolio.add(new GameSave.ShareSave(
                share.getStock().getSymbol(),
                share.getQuantity(),
                share.getPurchasePrice()
            ));
        }

        // Builds the list of transactions
        List<GameSave.TransactionSave> transactions = new ArrayList<>();
        for (Transaction t : player.getTransactionArchive().getAll()) {
            transactions.add(new GameSave.TransactionSave(
                t instanceof Purchase ? "purchase" : "sale",
                t.getShare().getStock().getSymbol(),
                t.getShare().getQuantity(),
                t.getShare().getPurchasePrice(),
                t.getWeek()
            ));
        }

        // Creates the GameSave object to be serialized
        GameSave save = new GameSave(
            saveName,
            timestamp,
            player.getName(),
            player.getMoney(),
            player.getStartingMoney(),
            exchange.getWeek(),
            player.getNetWorthHistory(),
            stocks,
            portfolio,
            transactions
        );

        String filename = saveName + " (" + timestamp + ").sav";
        filename = filename.replaceAll("[/:*?\"<>|]", "_");
        File file = new File(dir, filename);

        try (FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(save);
            } catch (FileNotFoundException e) {
                throw new GameSaveException(
                    "Could not find save directory: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new GameSaveException(
                    "Could not write save file: " + e.getMessage(), e);
        }
    }


    /**
     * Loads a saved game state from file.
     *
     * @param file the file to load the game state from
     * @return a GameLoadResult containing the reconstructed player and exchange
     * @throws GameSaveException if the file could not be read or is corrupted
     */
    public static GameLoadResult loadGame(final File file) throws GameSaveException {
        GameSave save;

        try (FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
                save = (GameSave) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new GameSaveException(
                "Save file not found: " + e.getMessage(), e);
        } catch (ClassNotFoundException | IOException e) {
            throw new GameSaveException(
                "Could not read save file: " + e.getMessage(), e);
        }

        // Reconstructs the stocks
        List<Stock> stocks = new ArrayList<>();
        for (GameSave.StockSave s : save.getStocks()) {
            stocks.add(new Stock(s.getSymbol(), s.getCompany(), s.getPrices()));
        }

        // Reconstructs the exchange
        Exchange exchange = new Exchange("Millions Exchange", stocks);
        exchange.setWeek(save.getWeek());

        // Reconstructs the portfolio
        Portfolio portfolio = new Portfolio();
        for (GameSave.ShareSave sh : save.getPortfolio()) {
            Stock stock = exchange.getStock(sh.getSymbol());
            if (stock != null) {
                portfolio.addShare(
                    new Share(stock, sh.getQuantity(), sh.getPurchasePrice())
                );
            }
        }

        // Reconstructs the transaction archive
        TransactionArchive transactionArchive = new TransactionArchive();
        for (GameSave.TransactionSave ts : save.getTransactions()) {
            Stock stock = exchange.getStock(ts.getSymbol());
            if (stock != null) {
                Share share = new Share(stock, ts.getQuantity(), ts.getPrice());
                Transaction t = ts.getType().equals("purchase")
                    ? new Purchase(share, ts.getWeek())
                    : new Sale(share, ts.getWeek());
                transactionArchive.addCommitted(t);
            }
        }

        // Reconstructs the player
        Player player = new Player(
            save.getPlayerName(),
            save.getStartingBalance(),
            save.getBalance(),
            portfolio,
            transactionArchive
        );

        // Restores net worth history
        if (save.getNetWorthHistory() != null) {
            save.getNetWorthHistory().forEach(player::addNetWorthRecord);
        }

        return new GameLoadResult(player, exchange);
    }


    /**
     * Returns all saved games found in the saves/ directory.
     *
     * @return a list of .sav files
     */
    public static List<File> getSavedGames() {
        File dir = new File(SAVES_DIR);
        if (!dir.exists()) {
            return List.of();
        }

        File[] files = dir.listFiles(
            (d, name) -> name.endsWith(".sav")
        );
        if (files == null) {
            return List.of();
        }

        return List.of(files);
    }
}
