package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.entity.Share;
import no.ntnu.idatt2003.model.entity.Transaction;

/**
 * Service class responsible for transforming portfolio domain data into
 * UI-ready representations and performing derived financial calculations.
 *
 * <p>This class acts as a bridge between the domain layer and the presentation
 * layer. It extracts, computes, and formats portfolio-related information into
 * simplified Data Transfer Objects (DTOs) used by the UI.
 *
 * <p>The class is stateless and does not modify any domain objects. It only
 * reads data from the model and produces calculated results.
 *
 * <p>Key responsibilities include:
 * <ul>
 *     <li>Calculating total stock value based on current market prices</li>
 *     <li>Computing portfolio performance relative to starting capital</li>
 *     <li>Transforming share holdings into UI-friendly DTOs</li>
 *     <li>Transforming transaction history into receipt representations</li>
 * </ul>
 */
public class PortfolioFormatter {

    /**
     * Creates a new instance of PortfolioFormatter.
     * The service is stateless and can be reused across the application.
     */
    public PortfolioFormatter() {}


    /**
     * Calculates the percentage return of the player's portfolio compared to
     * the starting capital.
     *
     * <p>The result represents overall portfolio performance, including both
     * cash balance and held assets.
     *
     * @param player the player whose performance is calculated
     * @return portfolio return in percent, or {@code 0.0} if undefined
     */
    public double performancePercent(Player player) {
        BigDecimal starting = safe(player.getStartingMoney());
        BigDecimal current = safe(player.getNetWorth());

        if (starting.compareTo(BigDecimal.ZERO) == 0) return 0.0;

        return current.subtract(starting)
                .multiply(BigDecimal.valueOf(100))
                .divide(starting, 4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Converts the player's current share holdings into UI-friendly DTOs.
     *
     * <p>Each holding contains symbol, company name, quantity, current value,
     * and individual return percentage.
     *
     * @param player the player whose holdings are mapped
     * @return list of holding DTOs for display in the UI
     */
    public List<HoldingRow> holdingRows(Player player) {
        return player.getPortfolio().getShares().stream()
                .map(s -> {

                    BigDecimal purchasePrice = safe(s.getPurchasePrice());
                    BigDecimal currentPrice = safe(s.getStock().getSalesPrice());

                    int qty = s.getQuantity().intValue();

                    BigDecimal value = currentPrice.multiply(BigDecimal.valueOf(qty));

                    double returnPct = returnPercent(purchasePrice, currentPrice);

                    return new HoldingRow(
                            s.getStock().getSymbol(),
                            s.getStock().getCompany(),
                            qty,
                            value.doubleValue(),
                            returnPct
                    );
                })
                .toList();
    }

    /**
     * Converts the player's transaction history into receipt DTOs.
     *
     * <p>Each receipt contains detailed information about a completed trade,
     * including taxes, commissions, and total cost or revenue.
     *
     * @param player the player whose transaction history is mapped
     * @return list of transaction receipts
     */
    public List<ReceiptData> receipts(Player player) {
        return player.getTransactionArchive().getAll().stream()
                .map(this::toReceipt)
                .toList();
    }

    /**
     * Converts a single transaction into a receipt DTO.
     *
     * @param t the transaction to convert
     * @return structured receipt representation
     */
    private ReceiptData toReceipt(Transaction t) {
        Share s = t.getShare();

        return new ReceiptData(
                t.getClass().getSimpleName(),
                s.getStock().getSymbol(),
                s.getStock().getCompany(),
                t.getWeek(),
                s.getQuantity().intValue(),
                s.getPurchasePrice().doubleValue(),
                t.getCalculator().calculateGross().doubleValue(),
                t.getCalculator().calculateCommission().doubleValue(),
                t.getCalculator().calculateTax().doubleValue(),
                t.getCalculator().calculateTotal().doubleValue()
        );
    }

    /**
     * Calculates percentage return between purchase price and current price.
     *
     * @param purchase original purchase price
     * @param current current market price
     * @return return percentage, or {@code 0.0} if invalid input
     */
    private double returnPercent(BigDecimal purchase, BigDecimal current) {
        if (purchase == null || purchase.compareTo(BigDecimal.ZERO) == 0) return 0.0;

        return current.subtract(purchase)
                .multiply(BigDecimal.valueOf(100))
                .divide(purchase, 4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Ensures a non-null BigDecimal value.
     *
     * @param v input value
     * @return original value or {@code BigDecimal.ZERO} if null
     */
    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    /**
     * Data Transfer Object representing a single portfolio holding.
     */
    public record HoldingRow(
            String symbol,
            String company,
            int quantity,
            double value,
            double returnPct
    ) {}

    /**
     * Data Transfer Object representing a completed transaction receipt.
     */
    public record ReceiptData(
            String type,
            String symbol,
            String company,
            int week,
            int quantity,
            double price,
            double gross,
            double commission,
            double tax,
            double total
    ) {}
}