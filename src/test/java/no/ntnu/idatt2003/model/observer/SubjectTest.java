package no.ntnu.idatt2003.model.observer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubjectTest {

    private static class ConcreteSubject extends Subject {
        void trigger() {
            notifyObservers();
        }
    }

    private ConcreteSubject subject;

    @BeforeEach
    void setUp() {
        subject = new ConcreteSubject();
    }

    @Test
    void testAttachAndNotify() {
        AtomicInteger count = new AtomicInteger(0);
        subject.attach(count::incrementAndGet);
        subject.trigger();
        assertEquals(1, count.get());
    }

    @Test
    void testAttachNull() {
        assertDoesNotThrow(() -> subject.attach(null));
        assertDoesNotThrow(() -> subject.trigger());
    }

    @Test
    void testAttachDuplicate() {
        AtomicInteger count = new AtomicInteger(0);
        Observer observer = count::incrementAndGet;
        subject.attach(observer);
        subject.attach(observer);
        subject.trigger();
        assertEquals(1, count.get());
    }

    @Test
    void testDetach() {
        AtomicInteger count = new AtomicInteger(0);
        Observer observer = count::incrementAndGet;
        subject.attach(observer);
        subject.detach(observer);
        subject.trigger();
        assertEquals(0, count.get());
    }
}
