package com.ibasco.glcdemu.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class FpsCounter {

    private AtomicInteger counter = new AtomicInteger(0);

    private long previous = 0;

    private Consumer<Integer> countListener;

    public void setCountListener(Consumer<Integer> listener) {
        this.countListener = listener;
    }

    public void count() {
        counter.incrementAndGet();
    }

    public void pulse() {
        long current = System.currentTimeMillis();
        if ((current - previous) >= 1000) {
            if (countListener != null)
                countListener.accept(counter.getAndSet(0));
            previous = current;
        }
    }
}
