package no.ntnu.idatt2003.model.logic;

import no.ntnu.idatt2003.model.entity.Purchase;
import no.ntnu.idatt2003.model.entity.Sale;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Stock;
import no.ntnu.idatt2003.model.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFactoryTest {

  private Share share;

  @BeforeEach
  void setUp() {
    Stock stock = new Stock("AAPL", "Apple Inc.", List.of(new BigDecimal("150.00")));
    share = new Share(stock, new BigDecimal("10"), new BigDecimal("150.00"));
  }

  @Test
  void getPurchaseReturnsPurchaseTransaction() throws UnknownTransactionException {
    Transaction transaction = TransactionFactory.get("purchase", share, 1);
    assertInstanceOf(Purchase.class, transaction);
  }

  @Test
  void getSaleReturnsSaleTransaction() throws UnknownTransactionException {
    Transaction transaction = TransactionFactory.get("sale", share, 1);
    assertInstanceOf(Sale.class, transaction);
  }

  @Test
  void getUnknownTypeThrowsUnknownTransactionException() {
    assertThrows(UnknownTransactionException.class,
        () -> TransactionFactory.get("unknown", share, 1));
  }

  @Test
  void getNullTypeThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> TransactionFactory.get(null, share, 1));
  }

  @Test
  void getNullShareThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> TransactionFactory.get("purchase", null, 1));
  }

  @Test
  void getNegativeWeekThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> TransactionFactory.get("purchase", share, -1));
  }
}
