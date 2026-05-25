package no.ntnu.idatt2003.model.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Stock;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockFileHandlerTest {
    private Path tempFile;
    private StockFileHandler handler;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test_stocks", ".csv");
        handler = new StockFileHandler();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testReadStocksFromFile_validData() throws IOException {
        String content = """
                # Top 500 US Stocks by Market Cap
                NVDA,Nvidia,191.27
                AAPL,Apple Inc.,276.43
                MSFT,Microsoft,404.68
                """;
        Files.writeString(tempFile, content);

        List<Stock> stocks = handler.readFromFile(tempFile.toString());

        assertEquals(3, stocks.size());

        assertEquals("NVDA", stocks.get(0).getSymbol());
        assertEquals("Nvidia", stocks.get(0).getCompany());
        assertEquals(new BigDecimal("191.27"), stocks.get(0).getSalesPrice());

        assertEquals("AAPL", stocks.get(1).getSymbol());
        assertEquals("Apple Inc.", stocks.get(1).getCompany());
        assertEquals(new BigDecimal("276.43"), stocks.get(1).getSalesPrice());

        assertEquals("MSFT", stocks.get(2).getSymbol());
        assertEquals("Microsoft", stocks.get(2).getCompany());
        assertEquals(new BigDecimal("404.68"), stocks.get(2).getSalesPrice());
    }

    @Test
    void testReadStocksFromFile_ignoresCommentsAndBlanks() throws IOException {
        String content = """
                # This is a comment

                GOOGL,Google,120.50

                # Another comment
                """;
        Files.writeString(tempFile, content);

        List<Stock> stocks = handler.readFromFile(tempFile.toString());

        assertEquals(1, stocks.size());
        assertEquals("GOOGL", stocks.get(0).getSymbol());
        assertEquals(new BigDecimal("120.50"), stocks.get(0).getSalesPrice());
    }

    @Test
    void testReadStocksFromFile_skipsInvalidLines() throws IOException {
        String content = """
                INVALID_LINE
                TSLA,Tesla,empty_number
                AMZN,Amazon,340.75
                """;
        Files.writeString(tempFile, content);

        List<Stock> stocks = handler.readFromFile(tempFile.toString());

        assertEquals(1, stocks.size());
        assertEquals("AMZN", stocks.get(0).getSymbol());
        assertEquals(new BigDecimal("340.75"), stocks.get(0).getSalesPrice());
    }

    @Test
    void testWriteStocksToFile_createsCorrectFile() throws IOException {
        Stock stock1 = new Stock("NFLX", "Netflix", List.of(new BigDecimal("450.50")));
        Stock stock2 = new Stock("AMZN", "Amazon", List.of(new BigDecimal("340.75")));

        List<Stock> stocks = List.of(stock1, stock2);

        handler.writeToFile(stocks, tempFile.toString());

        List<String> lines = Files.readAllLines(tempFile);

        assertTrue(lines.get(0).startsWith("#"));

        assertTrue(lines.stream().anyMatch(l -> l.equals("NFLX,Netflix,450.50")));
        assertTrue(lines.stream().anyMatch(l -> l.equals("AMZN,Amazon,340.75")));
    }

    @Test
    void testReadThenWrite_preservesStartPrice() throws IOException {
        String content = """
                AAPL,Apple Inc.,276.43
                MSFT,Microsoft,404.68
                """;
        Files.writeString(tempFile, content);

        List<Stock> stocks = handler.readFromFile(tempFile.toString());

        Path outputFile = Files.createTempFile("output_stocks", ".csv");
        handler.writeToFile(stocks, outputFile.toString());

        List<Stock> readBack = handler.readFromFile(outputFile.toString());

        assertEquals(stocks.size(), readBack.size());
        assertEquals(stocks.get(0).getSymbol(), readBack.get(0).getSymbol());
        assertEquals(stocks.get(0).getSalesPrice(), readBack.get(0).getSalesPrice());

        Files.deleteIfExists(outputFile);
    }

    @Test
    void testReadEmptyFile_returnsEmptyList() throws IOException {
        Files.writeString(tempFile, "");
        List<Stock> stocks = handler.readFromFile(tempFile.toString());
        assertTrue(stocks.isEmpty());
    }
}
