package no.ntnu.idatt2003.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;

/**
 * UI component representing a single transaction receipt card.
 *
 * <p>This component visualizes a completed stock transaction (buy/sell)
 * including symbol, company name, quantity, price, fees, tax, and total value.
 *
 * <p>The card is styled as a compact vertical layout with separated sections
 * for readability and consistency in the portfolio transaction panel.
 */
public class ReceiptCardComponent extends VBox {

    private static final String CARD_STYLE =
        "-fx-background-color:white;"
        + "-fx-border-color:#ddd;"
        + "-fx-border-width:1;"
        + "-fx-border-radius:6;"
        + "-fx-background-radius:6;";

    private static final String COLOR_PURCHASE = "#1a6fc4";
    private static final String COLOR_SALE     = "#c0392b";

    /**
     * Creates a receipt card for a given transaction.
     *
     * @param r the receipt data containing transaction details
     */
    public ReceiptCardComponent(ReceiptData r) {
        setStyle(CARD_STYLE);
        getChildren().addAll(
            buildHeader(r),
            buildQtyPrice(r),
            separator(),
            buildDetails(r),
            separator(),
            buildTotal(r)
        );
    }

    /**
     * Builds the header section containing stock symbol, company name,
     * transaction type, and week number.
     *
     * @param r the receipt data
     * @return formatted header row
     */
    private HBox buildHeader(ReceiptData r) {
        Label symbolLabel = new Label(r.symbol() + " – " + r.company());
        symbolLabel.setStyle("-fx-font-weight:bold; -fx-font-size:12px;");

        boolean isPurchase = r.type().equalsIgnoreCase("Purchase");
        String typeColor   = isPurchase ? COLOR_PURCHASE : COLOR_SALE;

        Label typeLabel = new Label("Week " + r.week() + " · " + r.type().toUpperCase());
        typeLabel.setStyle("-fx-text-fill:" + typeColor + "; -fx-font-size:11px;");

        HBox box = new HBox(symbolLabel, typeLabel);
        HBox.setHgrow(symbolLabel, Priority.ALWAYS);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(8, 10, 4, 10));
        return box;
    }

    /**
     * Builds the quantity and price section of the receipt.
     *
     * @param r the receipt data
     * @return formatted quantity/price row
     */
    private HBox buildQtyPrice(ReceiptData r) {
        Label label = new Label("Quantity: " + r.quantity() + "   Price: " + r.price());
        label.setStyle("-fx-font-size:11px; -fx-text-fill:#555;");
        HBox box = new HBox(label);
        box.setPadding(new Insets(0, 10, 6, 10));
        return box;
    }

    /**
     * Builds the transaction details section showing gross value,
     * commission, and tax.
     *
     * @param r the receipt data
     * @return vertical box containing breakdown rows
     */
    private VBox buildDetails(ReceiptData r) {
        VBox box = new VBox(3,
            receiptLine("Gross",      r.gross()),
            receiptLine("Commission", r.commission()),
            receiptLine("Tax",        r.tax())
        );
        box.setPadding(new Insets(6, 10, 6, 10));
        return box;
    }

    /**
     * Builds the total section showing final transaction result.
     *
     * @param r the receipt data
     * @return formatted total row
     */
    private HBox buildTotal(ReceiptData r) {
        Label lbl = new Label("Total");
        lbl.setStyle("-fx-font-weight:bold; -fx-font-size:12px;");

        Label val = new Label(String.format("%.2f NOK", r.total()));
        val.setStyle("-fx-font-weight:bold; -fx-font-size:12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox box = new HBox(lbl, spacer, val);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 10, 8, 10));

        return box;
    }

    /**
     * Creates a single labeled row for receipt details.
     *
     * @param label the field name
     * @param value the numeric value
     * @return formatted row
     */
    private HBox receiptLine(String label, double value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:11px; -fx-text-fill:#444;");

        Label val = new Label(String.format("%.2f NOK", value));
        val.setStyle("-fx-font-size:11px; -fx-text-fill:#222;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(lbl, spacer, val);
        row.setAlignment(Pos.CENTER_LEFT);

        return row;
    }

    /**
     * Creates a thin visual separator line between sections.
     *
     * @return separator component
     */
    private HBox separator() {
        HBox sep = new HBox();
        sep.setStyle("-fx-border-color:#eee; -fx-border-width:1 0 0 0;");
        sep.setMinHeight(1);
        sep.setPrefHeight(1);
        return sep;
    }
}