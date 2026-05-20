package no.ntnu.idatt2003.view.components;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

    /**
     * Constructs the transactions panel with a title and scrollable card area.
     */
    public TransactionsPanelComponent() {
        setSpacing(CARD_SPACING);
        setPrefWidth(PANEL_WIDTH);

        Label title = new Label("Transactions");
        title.getStyleClass().add("section-title");

        cardsBox.setPadding(new Insets(BOX_PADDING));

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
     * Sets the callback invoked when a transaction card is clicked.
     *
     * @param handler called with the clicked receipt's data
     */
    public void setOnReceiptClick(final Consumer<ReceiptData> handler) {
        this.onReceiptClick = handler;
    }

    /**
     * Replaces all receipt cards with those built from {@code receipts}.
     * The list is shown newest-first (last element rendered at the top).
     *
     * @param receipts all transactions to display; may be empty
     */
    public void update(final List<ReceiptData> receipts) {
        cardsBox.getChildren().clear();

        if (receipts.isEmpty()) {
            Label empty = new Label("No transactions yet.");
            empty.setStyle("-fx-text-fill:#888; -fx-font-size:12px;");
            cardsBox.getChildren().add(empty);
            return;
        }

        List<ReceiptData> reversed = receipts.reversed();
        for (ReceiptData r : reversed) {
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
