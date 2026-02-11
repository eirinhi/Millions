import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class Stock {
  private String symbol;
  private String company;
  private List<BigDecimal> prices;

  public Stock(String symbol, String company, List<BigDecimal> salesPrice) {
    this.symbol = symbol;
    this.company = company;
    this.prices = new ArrayList<>();
    this.prices.addAll(salesPrice);
  }
  public String getSymbol() {
    return symbol;
  }
  public String getCompany() {
    return company;
  }
  public List<BigDecimal> getPrices() {
    return prices;
  }
  public void addNewSalesPrice(BigDecimal price) {
    this.prices.add(price);
  }

  public BigDecimal getSalesPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("No prices have been added to the stock");
    }
    return prices.get(prices.size() - 1);
  }
}
