package no.ntnu.idatt2003.model.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Purchase;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;

class GameFileHandlerTest {

    private Player player;
    private Exchange exchange;
    private Stock stock;
    private Set<File> filesBeforeTest;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple", List.of(new BigDecimal("150.00")));
        exchange = new Exchange("Test Exchange", List.of(stock));
        player = new Player("TestPlayer", new BigDecimal("10000"));
        filesBeforeTest = new HashSet<>(GameFileHandler.getSavedGames());
    }

    @AfterEach
    void tearDown() {
        for (File file : GameFileHandler.getSavedGames()) {
            if (!filesBeforeTest.contains(file)) {
                file.delete();
            }
        }
    }

    private File saveAndGetFile(String name) throws GameSaveException {
        GameFileHandler.saveGame(name, player, exchange);
        return GameFileHandler.getSavedGames().stream()
            .filter(f -> !filesBeforeTest.contains(f))
            .findFirst()
            .orElseThrow();
    }

    @Test
    void testSaveAndLoad_restoresPlayerBalance() throws GameSaveException {
        player.withdrawMoney(new BigDecimal("2000"));
        File saved = saveAndGetFile("test");

        GameLoadResult result = GameFileHandler.loadGame(saved);

        assertEquals(new BigDecimal("8000"), result.getPlayer().getMoney());
    }

    @Test
    void testSaveAndLoad_restoresPortfolio() throws GameSaveException {
        Share share = new Share(stock, new BigDecimal("5"), new BigDecimal("150.00"));
        player.getPortfolio().addShare(share);
        File saved = saveAndGetFile("test");

        GameLoadResult result = GameFileHandler.loadGame(saved);

        assertEquals(1, result.getPlayer().getPortfolio().getShares().size());
    }

    @Test
    void testSaveAndLoad_restoresTransactionArchive() throws GameSaveException {
        Share share = new Share(stock, new BigDecimal("5"), new BigDecimal("150.00"));
        Purchase purchase = new Purchase(share, 1);
        purchase.commit(player);
        File saved = saveAndGetFile("test");

        GameLoadResult result = GameFileHandler.loadGame(saved);

        assertEquals(1, result.getPlayer().getTransactionArchive().getAll().size());
    }

    @Test
    void testSaveAndLoad_restoresWeek() throws GameSaveException {
        exchange.setWeek(5);
        File saved = saveAndGetFile("test");

        GameLoadResult result = GameFileHandler.loadGame(saved);

        assertEquals(5, result.getExchange().getWeek());
    }

    @Test
    void testSaveAndLoad_restoresNetWorthHistory() throws GameSaveException {
        player.recordNetWorth();
        File saved = saveAndGetFile("test");

        GameLoadResult result = GameFileHandler.loadGame(saved);

        assertEquals(1, result.getPlayer().getNetWorthHistory().size());
    }

    @Test
    void testLoadGame_invalidFile_throwsException() {
        File invalidFile = new File("nonexistent.sav");
        assertThrows(GameSaveException.class, () -> GameFileHandler.loadGame(invalidFile));
    }

    @Test
    void testReadSave_returnsCorrectMetadata() throws GameSaveException {
        File saved = saveAndGetFile("metaTest");

        GameSave save = GameFileHandler.readSave(saved);

        assertEquals("metaTest", save.getSaveName());
        assertEquals("TestPlayer", save.getPlayerName());
        assertEquals(new BigDecimal("10000"), save.getBalance());
        assertEquals(1, save.getWeek());
    }

    @Test
    void testReadSave_invalidFile_throwsException() {
        File invalidFile = new File("nonexistent.sav");
        assertThrows(GameSaveException.class, () -> GameFileHandler.readSave(invalidFile));
    }
}
