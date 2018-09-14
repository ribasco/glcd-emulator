package com.ibasco.glcdemu.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class FpsCounter {

    private AtomicInteger counter = new AtomicInteger(0);

    private long previous = 0;

    private Consumer<Integer> countListener;

    private long interval;

    private TimeUnit timeUnit;

    private int lastCount;

    public FpsCounter() {
        this(1, TimeUnit.SECONDS);
    }

    public FpsCounter(long interval, TimeUnit timeUnit) {
        this.interval = timeUnit.toNanos(interval);
        this.timeUnit = timeUnit;
    }

    public int getLastCount() {
        return lastCount;
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
        pulse(TimeUnit.SECONDS.toNanos(1));
    }

    /**
     * Sends a pulse to regularly check on the current state
     *
     * @param now
     *         The pulse time in nanoseconds
     */
    public synchronized void pulse(long now) {
        if ((now - previous) >= interval) {
            lastCount = counter.getAndSet(0);
            if (countListener != null) {
                countListener.accept(lastCount);
            }
            previous = now;
        }
    }
}
