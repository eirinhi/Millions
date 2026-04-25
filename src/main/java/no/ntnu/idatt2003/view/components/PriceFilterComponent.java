package no.ntnu.idatt2003.view.components;

import java.math.BigDecimal;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idatt2003.controller.ExchangeViewController;

public class PriceFilterComponent extends HBox {

    public PriceFilterComponent(ExchangeViewController controller) {
        super(12);

        Slider minSlider = new Slider(0, 10000, 0);
        Slider maxSlider = new Slider(0, 10000, 10000);
        
        Label rangeLabel = new Label("0 - 10 000 NOK");

        minSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            rangeLabel.setText((int) minSlider.getValue() + " - " + (int) maxSlider.getValue() + " NOK");
            controller.onPriceFilter(BigDecimal.valueOf(minSlider.getValue()), BigDecimal.valueOf(maxSlider.getValue()));
        });

        maxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            rangeLabel.setText((int) minSlider.getValue() + " - " + (int) maxSlider.getValue() + " NOK");
            controller.onPriceFilter(BigDecimal.valueOf(minSlider.getValue()), BigDecimal.valueOf(maxSlider.getValue()));
        });

        Label minLabel = new Label("Min");
        HBox minRow = new HBox(8, minLabel, minSlider);

        Label maxLabel = new Label("Max");
        HBox maxRow = new HBox(8, maxLabel, maxSlider);

        VBox sliders = new VBox(6, minRow, maxRow);

        this.getChildren().addAll(sliders, rangeLabel);
    }
}
