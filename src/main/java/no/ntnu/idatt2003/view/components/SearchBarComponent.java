package no.ntnu.idatt2003.view.components;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.ExchangeViewController;

public class SearchBarComponent extends VBox {

    public SearchBarComponent(ExchangeViewController controller) {
        super(8);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or symbol...");
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> controller.onSearch(newVal));

        this.getChildren().add(searchField);
    }
}
