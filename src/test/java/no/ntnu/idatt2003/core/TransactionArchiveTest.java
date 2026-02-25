package no.ntnu.idatt2003.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionArchiveTest {
    TransactionArchive archive;
    Purchase purchase;
    Sale sale;
    List<BigDecimal> price;
    BigDecimal quantity;
    BigDecimal purchasePrice;
    int week;
    String name;
    BigDecimal startingMoney;
    Player player;

    Stock stock;
    Share share;

        @BeforeEach
        void setUp() {
            price = List.of(new BigDecimal("100.00"));
            quantity = new BigDecimal("10");
            purchasePrice = new BigDecimal("100.00");
            stock = new Stock("SYMBOL", "Company", price);
            share = new Share(stock, quantity, purchasePrice);
            week = 1;
            name = "Player1";
            startingMoney = new BigDecimal("10000");
            player = new Player(name, startingMoney);
            archive = new TransactionArchive();
            purchase = new Purchase(share, week);
            sale = new Sale(share, week);
        }

       
        @Test
        void testConstructor() {
            assertTrue(archive.isEmpty());
        }

        @Test
        void testInvalidAdd() {
            assertFalse(archive.add(null));
            assertFalse(archive.add(purchase));
        }

        @Test
        void testValidAdd() {
            purchase.commit(player);
            assertTrue(archive.add(purchase));

            sale.commit(player);
            assertTrue(archive.add(sale));
        }

        @Test
        void testIsEmpty() {
            assertTrue(archive.isEmpty());

            purchase.commit(player);
            archive.add(purchase);
            assertFalse(archive.isEmpty());
        }

        @Test
        void testGetTransaction() {
            assertEquals(List.of(), player.getTransactionArchive().getTransactions(week));

            purchase.commit(player);
            assertEquals(List.of(purchase), player.getTransactionArchive().getTransactions(week));

            assertEquals(List.of(), player.getTransactionArchive().getTransactions(2));
        }

        @Test
        void testGetPurchases() {
            assertEquals(List.of(), archive.getPurchases(week));

            purchase.commit(player);
            archive.add(purchase);
            assertEquals(List.of(purchase), archive.getPurchases(week));

            int emptyWeek = 2;
            assertEquals(List.of(), archive.getPurchases(emptyWeek));
        }

        @Test
        void testGetSales() {
            assertEquals(List.of(), player.getTransactionArchive().getSales(week));

            player.getPortfolio().addShare(share);
            sale.commit(player);
            archive.add(sale);
            assertEquals(List.of(sale), archive.getSales(week));

            int emptyWeek = 2;
            assertEquals(List.of(), archive.getSales(emptyWeek));
        }

        @Test
        void testCountDistinctWeeks() {
            assertEquals(0, archive.countDistinctWeeks());

            player.getPortfolio().addShare(share);
            sale.commit(player);
            archive.add(sale);
            assertEquals(1, archive.countDistinctWeeks());

            int newWeek = 2;
            purchase = new Purchase(share, newWeek);
            purchase.commit(player);
            archive.add(purchase);
            assertEquals(2, archive.countDistinctWeeks());
        }
    }
