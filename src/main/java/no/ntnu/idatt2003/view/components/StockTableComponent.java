package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.ExchangeViewController;
import no.ntnu.idatt2003.model.entity.Stock;

public class StockTableComponent extends VBox {

        TableView<Stock> table;

    public StockTableComponent(List<Stock> stocks, ExchangeViewController controller) {
        super(10);

        table = new TableView<>();

        TableColumn<Stock, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));

        TableColumn<Stock, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("company"));

        TableColumn<Stock, BigDecimal> returnCol = new TableColumn<>("Return");
        returnCol.setCellValueFactory(new PropertyValueFactory<>("weeklyReturnPercentage"));

        TableColumn<Stock, BigDecimal> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("salesPrice"));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().addAll(List.of(symbolCol, nameCol, returnCol, priceCol));
        table.setMaxWidth(Double.MAX_VALUE);
        table.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(table, Priority.ALWAYS);

        update(stocks);
        this.setMaxHeight(Double.MAX_VALUE);
        this.getChildren().add(table);
    }

    public void update(List<Stock> stocks) {
        table.getItems().setAll(stocks);
    }
}
