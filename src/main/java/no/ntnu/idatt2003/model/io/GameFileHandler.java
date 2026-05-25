package no.ntnu.idatt2003.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
public class GameFileHandler implements FileHandler<GameSave> {

    private static final Logger LOGGER =
        Logger.getLogger(GameFileHandler.class.getName());

    /** The directory where files are stored. */
    private static final String SAVES_DIR = "saves";

    /** The formatter for timestamp in save file names. */
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Reads a {@link GameSave} from the file at the given path.
     *
     * @param filePath path to the save file
     * @return the deserialized GameSave
     * @throws GameSaveException if the file could not be read or is corrupted
     */
    @Override
    public GameSave readFromFile(final String filePath) throws GameSaveException {
        try (FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (GameSave) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new GameSaveException("Save file not found: " + e.getMessage(), e);
        } catch (ClassNotFoundException | IOException e) {
            throw new GameSaveException("Could not read save file: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes the given {@link GameSave} to the file at the given path.
     *
     * @param data the GameSave to write
     * @param filePath path to the output file
     * @throws GameSaveException if the file could not be written
     */
    @Override
    public void writeToFile(final GameSave data, final String filePath) throws GameSaveException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(data);
        } catch (FileNotFoundException e) {
            throw new GameSaveException(
                "Could not find save directory: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new GameSaveException(
                "Could not write save file: " + e.getMessage(), e);
        }
    }

    /**
     * Saves the current game state to a file in the saves/ directory.
     *
     * @param saveName      the name given to the save file by the user
     * @param player        the player whose state is being saved
     * @param exchange      the current exchange
     * @throws GameSaveException if the file could not be written
     */
    public void saveGame(
        final String saveName,
        final Player player,
        final Exchange exchange
    ) throws GameSaveException {
        File dir = new File(SAVES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = LocalDateTime.now().format(FORMATTER);

        List<GameSave.StockSave> stocks = new ArrayList<>();
        for (Stock stock : exchange.getAllStocks()) {
            stocks.add(new GameSave.StockSave(
                stock.getSymbol(),
                stock.getCompany(),
                stock.getHistoricalPrices()
            ));
        }

        List<GameSave.ShareSave> portfolio = new ArrayList<>();
        for (Share share : player.getPortfolio().getShares()) {
            portfolio.add(new GameSave.ShareSave(
                share.getStock().getSymbol(),
                share.getQuantity(),
                share.getPurchasePrice()
            ));
        }

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

        GameSave save = new GameSave(
            saveName,
            timestamp,
            player.getName(),
            player.getMoney(),
            player.getStartingMoney(),
            exchange.getWeek(),
            player.getNetWorthHistory(),
            player.getWatchlistSymbols(),
            stocks,
            portfolio,
            transactions
        );

        String filename = saveName + " (" + timestamp + ").sav";
        filename = filename.replaceAll("[/:*?\"<>|]", "_");
        File file = new File(dir, filename);

        writeToFile(save, file.getAbsolutePath());
    }

    /**
     * Reads only the metadata from a save file without reconstructing the game.
     * Used to display save information in the UI before loading.
     *
     * @param file the save file to read
     * @return the GameSave object containing save metadata
     * @throws GameSaveException if the file could not be read or is corrupted
     */
    public GameSave readSave(final File file) throws GameSaveException {
        return readFromFile(file.getAbsolutePath());
    }

    /**
     * Loads a saved game state from file.
     *
     * @param file the file to load the game state from
     * @return a GameLoadResult containing the reconstructed player and exchange
     * @throws GameSaveException if the file could not be read or is corrupted
     */
    public GameLoadResult loadGame(final File file) throws GameSaveException {
        GameSave save = readFromFile(file.getAbsolutePath());

        List<Stock> stocks = new ArrayList<>();
        for (GameSave.StockSave s : save.getStocks()) {
            stocks.add(new Stock(s.getSymbol(), s.getCompany(), s.getPrices()));
        }

        Exchange exchange = new Exchange("Millions Exchange", stocks);
        exchange.setWeek(save.getWeek());

        Portfolio portfolio = new Portfolio();
        for (GameSave.ShareSave sh : save.getPortfolio()) {
            Stock stock = exchange.getStock(sh.getSymbol());
            if (stock != null) {
                portfolio.addShare(
                    new Share(stock, sh.getQuantity(), sh.getPurchasePrice())
                );
            }
        }

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

        Player player = new Player(
            save.getPlayerName(),
            save.getStartingBalance(),
            save.getBalance(),
            portfolio,
            transactionArchive
        );

        if (save.getNetWorthHistory() != null) {
            save.getNetWorthHistory().forEach(player::addNetWorthRecord);
        }

        if (save.getWatchlistSymbols() != null) {
            save.getWatchlistSymbols().stream()
                .filter(exchange::hasStock)
                .forEach(player::addToWatchlist);
        }

        return new GameLoadResult(player, exchange);
    }

    /**
     * Returns all saved games found in the saves/ directory.
     *
     * @return a list of .sav files
     */
    public static List<File> getSavedGames() {
        Path dir = Paths.get(SAVES_DIR);
        if (!Files.exists(dir)) {
            return List.of();
        }

        try {
            return Files.list(dir)
                .filter(p -> p.toString().endsWith(".sav"))
                .map(Path::toFile)
                .toList();
        } catch (IOException e) {
            LOGGER.warning("Could not list save files: " + e.getMessage());
            return List.of();
        }
    }
}
