package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.model.entity.Purchase;
import no.ntnu.idatt2003.model.entity.Transaction;

/**
 * Dialog that displays a receipt for a completed buy or sell transaction.
 *
 * <p>Can be reused wherever a transaction receipt is needed.
 * The caller is responsible for calling {@code initOwner} before showing.</p>
 */
public class TransactionReceiptDialog extends Dialog<ButtonType> {

    /** Top padding for the header section. */
    private static final double HEADER_TOP_PAD = 4;

    /** Bottom padding for the header section. */
    private static final double HEADER_BOT_PAD = 8;

    /** Horizontal gap in the detail grid. */
    private static final double DETAIL_HGAP = 16;

    /** Vertical gap in both the detail and financials grids. */
    private static final double GRID_VGAP = 8;

    /** Top padding for the detail grid. */
    private static final double DETAIL_TOP_PAD = 12;

    /** Bottom padding for the detail grid. */
    private static final double DETAIL_BOT_PAD = 4;

    /** Minimum width for the value column in the financials grid. */
    private static final double VALUE_COL_MIN_WIDTH = 120;

    /** Side padding for the dialog content. */
    private static final double CONTENT_SIDE_PAD = 20;

    /** Bottom padding for the dialog content. */
    private static final double CONTENT_BOT_PAD = 16;

    /** Preferred width of the dialog content. */
    private static final double CONTENT_PREF_WIDTH = 380;

    /**
     * Creates a receipt dialog for the given transaction.
     *
     * @param transaction the completed transaction to display
     */
    public TransactionReceiptDialog(final Transaction transaction) {
        boolean isBuy = transaction instanceof Purchase;

        setTitle(isBuy ? "Purchase Receipt" : "Sale Receipt");
        setHeaderText(null);
        getDialogPane().setGraphic(null);
        getDialogPane().getStylesheets().add(
            getClass().getResource("/no/ntnu/idatt2003/styles.css")
            .toExternalForm()
        );
        getDialogPane().getButtonTypes().add(ButtonType.OK);

        String company = transaction.getShare().getStock().getCompany();
        String symbol = transaction.getShare().getStock().getSymbol();
        int week = transaction.getWeek();
        BigDecimal quantity = transaction.getShare().getQuantity();
        BigDecimal price = transaction.getShare().getPurchasePrice();
        BigDecimal gross = transaction.getCalculator().calculateGross();
        BigDecimal commission =
        transaction.getCalculator().calculateCommission();
        BigDecimal tax = transaction.getCalculator().calculateTax();
        BigDecimal total = transaction.getCalculator().calculateTotal();

        Label symbolLabel  = new Label(symbol);
        Label companyLabel = new Label(company);
        symbolLabel.getStyleClass().add("receipt-header-symbol");
        companyLabel.getStyleClass().add("receipt-header-company");

        Label typeBadge = new Label(isBuy ? "BUY" : "SELL");
        typeBadge.getStyleClass().addAll(
            "receipt-badge",
            isBuy ? "receipt-badge-buy" : "receipt-badge-sell"
        );

        VBox headerLeft = new VBox(2, symbolLabel, companyLabel);
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(headerLeft, headerSpacer, typeBadge);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(
            new Insets(HEADER_TOP_PAD, 0, HEADER_BOT_PAD, 0)
        );

        GridPane detailGrid = new GridPane();
        detailGrid.setHgap(DETAIL_HGAP);
        detailGrid.setVgap(GRID_VGAP);
        detailGrid.setPadding(
            new Insets(DETAIL_TOP_PAD, 0, DETAIL_BOT_PAD, 0)
        );

        addDetailRow(detailGrid, 0, "Week", String.valueOf(week));
        addDetailRow(detailGrid, 1, "Quantity", quantity + " shares");
        addDetailRow(detailGrid, 2, "Purchase Price", price + " NOK");

        GridPane financialsGrid = new GridPane();
        financialsGrid.setVgap(GRID_VGAP);

        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setHgrow(Priority.ALWAYS);
        ColumnConstraints valueCol = new ColumnConstraints();
        valueCol.setHgrow(Priority.NEVER);
        valueCol.setMinWidth(VALUE_COL_MIN_WIDTH);
        financialsGrid.getColumnConstraints().addAll(labelCol, valueCol);

        addFinancialRow(financialsGrid, 0, "Gross", gross + " NOK");
        addFinancialRow(financialsGrid, 1, "Commission", commission + " NOK");
        addFinancialRow(financialsGrid, 2, "Taxes", tax + " NOK");

        Label totalTitle = new Label("Total");
        totalTitle.getStyleClass().add("receipt-total-title");
        
        Label totalValue = new Label(total + " NOK");
        totalValue.getStyleClass().add("receipt-total-value");
        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);
        HBox totalRow = new HBox(totalTitle, totalSpacer, totalValue);
        totalRow.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox content = new VBox(
            header,
            detailGrid,
            new Separator(),
            financialsGrid,
            spacer,
            new Separator(),
            totalRow
        );
        content.setPadding(new Insets(
            0, CONTENT_SIDE_PAD, CONTENT_BOT_PAD, CONTENT_SIDE_PAD
        ));
        content.setPrefWidth(CONTENT_PREF_WIDTH);

        getDialogPane().setContent(content);
    }

    /**
     * Adds a labelled detail row to the given grid.
     *
     * @param grid the grid to add the row to
     * @param row the row index
     * @param title the label text
     * @param value the value text
     */
    private void addDetailRow(
            final GridPane grid,
            final int row,
            final String title,
            final String value) {

        Label titleLabel = new Label(title + ":");
        titleLabel.getStyleClass().add("receipt-detail-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("receipt-detail-value");

        grid.add(titleLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }

    /**
     * Adds a financial row with right-aligned value to the given grid.
     *
     * @param grid the grid to add the row to
     * @param row the row index
     * @param title the label text
     * @param value the value text
     */
    private void addFinancialRow(
            final GridPane grid,
            final int row,
            final String title,
            final String value) {

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("receipt-row-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("receipt-row-value");
        GridPane.setHalignment(valueLabel, HPos.RIGHT);

        grid.add(titleLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }
}
