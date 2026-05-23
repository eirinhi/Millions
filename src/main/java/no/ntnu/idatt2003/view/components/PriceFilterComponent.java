package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.ExchangeViewController;

/**
 * Component for filtering stocks by price range.
 * Contains two sliders for minimum and maximum price,
 * and a label showing the current range.
 */
public class PriceFilterComponent extends HBox {

    /** Spacing between elements. */
    private static final int SPACING = 12;

    /** Maximum price for the sliders. */
    private static final int MAX_PRICE = 10000;

    /** Spacing inside slider rows. */
    private static final int INNER_SPACING = 8;

    /** Spacing between slider rows. */
    private static final int SLIDER_SPACING = 6;

    /**
     * Creates a new PriceFilterComponent with sliders for min and max price.
     * Calls the controller's onPriceFilter method when sliders are adjusted.
     * @param controller the controller for this component
     */
    public PriceFilterComponent(final ExchangeViewController controller) {
        super(SPACING);
        this.setFillHeight(false);
        this.setAlignment(Pos.CENTER_LEFT);

        Slider minSlider = new Slider(0, MAX_PRICE, 0);
        Slider maxSlider = new Slider(0, MAX_PRICE, MAX_PRICE);

        Label rangeLabel = new Label("0 - 10 000 $");

        // Update range label and notify controller when sliders change
        minSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            rangeLabel.setText(
                (int) minSlider.getValue()
                + " - " + (int) maxSlider.getValue() + " $");
            controller.onPriceFilter(
                BigDecimal.valueOf(minSlider.getValue()),
                BigDecimal.valueOf(maxSlider.getValue()));
        });

        maxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            rangeLabel.setText(
                (int) minSlider.getValue()
                + " - " + (int) maxSlider.getValue() + " $");
            controller.onPriceFilter(
                BigDecimal.valueOf(minSlider.getValue()),
                BigDecimal.valueOf(maxSlider.getValue()));
        });

        Label minLabel = new Label("Min");
        HBox minRow = new HBox(INNER_SPACING, minLabel, minSlider);

        Label maxLabel = new Label("Max");
        HBox maxRow = new HBox(INNER_SPACING, maxLabel, maxSlider);

        VBox sliders = new VBox(SLIDER_SPACING, minRow, maxRow);

        this.getChildren().addAll(sliders, rangeLabel);
    }
}
