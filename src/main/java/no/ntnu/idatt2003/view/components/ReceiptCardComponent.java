package no.ntnu.idatt2003.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;

/**
 * UI component representing a single transaction receipt card.
 *
 * <p>Shows the stock symbol, company, transaction type, week, and total.
 * Click the card to open the full receipt dialog.</p>
 */
public class ReceiptCardComponent extends VBox {

    /** Inline style applied to the card container. */
    private static final String CARD_STYLE =
        "-fx-background-color:white;"
        + "-fx-border-color:#ddd;"
        + "-fx-border-width:1;"
        + "-fx-border-radius:6;"
        + "-fx-background-radius:6;"
        + "-fx-cursor:hand;";

    /** Color used for buy-type transaction labels. */
    private static final String COLOR_BUY  = "#27ae60";

    /** Color used for sell-type transaction labels. */
    private static final String COLOR_SELL = "#c0392b";

    /** Vertical translation applied on mouse hover. */
    private static final double HOVER_TRANSLATE_Y = -2;

    /** Horizontal side padding for card rows. */
    private static final double ROW_SIDE_PAD = 10;

    /** Top padding of the main (bottom) row. */
    private static final double MAIN_ROW_TOP = 8;

    /** Bottom padding of the main (bottom) row. */
    private static final double MAIN_ROW_BOTTOM = 2;

    /** Bottom padding of the sub (top) row. */
    private static final double SUB_ROW_BOTTOM = 8;

    /**
     * Creates a receipt card for a given transaction.
     *
     * @param r the receipt data containing transaction details
     */
    public ReceiptCardComponent(final ReceiptData r) {
        setStyle(CARD_STYLE);
        setOnMouseEntered(e -> setTranslateY(HOVER_TRANSLATE_Y));
        setOnMouseExited(e -> setTranslateY(0));
        getChildren().addAll(
            buildSubRow(r),
            buildMainRow(r)
        );
    }

    /**
     * Builds the main row showing the stock symbol and signed total amount.
     *
     * @param r the receipt data
     * @return formatted main row
     */
    private HBox buildMainRow(final ReceiptData r) {
        Label symbolLabel = new Label(r.symbol());
        symbolLabel.setStyle(
            "-fx-font-weight:bold; -fx-font-size:16px; -fx-text-fill:#222;"
        );

        boolean isPurchase = r.type().equalsIgnoreCase("Purchase");
        String sign = isPurchase ? "-" : "";
        Label amountLabel = new Label(
            String.format("%s%.2f NOK", sign, r.total())
        );
        amountLabel.setStyle(
            "-fx-font-weight:bold; -fx-font-size:16px; -fx-text-fill:#222;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox box = new HBox(symbolLabel, spacer, amountLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(
            MAIN_ROW_TOP, ROW_SIDE_PAD, MAIN_ROW_BOTTOM, ROW_SIDE_PAD
        ));
        return box;
    }

    /**
     * Builds the sub-row showing the transaction type and week.
     *
     * @param r the receipt data
     * @return formatted sub-row
     */
    private HBox buildSubRow(final ReceiptData r) {
        boolean isPurchase = r.type().equalsIgnoreCase("Purchase");
        String typeColor = isPurchase ? COLOR_BUY : COLOR_SELL;

        Label typeLabel = new Label(
            r.type().toUpperCase() + " · Week " + r.week()
        );
        typeLabel.setStyle(
            "-fx-font-size:11px; -fx-text-fill:" + typeColor + ";"
        );

        HBox box = new HBox(typeLabel);
        box.setPadding(
            new Insets(0, ROW_SIDE_PAD, SUB_ROW_BOTTOM, ROW_SIDE_PAD)
        );
        return box;
    }

}
