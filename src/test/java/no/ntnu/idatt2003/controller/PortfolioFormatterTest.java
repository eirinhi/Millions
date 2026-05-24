package no.ntnu.idatt2003.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatt2003.controller.PortfolioFormatter.HoldingRow;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;
import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Purchase;
import no.ntnu.idatt2003.model.entity.Sale;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;

class PortfolioFormatterTest {

    private static final double DELTA = 0.0001;

    private PortfolioFormatter formatter;
    private Player player;
    private Stock stock;

    @BeforeEach
    void setUp() {
        formatter = new PortfolioFormatter();
        player = new Player("Investor", new BigDecimal("1000"));
        stock = new Stock(
            "AAPL",
            "Apple",
            List.of(new BigDecimal("100"), new BigDecimal("120"))
        );
    }

    @Test
    void performancePercent_returnsGainRelativeToStartingMoney() {
        player.addMoney(new BigDecimal("250"));

        double performance = formatter.performancePercent(player);

        assertEquals(25.0, performance, DELTA);
    }

    @Test
    void holdingRows_groupsSharesBySymbolAndCalculatesReturn() {
        player.getPortfolio().addShare(
            new Share(stock, new BigDecimal("2"), new BigDecimal("100"))
        );
        player.getPortfolio().addShare(
            new Share(stock, new BigDecimal("3"), new BigDecimal("110"))
        );

        List<HoldingRow> rows = formatter.holdingRows(player);

        assertEquals(1, rows.size());
        HoldingRow row = rows.get(0);
        assertEquals("AAPL", row.symbol());
        assertEquals("Apple", row.company());
        assertEquals(2, row.shareCount());
        assertEquals(5, row.quantity());
        assertEquals(600.0, row.value(), DELTA);
        assertEquals(13.2075, row.returnPct(), DELTA);
    }

    @Test
    void holdingRows_returnsEmptyListWhenPortfolioIsEmpty() {
        assertTrue(formatter.holdingRows(player).isEmpty());
    }

    @Test
    void receipts_mapsPurchaseAndSaleTransactions() {
        Share share = new Share(stock, new BigDecimal("2"), new BigDecimal("100"));
        Purchase purchase = new Purchase(share, 3);
        purchase.commit(player);

        Sale sale = new Sale(share, 4);
        sale.commit(player);

        List<ReceiptData> receipts = formatter.receipts(player);

        assertEquals(2, receipts.size());

        ReceiptData purchaseReceipt = receipts.get(0);
        assertEquals("Purchase", purchaseReceipt.type());
        assertEquals("AAPL", purchaseReceipt.symbol());
        assertEquals("Apple", purchaseReceipt.company());
        assertEquals(3, purchaseReceipt.week());
        assertEquals(2, purchaseReceipt.quantity());
        assertEquals(100.0, purchaseReceipt.price(), DELTA);
        assertEquals(200.0, purchaseReceipt.gross(), DELTA);
        assertEquals(1.0, purchaseReceipt.commission(), DELTA);
        assertEquals(0.0, purchaseReceipt.tax(), DELTA);
        assertEquals(201.0, purchaseReceipt.total(), DELTA);

        ReceiptData saleReceipt = receipts.get(1);
        assertEquals("Sale", saleReceipt.type());
        assertEquals(4, saleReceipt.week());
        assertEquals(240.0, saleReceipt.gross(), DELTA);
        assertEquals(2.4, saleReceipt.commission(), DELTA);
        assertEquals(11.28, saleReceipt.tax(), DELTA);
        assertEquals(226.32, saleReceipt.total(), DELTA);
    }

    @Test
    void receipts_returnsEmptyListWhenNoTransactionsExist() {
        assertTrue(formatter.receipts(player).isEmpty());
    }
}
