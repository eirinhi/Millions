package no.ntnu.idatt2003.model.observer;

/**
 * Observer interface for the Observer design pattern.
 * Classes that want to be notified of state changes in a {@link Subject}
 * must implement this interface.
 */
public interface Observer {

  /**
   * Called by the subject when its state has changed.
   * The observer should pull updated state from the subject.
   */
  void update();
}
