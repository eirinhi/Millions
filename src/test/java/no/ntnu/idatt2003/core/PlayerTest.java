package no.ntnu.idatt2003.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

  private Player player;

  @BeforeEach
  public void setUp() {
    player = new Player("Name", BigDecimal.valueOf(1000));
  }

  @Test
  public void testGetName() {
    assertEquals("Name", player.getName());
  }
  @Test
  public void testGetStartingMoney() {
    assertEquals(BigDecimal.valueOf(1000), player.getStartingMoney());
  }

  @Test
  public void testGetMoney() {
    assertEquals(BigDecimal.valueOf(1000), player.getMoney());
  }

  @Test
  public void testGetPortfolio() {
    assertNotNull(player.getPortfolio());
  }

  @Test
  public void testGetTransactionArchive() {
    assertNotNull(player.getTransactionArchive());
  }

  @Test
  public void testAddMoney() {
    player.addMoney(BigDecimal.valueOf(700));
    assertEquals(BigDecimal.valueOf(1700), player.getMoney());
  }

  @Test
  public void testWithdrawMoney() {
    player.withdrawMoney(BigDecimal.valueOf(400));
    assertEquals(BigDecimal.valueOf(500), player.getMoney());
  }

  @Test
  public void testWithdrawTooMuchMoney() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(BigDecimal.valueOf(1200)));
    assertEquals("Not enough money in the account", e.getMessage());
  }

  @Test
  public void testNullName() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        new Player(null, BigDecimal.valueOf(1000)));
    assertEquals("Name cannot be null", e.getMessage());
  }

  @Test
  public void testEmptyName() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        new Player("", BigDecimal.valueOf(1000)));
    assertEquals("Name cannot be empty", e.getMessage());
  }

  @Test
  public void testNullStartingMoney() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        new Player("Name", null));
    assertEquals("Starting money cannot be null or negative", e.getMessage());
  }

  @Test
  public void testNegativeStartingMoney() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        new Player("Name", BigDecimal.valueOf(-200)));
    assertEquals("Starting money cannot be null or negative", e.getMessage());
  }

  @Test
  public void testAddMoneyNull() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(null));
    assertEquals("Amount cannot be null", e.getMessage());
  }

  @Test
  public void testAddMoneyNegative() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(BigDecimal.valueOf(-200)));
    assertEquals("Amount cannot be zero or negative", e.getMessage());
  }

  @Test
  public void testWithdrawMoneyNull() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(null));
    assertEquals("Amount cannot be null", e.getMessage());
  }

  @Test
  public void testWithdrawMoneyNegative() {
    Exception e = assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(BigDecimal.valueOf(-200)));
    assertEquals("Amount cannot be zero or negative", e.getMessage());
  }
}
