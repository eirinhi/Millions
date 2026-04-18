package no.ntnu.idatt2003.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

  private Player player;

  @BeforeEach
  void setUp() {
    player = new Player("Name", BigDecimal.valueOf(1000));
  }

  @Test
  void testGetName() {
    assertEquals("Name", player.getName());
  }
  @Test
  void testGetStartingMoney() {
    assertEquals(BigDecimal.valueOf(1000), player.getStartingMoney());
  }

  @Test
  void testGetMoney() {
    assertEquals(BigDecimal.valueOf(1000), player.getMoney());
  }

  @Test
  void testGetPortfolio() {
    assertNotNull(player.getPortfolio());
  }

  @Test
  void testGetTransactionArchive() {
    assertNotNull(player.getTransactionArchive());
  }

  @Test
  void testAddMoney() {
    player.addMoney(BigDecimal.valueOf(700));
    assertEquals(BigDecimal.valueOf(1700), player.getMoney());
  }

  @Test
  void testWithdrawMoney() {
    player.withdrawMoney(BigDecimal.valueOf(400));
    assertEquals(BigDecimal.valueOf(600), player.getMoney());
  }

  @Test
  void testWithdrawTooMuchMoney() {
    BigDecimal toMuch = new BigDecimal(1200);
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(toMuch));
    assertEquals("Not enough money in the account", e.getMessage());
  }

  @Test
  void testNullName() {
    BigDecimal startingMoney = BigDecimal.valueOf(1000);

    IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> new Player(null, startingMoney));

    assertEquals("Name cannot be null or empty", exception.getMessage());
  }

@Test
void testEmptyName() {
    String invalidName = "";
    BigDecimal startingMoney = BigDecimal.valueOf(1000);

    IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> new Player(invalidName, startingMoney));

    assertEquals("Name cannot be null or empty", exception.getMessage());
}


  @Test
  void testNullStartingMoney() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        new Player("Name", null));
    assertEquals("Starting money cannot be null or negative", e.getMessage());
  }

  @Test
  void testNegativeStartingMoney() {
    String name = "Name";
    BigDecimal negativeMoney = BigDecimal.valueOf(-200);

    IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> new Player(name, negativeMoney));

    assertEquals("Starting money cannot be null or negative", exception.getMessage());
  }

  @Test
  void testAddMoneyNull() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(null));
    assertEquals("Amount cannot be null or negative", e.getMessage());
  }

  @Test
  void testAddMoneyNegative() {
    BigDecimal negativeMoney = BigDecimal.valueOf(-200);

    IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> player.addMoney(negativeMoney));

    assertEquals("Amount cannot be zero or negative", exception.getMessage());
  }

  @Test
  void testWithdrawMoneyNull() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(null));
    assertEquals("Amount cannot be null", e.getMessage());
  }

  @Test
  void testWithdrawMoneyNegative() {
    BigDecimal negativeMoney = BigDecimal.valueOf(-200);

    IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> player.withdrawMoney(negativeMoney));

    assertEquals("Amount cannot be zero or negative", exception.getMessage());
  }

  @Test
  void testGetNetWorth() {
    List<BigDecimal> prices = new ArrayList<>();
    prices.add(new BigDecimal("100"));
    Stock stock = new Stock("SYMBOL", "Company", prices);
    Share share = new Share(stock, new BigDecimal("5"), new BigDecimal("90"));

    player.getPortfolio().addShare(share);
    
    BigDecimal expected = player.getMoney().add(player.getPortfolio().getNetWorth());
  
    assertEquals(expected, player.getNetWorth());
  }


  @Test
  void testGetStatusSpeculator() {
    assertEquals("Novice", player.getStatus());

    player.addMoney(new BigDecimal("200"));
    assertEquals("Novice", player.getStatus());

    player.withdrawMoney(new BigDecimal("200"));

    List<BigDecimal> prices = new ArrayList<>();
    prices.add(new BigDecimal("5"));
    Stock stock = new Stock("SYMBOL", "Company", prices);
    Share share = new Share(stock, new BigDecimal("1"), new BigDecimal("1"));
    for (int i = 1; i < 11; i++) {
      Purchase purchase = new Purchase(share, i);
      purchase.committed = true;
      player.getTransactionArchive().add(purchase);
    }

    assertEquals("Novice", player.getStatus());

    player.addMoney(new BigDecimal("1050"));
    assertEquals("Investor", player.getStatus());

    for (int i = 10; i < 21; i++) {
      Purchase purchase = new Purchase(share, i);
      purchase.committed = true;
      player.getTransactionArchive().add(purchase);
    }
    assertEquals("Speculator", player.getStatus());

    player.withdrawMoney(new BigDecimal("500"));
    assertEquals("Investor", player.getStatus());

  }
}
