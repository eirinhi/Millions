package no.ntnu.idatt2003.view.components;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.ExchangeViewController;

public class SearchBarComponent extends VBox {

    public SearchBarComponent(ExchangeViewController controller) {
        super(8);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or symbol...");
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> controller.onSearch(newVal));

        Button alphabeticalBtn = new Button("Alphabetical");
        alphabeticalBtn.setOnAction(e -> controller.onSort("name"));

        Button ascendingBtn = new Button("Ascending Price");
        ascendingBtn.setOnAction(e -> controller.onSort("priceAsc"));

        Button descendingBtn = new Button("Descending Price");
        descendingBtn.setOnAction(e -> controller.onSort("priceDesc"));

        HBox filterButtons = new HBox(8);
        filterButtons.getChildren().addAll(alphabeticalBtn, ascendingBtn, descendingBtn);

        this.getChildren().addAll(searchField, filterButtons);
    }
}
