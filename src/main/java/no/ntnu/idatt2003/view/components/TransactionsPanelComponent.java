package no.ntnu.idatt2003.view.components;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;
import no.ntnu.idatt2003.view.SoundPlayer;

/**
 * A scrollable panel that displays one {@link ReceiptCardComponent} per
 * transaction, with the most recent transaction shown at the top.
 *
 * <p>Call {@link #update(List)} to replace all cards.
 */
public class TransactionsPanelComponent extends VBox {

    /** Preferred width of this panel in pixels. */
    private static final double PANEL_WIDTH  = 270;

    /** Preferred height of the scroll area in pixels. */
    private static final double SCROLL_HEIGHT = 340;

    /** Spacing between receipt cards. */
    private static final int CARD_SPACING = 8;

    /** Padding inside the cards container. */
    private static final double BOX_PADDING = 4;

    /** Inner container rebuilt on every {@link #update(List)} call. */
    private final VBox cardsBox = new VBox(CARD_SPACING);

    /** Called with the receipt data when a transaction card is clicked. */
    private Consumer<ReceiptData> onReceiptClick;

    /** Called with the search text when the user types in the search field. */
    private Consumer<String> onSearch;

    /** Called with the filter value when the user clicks a filter button. */
    private Consumer<String> onFilter;

    /**
     * Constructs the transactions panel with a title and scrollable card area.
     */
    public TransactionsPanelComponent() {
        setSpacing(CARD_SPACING);
        setPrefWidth(PANEL_WIDTH);

        Label title = new Label("Transactions");
        title.getStyleClass().add("section-title");

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.textProperty().addListener((obs, old, val) -> {
            if (onSearch != null) onSearch.accept(val.toLowerCase());
        });

        Button allTransactionsBtn = new Button("All");
        allTransactionsBtn.getStyleClass().add("sort-btn");
        allTransactionsBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(allTransactionsBtn, Priority.ALWAYS);

        Button purchacesBtn = new Button("Purchases");
        purchacesBtn.getStyleClass().add("sort-btn");
        purchacesBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(purchacesBtn, Priority.ALWAYS);

        Button salesBtn = new Button("Sales");
        salesBtn.getStyleClass().add("sort-btn");
        salesBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(salesBtn, Priority.ALWAYS);

        allTransactionsBtn.setOnAction(e -> {
            SoundPlayer.playClick();
            if (onFilter != null) onFilter.accept("all");
        });

        purchacesBtn.setOnAction(e -> {
            SoundPlayer.playClick();
            if (onFilter != null) onFilter.accept("Purchase");
        });

        salesBtn.setOnAction(e -> {
            SoundPlayer.playClick();
            if (onFilter != null) onFilter.accept("Sale");
        });

        HBox filterButtons = new HBox(CARD_SPACING, allTransactionsBtn, purchacesBtn, salesBtn);


        cardsBox.setPadding(new Insets(BOX_PADDING));

        ScrollPane scroll = new ScrollPane(cardsBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefHeight(SCROLL_HEIGHT);
        scroll.setStyle(
            "-fx-background:transparent;"
            + "-fx-background-color:transparent;"
        );

        getChildren().addAll(title, searchField, filterButtons, scroll);
    }

    /**
     * Sets the callback invoked when a transaction card is clicked.
     *
     * @param handler called with the clicked receipt's data
     */
    public void setOnReceiptClick(final Consumer<ReceiptData> handler) {
        this.onReceiptClick = handler;
    }

    /**
     * Sets the callback invoked when the user types in the search field.
     *
     * @param handler called with the lowercase search text
     */
    public void setOnSearch(final Consumer<String> handler) {
        this.onSearch = handler;
    }

    /**
     * Sets the callback invoked when the user clicks a filter button.
     *
     * @param handler called with "all", "Purchase", or "Sale"
     */
    public void setOnFilter(final Consumer<String> handler) {
        this.onFilter = handler;
    }

    /**
     * Replaces all receipt cards with those built from {@code receipts}.
     *
     * @param receipts transactions to display; may be empty
     */
    public void update(final List<ReceiptData> receipts) {
        cardsBox.getChildren().clear();

        if (receipts.isEmpty()) {
            Label empty = new Label("No transactions found");
            empty.setStyle("-fx-text-fill: #222;");
            cardsBox.getChildren().add(empty);
            return;
        }

        for (ReceiptData r : receipts) {
            ReceiptCardComponent card = new ReceiptCardComponent(r);
            if (onReceiptClick != null) {
                card.setOnMouseClicked(e -> {
                    SoundPlayer.playClick();
                    onReceiptClick.accept(r);
                });
            }
            cardsBox.getChildren().add(card);
        }
    }
}
