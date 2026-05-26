package no.ntnu.idatt2003.model.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.model.entity.Stock;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockFileHandlerTest {

    private StockFileHandler handler;

    @BeforeEach
    void setUp() {
        handler = new StockFileHandler();
    }

    @Test
    void testParseStocks_validData() throws IOException {
        String content = """
                # Top 500 US Stocks by Market Cap
                NVDA,Nvidia,191.27
                AAPL,Apple Inc.,276.43
                MSFT,Microsoft,404.68
                """;

        List<Stock> stocks = handler.parseStocks(new StringReader(content));

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
    void testParseStocks_ignoresCommentsAndBlanks() throws IOException {
        String content = """
                # This is a comment

                GOOGL,Google,120.50

                # Another comment
                """;

        List<Stock> stocks = handler.parseStocks(new StringReader(content));

        assertEquals(1, stocks.size());
        assertEquals("GOOGL", stocks.get(0).getSymbol());
        assertEquals(new BigDecimal("120.50"), stocks.get(0).getSalesPrice());
    }

    @Test
    void testParseStocks_skipsInvalidLines() throws IOException {
        String content = """
                INVALID_LINE
                TSLA,Tesla,empty_number
                AMZN,Amazon,340.75
                """;

        List<Stock> stocks = handler.parseStocks(new StringReader(content));

        assertEquals(1, stocks.size());
        assertEquals("AMZN", stocks.get(0).getSymbol());
        assertEquals(new BigDecimal("340.75"), stocks.get(0).getSalesPrice());
    }

    @Test
    void testParseStocks_emptyInput_returnsEmptyList() throws IOException {
        List<Stock> stocks = handler.parseStocks(new StringReader(""));
        assertTrue(stocks.isEmpty());
    }

    @Test
    void testParseStocks_onlyCommentsAndBlanks_returnsEmptyList() throws IOException {
        String content = """
                # just a comment

                # another comment
                """;
        List<Stock> stocks = handler.parseStocks(new StringReader(content));
        assertTrue(stocks.isEmpty());
    }

    @Test
    void testWriteToFile_createsCorrectFile() throws IOException {
        Stock stock1 = new Stock("NFLX", "Netflix", List.of(new BigDecimal("450.50")));
        Stock stock2 = new Stock("AMZN", "Amazon", List.of(new BigDecimal("340.75")));
        Path tempFile = Files.createTempFile("test_write", ".csv");

        try {
            handler.writeToFile(List.of(stock1, stock2), tempFile.toString());

            List<String> lines = Files.readAllLines(tempFile);
            assertTrue(lines.get(0).startsWith("#"));
            assertTrue(lines.stream().anyMatch(l -> l.equals("NFLX,Netflix,450.50")));
            assertTrue(lines.stream().anyMatch(l -> l.equals("AMZN,Amazon,340.75")));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
