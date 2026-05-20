package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;
import java.util.List;

import javafx.scene.Cursor;

import no.ntnu.idatt2003.view.SoundPlayer;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.ExchangeViewController;
import no.ntnu.idatt2003.model.entity.Stock;

/**
 * Component for displaying a table of stocks.
 * Contains a TableView with columns for symbol, name, return, and price.
 * The table can be updated with a new list of stocks using the update method.
 */
public class StockTableComponent extends VBox {

    /** Spacing between elements. */
    private static final int SPACING = 10;

    /** The table displaying the stocks. */
    private TableView<Stock> table;

    /**
     * Creates a new StockTableComponent with the given stocks and controller.
     * @param stocks the initial list of stocks to display
     * @param controller the controller for this component
     */
    public StockTableComponent(
        final List<Stock> stocks,
        final ExchangeViewController controller) {
        super(SPACING);

        table = new TableView<>();

        TableColumn<Stock, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(
            new PropertyValueFactory<>("symbol"));

        TableColumn<Stock, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(
            new PropertyValueFactory<>("company"));

        TableColumn<Stock, BigDecimal> returnCol =
            new TableColumn<>("Return");
        returnCol.setCellValueFactory(
            new PropertyValueFactory<>("weeklyReturnPercentage"));

        TableColumn<Stock, BigDecimal> priceCol =
            new TableColumn<>("Price");
        priceCol.setCellValueFactory(
            new PropertyValueFactory<>("salesPrice"));

        // Set the table to resize columns to fill available space
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().addAll(
            List.of(symbolCol, nameCol, returnCol, priceCol));
        table.setMaxWidth(Double.MAX_VALUE);
        table.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(table, Priority.ALWAYS);

        table.setRowFactory(tv -> {
            TableRow<Stock> row = new TableRow<>();
            row.setCursor(Cursor.HAND);

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    SoundPlayer.playClick();
                    controller.onStockSelected(row.getItem());
                }
            });

            return row;
        });

        update(stocks);

        this.setMaxHeight(Double.MAX_VALUE);
        this.getChildren().add(table);
    }

    /**
     * Updates the table with a new list of stocks.
     * @param stocks the new list of stocks to display in the table
     */
    public void update(final List<Stock> stocks) {
        table.getItems().setAll(stocks);
    }
}
