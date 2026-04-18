package no.ntnu.idatt2003.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.logic.Exchange;

/**
 * A simple command-line client for running the stock trading game.
 */
public class CommandLineClient {

    private static final String UNDERLINE = "---------------------------------";
    public static void main(final String[] args) {
        Stock s1 = new Stock("AAPL", "Apple Inc.", List.of(new BigDecimal("270.00")));
        Stock s2 = new Stock("GOOGL", "Alphabet Inc.", List.of(new BigDecimal("313.00")));
        Stock s3 = new Stock("NVDA", "NVIDIA Corp.", List.of(new BigDecimal("195.00")));
        List<Stock> stocks = List.of(s1, s2, s3);
        Exchange exchange = new Exchange("NASDAQ", stocks);

        Player player = new Player("Player", new BigDecimal("10000"));

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n===== Welcome to Stock Game =====");

        while (running) {
            System.out.println("\nWeek: " + exchange.getWeek());
            System.out.println("---------");
            System.out.println("Your money: " + player.getMoney());
            System.out.println(UNDERLINE);
            if (player.getPortfolio().getShares().isEmpty()) {
                System.out.println("Portfolio is empty");
            } else {
                System.out.println("Your shares: ");
                for (Share share : player.getPortfolio().getShares()) {
                    System.out.println(share.toString());
                }
            }
            System.out.println(UNDERLINE);
            System.out.println("Listed shares: ");
            for (Stock stock : stocks) {
                System.out.println(stock.toString());
            }

            System.out.println("\nChoose action: [buy] [sell] [next] [quit]");
            String action = scanner.nextLine().toLowerCase();

            switch (action) {
                case "buy":
                    System.out.println("\nAvailable Stocks: ");
                    System.out.println(UNDERLINE);
                    for (Stock stock : stocks) {
                        System.out.println(stock.toString());
                    }
                    System.out.println("     Enter stock symbol: ");
                    String buySymbol = scanner.nextLine().trim().toUpperCase();
                    System.out.println("     Enter quantity: ");
                    BigDecimal buyQt = new BigDecimal(scanner.nextLine().trim());

                    try {
                        exchange.buy(buySymbol, buyQt, player);
                        System.out.println("Purchased successful!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;


                case "sell":
                    if (player.getPortfolio().getShares().isEmpty()) {
                        System.out.println("You have no shares to sell");
                        break;
                    }

                    System.out.println("\nYour shares: ");
                    System.out.println(UNDERLINE);
                    for (int i = 0; i < player.getPortfolio().getShares().size(); i++) {
                        Share share = player.getPortfolio().getShares().get(i);
                        System.out.println("[" + i + "]  " + share.toString());
                    }
                    System.out.println("     Enter the number of the share you want to sell: ");
                    int index = scanner.nextInt();
                    scanner.nextLine();

                    if (index < 0 || index >= player.getPortfolio().getShares().size()) {
                        System.out.println("Unvalid index.");
                        break;
                    }

                    Share shareToSell = player.getPortfolio().getShares().get(index);

                    try {
                        exchange.sell(shareToSell, player);
                        System.out.println("Sold successful!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;


                case "next":
                    exchange.advance();
                    System.out.println("Week updated!");
                    break;


                case "quit":
                    running = false;

                    BigDecimal totalStockValue = BigDecimal.ZERO;

                    for (Share share : player.getPortfolio().getShares()) {
                        BigDecimal currentPrice = share.getStock().getSalesPrice();
                        BigDecimal value = currentPrice.multiply(share.getQuantity());
                        totalStockValue = totalStockValue.add(value);
                    }

                    BigDecimal endingMoney = player.getMoney();
                    BigDecimal totalWealth = endingMoney.add(totalStockValue);

                    System.out.println("\n===== Game Over =====");
                    System.out.println("Your money: " + endingMoney);
                    System.out.println("Stock value: " + totalStockValue);
                    System.out.println("Total wealth: " + totalWealth);

                    BigDecimal result = totalWealth.subtract(player.getStartingMoney());

                    if (result.compareTo(BigDecimal.ZERO) > 0) {
                        System.out.println("You earned: " + result);
                    } else if (result.compareTo(BigDecimal.ZERO) < 0) {
                        System.out.println("You lost: " + result.abs());
                    } else {
                        System.out.println("No gain, no loss.");
                    }

                    System.out.println("----------------------------");
                    break;
            }
        }
        scanner.close();
    }
}
