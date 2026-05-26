package no.ntnu.idatt2003.model.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import no.ntnu.idatt2003.model.entity.Stock;

/**
 * Reads and writes stock data to CSV-like files.
 *
 * <p>Input format (per non-empty, non-comment line):
 * symbol,company,price
 *
 * <p>Lines that are empty or start with '#' are ignored. Malformed lines or invalid prices are
 * skipped with an error message to stderr.
 */
public class StockFileHandler implements FileHandler<List<Stock>> {

    private static final Logger LOGGER =
        Logger.getLogger(StockFileHandler.class.getName());

    /**
     * Reads stocks from a file.
     *
     * @param filePath path to the input file
     * @return list of Stock objects; each Stock will be created with a single-entry price history
     * @throws IllegalArgumentException if filePath is null or blank
     * @throws IOException if an IO error occurs while reading the file
     */
    @Override
    public List<Stock> readFromFile(final String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or blank");
        }
        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath, StandardCharsets.UTF_8))) {
            return parseStocks(reader);
        }
    }

    /**
     * Parses stocks from the given reader.
     *
     * @param reader the reader to parse from
     * @return list of parsed Stock objects
     * @throws IllegalArgumentException if reader is null
     * @throws IOException if an IO error occurs while reading
     */
    List<Stock> parseStocks(final Reader reader) throws IOException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }
        List<Stock> stocks = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 3) {
                    LOGGER.log(java.util.logging.Level.WARNING, "Invalid line format, skipping line: {0}", line);
                    continue;
                }

                BigDecimal price;
                try {
                    price = new BigDecimal(parts[2].trim());
                } catch (NumberFormatException e) {
                    LOGGER.log(java.util.logging.Level.WARNING, "Invalid price format, skipping line: {0}", line);
                    continue;
                }

                String symbol = parts[0].trim();
                String name = parts[1].trim();
                stocks.add(new Stock(symbol, name, List.of(price)));
            }
        }

        return stocks;
    }

    /**
     * Writes a list of stocks to a file in CSV format: symbol,company,latestPrice.
     *
     * @param data     list of stocks to write
     * @param filePath path to the output file
     * @throws IllegalArgumentException if data or filePath is null, or filePath is blank
     * @throws IOException if an IO error occurs while writing the file
     */
    @Override
    public void writeToFile(final List<Stock> data, final String filePath) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("Stock list cannot be null");
        }
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or blank");
        }
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(filePath, StandardCharsets.UTF_8))) {
            bw.write("# Exported stock data");
            bw.newLine();

            for (Stock stock : data) {
                String priceStr = "";
                try {
                    Object p = stock.getSalesPrice();
                    if (p != null) {
                        priceStr = p.toString();
                    }
                } catch (Throwable ignored) {
                    // Leave price empty if unavailable rather than failing the whole write.
                }

                bw.write(stock.getSymbol() + "," + stock.getCompany() + "," + priceStr);
                bw.newLine();
            }
        }
    }
}
