package no.ntnu.idatt2003.view.components;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.ExchangeViewController;
import no.ntnu.idatt2003.view.SoundPlayer;

/**
 * Component for searching and sorting stocks in the exchange view.
 * Contains a search field and buttons for sorting alphabetically,
 * by ascending price, and by descending price.
 */
public class SearchBarComponent extends VBox {

    /** Spacing between elements. */
    private static final int SPACING = 8;

    /**
     * Creates a new SearchBarComponent with a search field and sorting
     * buttons. Calls the controller's onSearch and onSort methods.
     * @param controller the controller for this component
     */
    public SearchBarComponent(final ExchangeViewController controller) {
        super(SPACING);

        // Create search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or symbol...");
        searchField.getStyleClass().add("search-field");
        searchField.textProperty().addListener(
            (obs, oldVal, newVal) -> controller.onSearch(newVal));

        // Create sorting buttons
        Button alphabeticalBtn = new Button("Alphabetical");
        alphabeticalBtn.getStyleClass().add("sort-btn");
        alphabeticalBtn.setOnAction(e -> {
            SoundPlayer.playClick();
            controller.onSort("name");
        });

        Button ascendingBtn = new Button("Ascending Price");
        ascendingBtn.getStyleClass().add("sort-btn");
        ascendingBtn.setOnAction(e -> {
            SoundPlayer.playClick();
            controller.onSort("priceAsc");
        });

        Button descendingBtn = new Button("Descending Price");
        descendingBtn.getStyleClass().add("sort-btn");
        descendingBtn.setOnAction(e -> {
            SoundPlayer.playClick();
            controller.onSort("priceDesc");
        });

        // Make buttons expand to fill available width
        alphabeticalBtn.setMaxWidth(Double.MAX_VALUE);
        ascendingBtn.setMaxWidth(Double.MAX_VALUE);
        descendingBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(alphabeticalBtn, Priority.ALWAYS);
        HBox.setHgrow(ascendingBtn, Priority.ALWAYS);
        HBox.setHgrow(descendingBtn, Priority.ALWAYS);

        HBox filterButtons = new HBox(SPACING);
        filterButtons.getChildren().addAll(
            alphabeticalBtn, ascendingBtn, descendingBtn);

        searchField.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().addAll(searchField, filterButtons);
    }
}
