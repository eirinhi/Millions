package no.ntnu.idatt2003.view;

import java.net.URL;
import javafx.scene.media.AudioClip;

/**
 * Plays sound effects used throughout the application.
 */
public final class SoundPlayer {

    /** Click sound for button and table interactions. */
    private static final AudioClip CLICK =
        load("/no/ntnu/idatt2003/sounds/click.wav");

    /** "Kaching"-sound played when a transaction is completed. */
    private static final AudioClip KACHING =
        load("/no/ntnu/idatt2003/sounds/kaching.wav");

    /** Sound played when the player ends the game. */
    private static final AudioClip END_GAME =
        load("/no/ntnu/idatt2003/sounds/end_game.wav");

    /** Utility class which is not instantiable. */
    private SoundPlayer() { }

    /**
     * Plays a click sound for button and table interactions.
     */
    public static void playClick() {
        if (CLICK != null) {
            CLICK.play();
        }
    }

    /**
     * Plays a "kaching" sound when a transaction is completed.
     */
    public static void playKaching() {
        if (KACHING != null) {
            KACHING.play();
        }
    }

    /**
     * Plays a sound when the player ends the game.
     */
    public static void playEndGame() {
        if (END_GAME != null) {
            END_GAME.play();
        }
    }

    /**
     * Loads an audio clip from the given resource path.
     *
     * @param resource the absolute resource path to the audio file
     * @return the loaded clip, or null if loading failed
     */
    private static AudioClip load(final String resource) {
        try {
            URL url = SoundPlayer.class.getResource(resource);
            return url != null ? new AudioClip(url.toExternalForm()) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
