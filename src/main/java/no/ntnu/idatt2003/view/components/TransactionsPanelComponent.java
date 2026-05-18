package no.ntnu.idatt2003.view.components;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.PortfolioFormatter.ReceiptData;

/**
 * A scrollable panel that displays one {@link ReceiptCardComponent} per
 * transaction, with the most recent transaction shown at the top.
 *
 * <p>Call {@link #update(List)} to replace all cards.
 */
public class TransactionsPanelComponent extends VBox {

    private static final double PANEL_WIDTH  = 270;
    private static final double SCROLL_HEIGHT = 340;

    /** Inner container rebuilt on every {@link #update(List)} call. */
    private final VBox cardsBox = new VBox(8);

    public TransactionsPanelComponent() {
        setSpacing(8);
        setPrefWidth(PANEL_WIDTH);

        Label title = new Label("Transactions");
        title.getStyleClass().add("section-title");

        cardsBox.setPadding(new Insets(4));

        ScrollPane scroll = new ScrollPane(cardsBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefHeight(SCROLL_HEIGHT);
        scroll.setStyle(
            "-fx-background:transparent;"
            + "-fx-background-color:transparent;"
        );

        getChildren().addAll(title, scroll);
    }

    /**
     * Replaces all receipt cards with those built from {@code receipts}.
     * The list is shown newest-first (last element rendered at the top).
     *
     * @param receipts all transactions to display; may be empty
     */
    public void update(List<ReceiptData> receipts) {
        cardsBox.getChildren().clear();

        if (receipts.isEmpty()) {
            Label empty = new Label("No transactions yet.");
            empty.setStyle("-fx-text-fill:#888; -fx-font-size:12px;");
            cardsBox.getChildren().add(empty);
            return;
        }

        // Newest first
        List<ReceiptData> reversed = receipts.reversed();
        for (ReceiptData r : reversed) {
            cardsBox.getChildren().add(new ReceiptCardComponent(r));
        }
    }
}