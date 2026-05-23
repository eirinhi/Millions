package no.ntnu.idatt2003.view.components;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.PortfolioFormatter.HoldingRow;
import no.ntnu.idatt2003.view.SoundPlayer;

/**
 * Constructs the holding table component, including title, header row,
 * and an empty body container for dynamic content.
 *
 * <p>The constructor initializes the layout structure and prepares the component
 * for later updates via the {@code update} method.
 */
public class HoldingsTableComponent extends VBox {

    private static final double[] COL_WIDTHS = {14, 32, 14, 14, 20, 20};

    private final VBox body = new VBox(2);

    private final Consumer<String> onStockSelected;

    public HoldingsTableComponent(final Consumer<String> onStockSelected) {
        this.onStockSelected = onStockSelected;
        setSpacing(0);

        Label title = new Label("Holdings");
        title.getStyleClass().add("section-title");

        getChildren().addAll(title, buildHeader(), body);
    }

    /**
     * Updates the table content with a new list of holdings.
     * 
     * <p>This method clears any existing rows and rebuilds the table body
     * based on the provided data. If the list is empty, a placeholder
     * message is displayed instead of table rows.
     *
     * @param rows the list of holding rows to display in the table
     */
    public void update(List<HoldingRow> rows) {
        body.getChildren().clear();

        if (rows.isEmpty()) {
            Label empty = new Label("No holdings yet.");
            empty.setStyle("-fx-text-fill:#888; -fx-font-size:12px;");
            empty.setPadding(new Insets(6, 0, 0, 0));
            body.getChildren().add(empty);
            return;
        }

        for (HoldingRow row : rows) {
            body.getChildren().add(buildRow(row));
        }
    }

    /**
     * Builds the header row of the table.
     *
     * <p>The header contains static labels for each column. It is styled with
     * a bottom border and padding to visually separate it from the body rows.
     *
     * @return a {@link GridPane} representing the table header
     */
    private GridPane buildHeader() {
        GridPane header = newGrid();
        addCell(header, "Symbol",  0, true);
        addCell(header, "Company", 1, true);
        addCell(header, "Shares",  2, true);
        addCell(header, "Stocks",  3, true);
        addCell(header, "Value",   4, true);
        addCell(header, "Return",  5, true);

        header.setStyle("-fx-border-color:#ccc; -fx-border-width:0 0 1 0;");
        header.setPadding(new Insets(0, 0, 4, 0));
        return header;
    }

    /**
     * Builds a single table row representing one holding.
     * 
     * <p>The row includes stock information, quantity, total value, and return percentage.
     * Positive and negative returns are styled differently for better visual clarity.
     *
     * @param r the holding data to render
     * @return an {@link HBox} representing the formatted row
     */
    private HBox buildRow(HoldingRow r) {

        GridPane grid = newGrid();
        addCell(grid, r.symbol(),   0, false);
        addCell(grid, r.company(),  1, false);
        addCell(grid, Integer.toString(r.shareCount()), 2, false);
        addCell(grid, Integer.toString(r.quantity()), 3, false);
        addCell(grid, String.format("%.2f", r.value()), 4, false);

        double pct = r.returnPct();
        boolean positive = pct >= 0;

        Label retLabel = new Label(String.format("%+.2f%%", pct));

        if (positive) {
            retLabel.getStyleClass().add("positive-value");
        } else {
            retLabel.getStyleClass().add("negative-value");
        }

        grid.add(retLabel, 5, 0);

        HBox wrapper = new HBox(grid);
        HBox.setHgrow(grid, Priority.ALWAYS);
        wrapper.setOnMouseEntered(e -> wrapper.setStyle(
              "-fx-background-color: #fff5fa;"
            + " -fx-background-radius: 8px;"
            + " -fx-cursor: hand;"
        ));
        wrapper.setOnMouseExited(e -> wrapper.setStyle(""));
        wrapper.setOnMouseClicked(e -> {
            SoundPlayer.playClick();
            onStockSelected.accept(r.symbol());
        });
        return wrapper;
    }

    /**
     * Creates a new grid layout used for both header and data rows.
     *
     * <p>The grid defines fixed column widths and spacing between cells to ensure
     * a consistent layout.
     *
     * @return a {@link GridPane} instance configured for the table layout
     */
    private GridPane newGrid() {
        GridPane g = new GridPane();
        g.setHgap(12);
        g.setMaxWidth(Double.MAX_VALUE);
        for (double w : COL_WIDTHS) {
            ColumnConstraints cc = new ColumnConstraints(w);
            cc.setPercentWidth(w);
            g.getColumnConstraints().add(cc);
        }
        return g;
    }

    /**
     * Adds a single text cell to a grid column.
     *
     * <p>The method applies appropriate styling depending on whether the cell is
     * part of a header row or a body row.
     *
     * @param g      the grid pane to add the cell to
     * @param text   the text content of the cell
     * @param col    the column index for the cell
     * @param header true if the cell is part of the header row, false otherwise
     */
    private void addCell(GridPane g, String text, int col, boolean header) {
        Label l = new Label(text);

        if (header) {
            l.getStyleClass().add("table-header");
        } else {
            l.getStyleClass().add("table-cell");
        }

        g.add(l, col, 0);
    }
}

