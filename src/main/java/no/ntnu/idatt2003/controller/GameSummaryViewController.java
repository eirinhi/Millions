package no.ntnu.idatt2003.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.logic.Exchange;
import no.ntnu.idatt2003.view.GameSummaryView;

/**
 * Controller for the game summary view.
 */
public class GameSummaryViewController {

    /** The view for the game summary. */
    private final GameSummaryView view;

    /**
     * Creates a new game summary view controller.
     *
     * @param exchange the exchange to sell all stocks from
     * @param player the player whose performance is being summarized
     */
    public GameSummaryViewController(
            final Exchange exchange,
            final Player player) {
        exchange.sellAll(player);
        BigDecimal gain = player.getMoney().subtract(player.getStartingMoney());
        BigDecimal returnPct = gain
            .multiply(new BigDecimal("100"))
            .divide(player.getStartingMoney(), 2, RoundingMode.HALF_UP);

        boolean gainPositive = gain.compareTo(BigDecimal.ZERO) >= 0;
        boolean returnPositive = returnPct.compareTo(BigDecimal.ZERO) >= 0;

        this.view = new GameSummaryView(
            player.getStatus(),
            player.getStartingMoney(),
            player.getMoney(),
            gain,
            returnPct,
            gainPositive,
            returnPositive
        );
    }

    /**
     * Returns the view for this game summary controller.
     *
     * @return the game summary view to be displayed
     */
    public GameSummaryView getView() {
        return view;
    }
}
