# Millions - Stock Trading Game

Millions is a game where players can buy and sell stocks, manage their portfolio, and try to grow their capital and level up to gain a better status, all without the risk of real money.

---

## Views Overview

|**View**            |**Description**|
|---                 |---|
|**StartView**       |The initial screen where players can select a stock data file, enter their name and a starting capital, or load a previously saved game to start playing. |
|**MainView**        |The static top- and side-bar of the game which stays the same while the center view changes between `PortfolioView`, `ExchangeView`, and `TradeView`. |
|**PortfolioView**   |The view showing the player's portfolio with all holdings and all transactions, as well as performance metrics and a chart visualizing the player's money history.|
|**ExchangeView**    |The view for browsing stocks, with functionality for sorting, filtering, and searching.|
|**TradeView**       |The view entered from `ExchangeView`, for buying and selling stocks, as well as a visualization of the stock's historical prices.|
|**GameSummaryView** |The ending view showing the player's achieved status, starting- and ending capital, and total gains/losses.|


![Start view](docs/screenshots/start-view.png)
![Portfolio view](docs/screenshots/portfolio-view.png)
![Exchange view](docs/screenshots/exchange-view.png)
![Trade view (Buy)](docs/screenshots/trade-view-buy.png)
![Trade view (Sell)](docs/screenshots/trade-view-sell.png)
![Receipt](docs/screenshots/receipt.png)
![Game summary view](docs/screenshots/game-summary-view.png)


## Build and Run

```bash
mvn clean package
mvn javafx:run
```

## Running Tests

```bash
mvn test
```
