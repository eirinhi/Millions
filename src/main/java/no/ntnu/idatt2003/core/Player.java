package no.ntnu.idatt2003.core;

import java.math.BigDecimal;

/**
 * Represents a player in the stock game.
 * <p>
 * A player has:
 *<ul>
 *   <li>a name</li>
 *   <li>a starting capital</li>
 *   <li>a current money balance</li>
 *   <li>a portfolio of owned shares</li>
 *   <li>a transaction archive</li>
 *</ul>
 *
 * The player's current balance is tracked by {@code money},
 * while {@code startingMoney} stores the original capital for
 * reference in game status calculations.
 */
public class Player {
  /**The name of the player. */
  private String name;

  /**The starting capital of the player. */
  private BigDecimal startingMoney;

  /**The current balance of the player. */
  private BigDecimal money;

  /**The portfolio containing owned shares of the player. */
  private Portfolio portfolio;

  /**The archive storing all committed transactions for this player. */
  private TransactionArchive transactionArchive;

  /**
   * Creates a new player with the specified name and starting money.
   * The current money is initialized to the starting amount and a new
   * empty portfolio and transaction archive are created.
   *
   * @param name          the players name
   * @param startingMoney the start capital
   * @throws IllegalArgumentException if {@code name} or
   *        {@code staringMoney} is null or <= 0.
   */
  public Player(final String name, final BigDecimal startingMoney) {

    if (startingMoney == null
        || startingMoney.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(
        "Starting money cannot be null or negative");
    }

    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }

    this.name = name;
    this.startingMoney = startingMoney;
    this.money = startingMoney;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();
  }

  /**
   * Returns the name of the player.
   *
   * @return the name of the player
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current money balance of the player.
   *
   * @return the current money balance of the player
   */
  public BigDecimal getMoney() {
    return money;
  }

  /**
   * Returns the starting capital of the player.
   *
   * @return the starting money of the player
   */
  public BigDecimal getStartingMoney() {
    return startingMoney;
  }

  /**
   * Returns the portfolio of the player.
   *
   * @return the portfolio object containing the player's shares
   */
  public Portfolio getPortfolio() {
    return portfolio;
  }

  /**
   * Returns the transaction archive of the player.
   *
   * @return the transaction archive storing committed transactions
   */
  public TransactionArchive getTransactionArchive() {
    return transactionArchive;
  }

  /**
   * Adds the specified amount of money to the player's balance.
   *
   * @param amount the amount to add
   * @throws IllegalArgumentException if {@code amount} is null or <= 0
   */
  public void addMoney(final BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null or negative");
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount cannot be zero or negative");
    }
    money = money.add(amount);
  }

  /**
   * Withdraws the specified amount of money from the player's balance.
   *
   * @param amount the amount to withdraw
   * @throws IllegalArgumentException if {@code amount} is null, <= 0,
   *        or exceeds current money
   */
  public void withdrawMoney(final BigDecimal amount) {

    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount cannot be zero or negative");
    }

    if (money.compareTo(amount) < 0) {
      throw new IllegalArgumentException("Not enough money in the account");
    }

    money = money.subtract(amount);
  }
}
