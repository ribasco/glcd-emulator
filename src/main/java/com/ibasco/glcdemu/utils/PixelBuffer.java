package com.ibasco.glcdemu.utils;

import com.ibasco.glcdemu.exceptions.InvalidPixelDimensions;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * A generic display buffer interface which provides read/write access of pixel data. This class is NOT thread-safe.
 *
 * @author Rafael Ibasco
 */
public class PixelBuffer {

    private static final Logger log = LoggerFactory.getLogger(PixelBuffer.class);

    private byte buffer[][];

    private ReadOnlyIntegerWrapper width = new ReadOnlyIntegerWrapper();

    private ReadOnlyIntegerWrapper height = new ReadOnlyIntegerWrapper();

    private ReadOnlyBooleanWrapper invalidated = new ReadOnlyBooleanWrapper();

    private boolean fair = true;

    public PixelBuffer(byte[][] copy) {
        this.buffer = new byte[copy.length][copy[0].length];
        this.width.set(copy[0].length);
        this.height.set(copy.length);
        copyBuffer(copy, this.buffer);
    }

    /**
     * Create a new instance of {@link PixelBuffer} using the dimensions provided
     *
     * @param width
     *         The width of the display (in pixels)
     * @param height
     *         The height of the display (in pixels)
     */
    public PixelBuffer(int width, int height) {
        this(width, height, true);
    }

    /**
     * Create a new instance of {@link PixelBuffer} using the dimensions provided
     *
     * @param width
     *         The width of the display (in pixels)
     * @param height
     *         The height of the display (in pixels)
     * @param fair
     *         If true, no exceptions will be thrown if x or y coordinates exceed it's boundary limits.
     */
    public PixelBuffer(int width, int height, boolean fair) {
        checkDimensions(width, height);
        this.buffer = allocate(width, height);
        this.width.set(width);
        this.height.set(height);
        this.fair = fair;
    }

    public boolean isInvalidated() {
        return invalidated.get();
    }

    public ReadOnlyBooleanProperty invalidatedProperty() {
        return invalidated.getReadOnlyProperty();
    }

    /**
     * Write pixel data into the internal buffer
     *
     * @param x
     *         The x-coordinate of the pixel in the buffer
     * @param y
     *         The y-coordinate of the pixel in the buffer
     * @param state
     *         If value is > 0, pixel will be set to ON state, if <= 0 the state will be OFF
     */
    public void write(int x, int y, int state) {
        write(x, y, state > 0);
    }

    //TODO: Implement
    public void write(PixelBuffer buffer) {
        copyBuffer(buffer.getBuffer(), this.buffer);
    }

    /**
     * Writes an 8-bit value to the buffer. The cursor will automatically be incremented by 8.
     *
     * @param data
     *         A byte of data (8-bit) to be written
     */
    public void write(byte data) {
    }

    public void write(short data) {

    }

    /**
     * Convenience method to check if the buffer is empty (all zero bits)
     *
     * @return True if the buffer is empty
     */
    public boolean isEmpty() {
        for (byte[] row : buffer) {
            for (byte col : row) {
                if (col != 0)
                    return false;
            }
        }
        return true;
    }

    /**
     * Write pixel data into the internal buffer
     *
     * @param x
     *         The x-coordinate of the pixel in the buffer
     * @param y
     *         The y-coordinate of the pixel in the buffer
     * @param state
     *         True to set the pixel into ON state, otherwise False for OFF
     */
    public void write(int x, int y, boolean state) {
        Objects.requireNonNull(buffer);
        if (!checkBounds(x, y)) {
            if (fair)
                return;
            throw new IndexOutOfBoundsException(String.format("X or Y indices are out of bounds. (ACTUAL: x=%d, y=%d, MAX: x=%d, y=%d)", x, y, width.get() - 1, height.get() - 1));
        }
        int xpos = calculateXPos(x);
        if ((BitUtils.readBit(buffer[y][xpos], x) == 1) != state) {
            buffer[y][xpos] = BitUtils.writeBit(buffer[y][xpos], x, state);
            markInvalid();
        }
    }

    /**
     * Read the current state of the pixel from the buffer
     *
     * @param x
     *         The x-coordinate of the pixel in the buffer
     * @param y
     *         The y-coordinate of the pixel in the buffer
     *
     * @return The pixel state 1 = on, 0 = off. -1 if the the coordinates provided are out of bounds
     */
    public int read(int x, int y) {
        Objects.requireNonNull(buffer);
        if (!checkBounds(x, y)) {
            if (fair)
                return 0;
            throw new IndexOutOfBoundsException(String.format("X or Y indices are out of bounds. (ACTUAL: x=%d, y=%d, MAX: x=%d, y=%d)", x, y, width.get() - 1, height.get() - 1));
        }
        if (y > (buffer.length - 1))
            return -1;
        int bufferX = calculateXPos(x);
        if (bufferX > (buffer[y].length - 1)) {
            return -1;
        }
        invalidated.set(false);
        return BitUtils.readBit(buffer[y][bufferX], x);
    }

    /**
     * Resize the dimensions of the internal buffer (number of pixels)
     *
     * @param newWidth
     *         The new width of the display. Set to null for no-change
     * @param newHeight
     *         The new height of the display. Set to null for no-change
     */
    public void resize(Integer newWidth, Integer newHeight) {
        if (newWidth == null && newHeight == null)
            return;

        checkDimensions(newWidth, newHeight);

        int height = newHeight == null ? this.height.get() : newHeight;
        int width = newWidth == null ? this.width.get() : newWidth;

        //If same dimension, do not proceed
        if ((width == getWidth()) && (height == getHeight())) {
            return;
        }

        byte[][] tmp = allocate(width, height);
        copyBuffer(this.buffer, tmp);
        this.buffer = tmp;

        this.width.set(width);
        this.height.set(height);
        log.debug("Pixel buffer resized (Width: {}, Height: {})", width, height);
    }

    /**
     * Clears the buffer by setting each value to null
     */
    public void clear() {
        for (int y = 0; y < buffer.length; y++) {
            for (int x = 0; x < buffer[y].length; x++) {
                buffer[y][x] = 0;
            }
        }
    }

    /**
     * Utility method to copy a multi-dimensional buffer
     *
     * @param source
     *         The source array
     * @param dest
     *         The destination array
     */
    private void copyBuffer(byte[][] source, byte[][] dest) {
        for (int row = 0; row < dest.length; row++) {
            if (row > (source.length - 1))
                return;
            dest[row] = Arrays.copyOf(source[row], dest[row].length);
        }
    }

    /**
     * Returns the width of the buffer (number of pixels)
     *
     * @return The width of the buffer
     */
    public int getWidth() {
        return width.get();
    }

    /**
     * Read-only width property. Either create a new instance or call the {@link #resize(Integer, Integer)} method to update dimensions
     *
     * @return The buffer width read-only property
     */
    public ReadOnlyIntegerProperty widthProperty() {
        return width.getReadOnlyProperty();
    }

    /**
     * Returns the height of the buffer (number of pixels)
     *
     * @return The height of the buffer
     */
    public int getHeight() {
        return height.get();
    }

    /**
     * Read-only height property. Either create a new instance or call the {@link #resize(Integer, Integer)} method to update dimensions
     *
     * @return The buffer height read-only property
     */
    public ReadOnlyIntegerProperty heightProperty() {
        return height.getReadOnlyProperty();
    }

    /**
     * @return the internal buffer of this instance
     */
    public byte[][] getBuffer() {
        return this.buffer;
    }

    /**
     * Translates the x-coordinate of a pixel into an x-coordinate of the byte buffer
     *
     * @param x
     *         The x-coordinate of a pixel
     *
     * @return The byte buffer x-index
     */
    private int calculateXPos(int x) {
        return (int) Math.floor((double) x / 8);
    }

    /**
     * Checks if the specified indices are out of bounds
     *
     * @param x
     *         The x-coordinate of a pixel
     * @param y
     *         The y-coordinate of a pixel
     *
     * @throws IndexOutOfBoundsException
     *         when x or y coordinates are out of bounds
     */
    private boolean checkBounds(int x, int y) {
        if (x >= width.get() || y >= height.get()) {
            //throw new IndexOutOfBoundsException(String.format("X or Y indices are out of bounds. (ACTUAL: x=%d, y=%d, MAX: x=%d, y=%d)", x, y, width.get() - 1, height.get() - 1));
            return false;
        }
        return true;
    }

    /**
     * Verify if the width or height dimensions are valid.
     *
     * @param width
     *         The width (in pixels)
     * @param height
     *         The height (in pixels)
     */
    private void checkDimensions(Integer width, Integer height) {
        if (width != null && ((width % 8) != 0)) {
            throw new InvalidPixelDimensions(width, height);
        }
        if (height != null && ((height % 8) != 0))
            throw new InvalidPixelDimensions(width, height);
    }

    /**
     * Helper method to allocate a new buffer based on the display width and height
     *
     * @param width
     *         The display width (in pixels)
     * @param height
     *         The display height (in pixels)
     *
     * @return A multi-dimensional array
     */
    private byte[][] allocate(int width, int height) {
        checkDimensions(width, height);
        //e.g. if the display width is 128, then our buffer width would be 16 bytes (width / 8)
        int bufferWidth = width / 8;
        return new byte[height][bufferWidth];
    }

    private void markInvalid() {
        if (!invalidated.get()) {
            invalidated.set(true);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
