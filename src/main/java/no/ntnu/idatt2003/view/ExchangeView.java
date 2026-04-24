package no.ntnu.idatt2003.view;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import no.ntnu.idatt2003.controller.ExchangeViewController;

public class ExchangeView extends GridPane {

    public ExchangeView(ExchangeViewController controller) {
        this.add(new Label("Search bar"), 0, 0);
        this.add(new Label("Price filter"), 1, 0);
        this.add(new Label("Stock table"), 0, 1);
        this.add(new Label("Winners and losers"), 1, 1);
    }
}
