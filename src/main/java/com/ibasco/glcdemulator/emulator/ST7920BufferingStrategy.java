package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.utils.PixelBuffer;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("Duplicates")
public class ST7920BufferingStrategy extends BufferStrategyBase {

    private final AtomicInteger dataCtr = new AtomicInteger(0);

    private short _data = 0;

    private int yAddress = 0;

    private int xAddress = 0;

    @Override
    public void processByte(byte data) {
        //Note: For one address, two succeeding bytes are received (16 bits)
        //first byte (high nibble)
        if (dataCtr.getAndUpdate(this::toggle) == 0) { //dataCtr == 0
            _data = (short) ((data & 0xff) << 8);
        }
        //second byte (low nibble)
        else {
            _data |= data & 0xff;
            try {
                //Process 2 bytes of data at a time then iterate through each bit starting
                // from the most significant bit. Flush to pixel buffer
                flush(_data);
            } finally {
                _data = 0;
            }
            //increment x-address after receving the second byte
            xAddress = ++xAddress & 0xf;
        }
    }

    /**
     * This will process 16-bit of data and flushes it to the pixel buffer
     *
     * @param data
     *         A 16-bit value to be flushed to the display buffer
     */
    private void flush(short data) {
        int width = getBuffer().getWidth();
        int mask = width - 1;
        int offset = getBuffer().getHeight() / 2; //this would be our overflow offset

        PixelBuffer buffer = getBuffer();

        for (int pos = 15; pos >= 0; pos--) {
            int x = (15 - pos) + (xAddress * 16); //calculate x-pixel coordinate
            int y = yAddress; //y-pixel coordinate (as is)
            boolean value = (data & (1 << pos)) != 0; //read nth bit

            //re-adjust x and y coordinates when overflow occurs
            if (x >= width) {
                x &= mask; //apply mask to limit range between 0 and (width -1)
                y += offset; //increment y with the overflow offset
                if (y > (buffer.getHeight() - 1)) {
                    throw new IllegalStateException(String.format("Y-coordinate greater than the maximum display height (actual: %d, max: %d)", y, getBuffer().getHeight() - 1));
                }
            }

            //Write to pixel buffer
            buffer.write(x, y, value);
        }
    }

    @Override
    public void reset() {
        dataCtr.set(0);
        _data = 0;
        xAddress = 0;
        yAddress = 0;
    }

    private int toggle(int prev) {
        return ++prev & 0x1;
    }
}
