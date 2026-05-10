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

    /** The view for the game summary, created lazily on first access. */
    private GameSummaryView view;

    /** The player's status at the end of the game. */
    private final String status;

    /** The player's starting money. */
    private final BigDecimal startMoney;

    /** The player's ending money after selling all shares. */
    private final BigDecimal endMoney;

    /** The total gain or loss. */
    private final BigDecimal gain;

    /** The total return percentage. */
    private final BigDecimal returnPct;

    /** Whether the gain is positive. */
    private final boolean gainPositive;

    /** Whether the return percentage is positive. */
    private final boolean returnPositive;

    /**
     * Creates a new game summary view controller.
     * Sells all player shares and computes summary statistics.
     *
     * @param exchange the exchange to sell all stocks from
     * @param player the player whose performance is being summarized
     */
    public GameSummaryViewController(
            final Exchange exchange,
            final Player player) {
        exchange.sellAll(player);
        this.status = player.getStatus();
        this.startMoney = player.getStartingMoney();
        this.endMoney = player.getMoney();
        this.gain = endMoney.subtract(startMoney);
        this.returnPct = gain
            .multiply(new BigDecimal("100"))
            .divide(startMoney, 2, RoundingMode.HALF_UP);
        this.gainPositive = gain.compareTo(BigDecimal.ZERO) >= 0;
        this.returnPositive = returnPct.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Returns the view for this game summary controller.
     * The view is created lazily on first access.
     *
     * @return the game summary view to be displayed
     */
    public GameSummaryView getView() {
        if (view == null) {
            view = new GameSummaryView(
                status, startMoney, endMoney,
                gain, returnPct, gainPositive, returnPositive
            );
        }
        return view;
    }

    /**
     * Returns the total gain or loss.
     *
     * @return the gain
     */
    public BigDecimal getGain() {
        return gain;
    }

    /**
     * Returns the total return percentage.
     *
     * @return the return percentage
     */
    public BigDecimal getReturnPct() {
        return returnPct;
    }

    /**
     * Returns whether the gain is positive.
     *
     * @return true if gain is positive or zero
     */
    public boolean isGainPositive() {
        return gainPositive;
    }

    /**
     * Returns whether the return percentage is positive.
     *
     * @return true if return percentage is positive or zero
     */
    public boolean isReturnPositive() {
        return returnPositive;
    }
}
