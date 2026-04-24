package no.ntnu.idatt2003.model.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract subject for the Observer design pattern.
 * Subclasses represent the observable model and call
 * {@link #notifyObservers()} whenever their state changes.
 */
public abstract class Subject {

  /** Registered observers. */
  private final List<Observer> observerList = new ArrayList<>();

  /**
   * Registers an observer to receive state-change notifications.
   *
   * @param observer the observer to add
   */
  public void attach(final Observer observer) {
    if (observer != null && !observerList.contains(observer)) {
      observerList.add(observer);
    }
  }

  /**
   * Removes a previously registered observer.
   *
   * @param observer the observer to remove
   */
  public void detach(final Observer observer) {
    observerList.remove(observer);
  }

  /**
   * Notifies all registered observers that the state has changed.
   */
  public void notifyObservers() {
    for (Observer observer : observerList) {
      observer.update();
    }
  }
}
