/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: Counter.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 - 2019 Rafael Luis Ibasco
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * =========================END==================================
 */
package com.ibasco.glcdemulator.utils;

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

    public int getCurrentCount() {
        return counter.get();
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
