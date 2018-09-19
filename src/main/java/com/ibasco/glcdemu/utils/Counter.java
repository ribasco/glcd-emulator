package com.ibasco.glcdemu.utils;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Counter {

    private AtomicInteger counter = new AtomicInteger(0);

    private long previous = 0;

    private Consumer<Integer> countListener;

    private long interval;

    private TimeUnit timeUnit;

    private ReadOnlyIntegerWrapper lastCount = new ReadOnlyIntegerWrapper(0);

    public Counter() {
        this(1, TimeUnit.SECONDS);
    }

    public Counter(long interval, TimeUnit timeUnit) {
        this.interval = timeUnit.toNanos(interval);
        this.timeUnit = timeUnit;
    }

    public int getLastCount() {
        return lastCount.get();
    }

    public ReadOnlyIntegerProperty lastCountProperty() {
        return lastCount.getReadOnlyProperty();
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setListener(Consumer<Integer> listener) {
        this.countListener = listener;
    }

    public void count() {
        counter.incrementAndGet();
    }

    public void pulse() {
        pulse(TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()));
    }

    public void reset() {
        Platform.runLater(() -> lastCount.set(0));
    }

    /**
     * Sends a pulse to regularly check on the current state
     *
     * @param now
     *         The pulse time in nanoseconds
     */
    public synchronized void pulse(long now) {
        if ((now - previous) >= interval) {
            int count = counter.getAndSet(0);
            Platform.runLater(() -> lastCount.set(count));
            if (countListener != null) {
                countListener.accept(getLastCount());
            }
            previous = now;
        }
    }
}