package no.ntnu.idatt2003.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.model.entity.Stock;

/**
 * View showing the player's watched/favorite stocks.
 */
public class WatchlistView extends VBox {

    /** Spacing between child nodes. */
    private static final int SPACING = 16;

    /** Padding around the view. */
    private static final int PADDING = 24;

    /** Table displaying watched stocks. */
    private final TableView<Stock> table = new TableView<>();

    /** Empty-state label shown when no stocks are watched. */
    private final Label emptyLabel = new Label(
        "Your watchlist is empty. Add stocks from Exchange."
    );

    /**
     * Creates a watchlist view.
     *
     * @param stocks watched stocks to display
     * @param onStockSelected callback for opening a selected stock
     */
    public WatchlistView(
            final List<Stock> stocks,
            final Consumer<Stock> onStockSelected) {
        super(SPACING);
        setPadding(new Insets(PADDING));
        getStyleClass().add("watchlist-view");

        Label title = new Label("Watchlist");
        title.getStyleClass().add("watchlist-title");

        emptyLabel.getStyleClass().add("muted-label");
        setupTable(onStockSelected);

        getChildren().addAll(title, emptyLabel, table);
        update(stocks);
    }

    /**
     * Updates the displayed watchlist stocks.
     *
     * @param stocks watched stocks to display
     */
    public void update(final List<Stock> stocks) {
        table.getItems().setAll(stocks);
        table.refresh();
        boolean empty = stocks.isEmpty();
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);
        table.setVisible(!empty);
        table.setManaged(!empty);
    }

    private void setupTable(final Consumer<Stock> onStockSelected) {
        TableColumn<Stock, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));

        TableColumn<Stock, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("company"));

        TableColumn<Stock, BigDecimal> returnCol =
            new TableColumn<>("Return");
        returnCol.setCellValueFactory(
            new PropertyValueFactory<>("weeklyReturnPercentage"));

        TableColumn<Stock, BigDecimal> priceCol =
            new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("salesPrice"));

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
                    onStockSelected.accept(row.getItem());
                }
            });
            return row;
        });
    }
}
