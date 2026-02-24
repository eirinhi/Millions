package no.ntnu.idatt2003.core;
import java.math.BigDecimal;
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
   * Constructs Stock object with symbol, companyname, and list of sales prices.
   *
   * @param symbol the unique stock symbol
   * @param company the name of the company
   * @param salesPrice a list of historical sales prices
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
  public List<BigDecimal> getPrices() {
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
   * Returns a string representation of the stock.
   *
   * @return a string representation
   */
  @Override
  public String toString() {
    return "Stock [symbol=" + symbol
        + ", company=" + company
        + ", prices=" + prices + "]";
  }
}
