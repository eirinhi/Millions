package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.view.GameSummaryView;

public class GameSummaryViewController {

    private final GameSummaryView view;

    public GameSummaryViewController(Exchange exchange, Player player) {
        exchange.sellAll(player);
        BigDecimal gain = player.getMoney().subtract(player.getStartingMoney());
        BigDecimal returnPct = gain
            .multiply(new BigDecimal("100"))
            .divide(player.getStartingMoney(), 2, RoundingMode.HALF_UP);

        this.view = new GameSummaryView(
            player.getStatus(),
            player.getStartingMoney(),
            player.getMoney(),
            gain,
            returnPct
        );
    }

    public GameSummaryView getView() {
        return view;
    }
}
