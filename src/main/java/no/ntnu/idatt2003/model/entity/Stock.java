package no.ntnu.idatt2003.model.entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a stock in a company listed on an exchange.
 * <p>
 *   A stock has a unique symbol (e.g. "AAPL" for Apple Inc.),
 *   a company name, and a list of historical sales prices.
 *   The sales price is updated weekly.
 * </p>
 * The current sales price of the stock is defined as the most
 * recently added price in the price list.
 */
public class Stock {

  /**The unique symbol of the stock (e.g. "AAPL" for Apple Inc.). **/
  private final String symbol;

  /**The name of the company. **/
  private final String company;

  /**A list of historical sales prices. **/
  private final List<BigDecimal> prices;

  /**
   * Constructs Stock object with symbol, company name, and list of sales prices.
   *
   * @param symbol      the unique stock symbol
   * @param company     the name of the company
   * @param salesPrice  a list of historical sales prices
   * @throws IllegalArgumentException if any argument is null
   */
  public Stock(
    final String symbol,
    final String company,
    final List<BigDecimal> salesPrice) {

    if (symbol == null) {
      throw new IllegalArgumentException("Symbol cannot be null.");
    }

    if (company == null) {
      throw new IllegalArgumentException("Company cannot be null.");
    }

    if (salesPrice == null) {
      throw new IllegalArgumentException("SalesPrice cannot be null.");
    }

    this.symbol = symbol;
    this.company = company;
    this.prices = new ArrayList<>();
    this.prices.addAll(salesPrice);
  }

  /**
   * Returns the stock symbol.
   *
   * @return the unique stock symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Returns the company name.
   *
   * @return the company name
   */
  public String getCompany() {
    return company;
  }

  /**
   * Returns the list of historical sales prices.
   *
   * @return a list of sales prices
   */
  public List<BigDecimal> getHistoricalPrices() {
    return new ArrayList<>(prices);
  }

  /**
   * Adds a new weekly sales price to the stock.
   *
   * @param price the new sales price to add
   * @throws IllegalArgumentException if price is null
   */
  public void addNewSalesPrice(final BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null.");
    }
    this.prices.add(price);
  }

  /**
   * Returns the current sales price of the stock.
   * <p>
   *   The current sales price is defined as the most recently added
   *   price in the price list.
   * </p>
   *
   * @return the latest sales price
   * @throws IllegalStateException if no prices have been added
   */
  public BigDecimal getSalesPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("No prices have been added to the stock");
    }
    return prices.get(prices.size() - 1);
  }

  /**
   * Returns the highest recorded sales price for this stock.
   *
   * @return the highest recorded sales price
   * @throws IllegalStateException if no price have been added
   */
  public BigDecimal getHighestPrice() {
    return prices.stream()
        .max(BigDecimal::compareTo)
        .orElseThrow(() -> new IllegalStateException("No prices have been added to the stock"));
  }

  /**
   * Returns the lowest recorded sales price for this stock.
   *
   * @return the lowest recorded sales price
   * @throws IllegalStateException if no prices have been added
   */
  public BigDecimal getLowestPrice() {
    return prices.stream()
        .min(BigDecimal::compareTo)
        .orElseThrow(() -> new IllegalStateException("No prices have been added to the stock"));
  }

  /**
   * Returns the difference between the last two recorded sales prices.
   *
   * @return the difference between the most recent price and te previous price
   * @throws IllegalStateException if no prices have been added
   */
  public BigDecimal getLatestPriceChange() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("No prices have been added to the stock");
    }
    if (prices.size() == 1) {
      return BigDecimal.ZERO;
    }

    BigDecimal latest   = prices.get(prices.size() - 1);
    BigDecimal previous = prices.get(prices.size() - 2);

    return latest.subtract(previous);
  }

  /**
   * Returns the weekly return percentage.
   *
   * @return the weekly return percentage
   */
  public BigDecimal getWeeklyReturnPercentage() {
    BigDecimal change = getLatestPriceChange();
    if (change.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    BigDecimal previous = getSalesPrice().subtract(change);
    if (previous.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return change.divide(previous, 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100))
        .setScale(2, RoundingMode.HALF_UP);
  }


  /**
   * Returns a string representation of the stock with key information.
   *
   * @return a formatted summary of the stock's key information
   */
  @Override
  public String toString() {
    return "Stock information\n" +
        "Symbol: " + symbol +
        "\nCompany: " + company +
        "\nCurrent price: " + getSalesPrice() +
        "\nHighest price: " + getHighestPrice() +
        "\nLowest price: " + getLowestPrice() +
        "\nLatest price change: " + getLatestPriceChange() +
        "\nRecorded prices: " + getHistoricalPrices().size();
  }
}
