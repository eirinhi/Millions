package no.ntnu.idatt2003.model.io;

import no.ntnu.idatt2003.model.entity.Player;
import no.ntnu.idatt2003.model.logic.Exchange;

/**
 * Holds the result of loading a saved game.
 * The object contains the reconstructed player and exchange.
 */
public class GameLoadResult {
    
    /** The reconstructed player. */
    private final Player player;

    /** The reconstructed exchange. */
    private final Exchange exchange;

    /**
     * Creates a new GameLoadResult.
     *
     * @param player   the reconstructed player
     * @param exchange the reconstructed exchange
     */
    public GameLoadResult(final Player player, final Exchange exchange) {
        this.player = player;
        this.exchange = exchange;
    }

    /**
     * Returns the reconstructed player.
     *
     * @return the reconstructed player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the reconstructed exchange.
     *
     * @return the reconstructed exchange
     */
    public Exchange getExchange() {
        return exchange;
    }
}
