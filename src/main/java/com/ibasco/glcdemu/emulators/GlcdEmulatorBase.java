package com.ibasco.glcdemu.emulators;

import com.ibasco.glcdemu.utils.PixelBuffer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;

abstract public class GlcdEmulatorBase implements GlcdEmulator {

    private ObjectProperty<PixelBuffer> buffer = new SimpleObjectProperty<>();

    @Override
    abstract public void processByte(int data);

    @Override
    public PixelBuffer getBuffer() {
        return Objects.requireNonNull(buffer.get(), "Buffer has not yet been assigned");
    }

    @Override
    public ObjectProperty<PixelBuffer> bufferProperty() {
        return buffer;
    }

    @Override
    public void setBuffer(PixelBuffer buffer) {
        this.buffer.set(buffer);
    }
}
