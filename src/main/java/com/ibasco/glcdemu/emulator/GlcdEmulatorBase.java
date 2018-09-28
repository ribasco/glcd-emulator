package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Base class for emulators providing basic functionality
 *
 * @author Rafael Ibasco
 */
abstract public class GlcdEmulatorBase implements GlcdEmulator {

    private ObjectProperty<PixelBuffer> buffer = new SimpleObjectProperty<>();

    private ObjectProperty<GlcdBusInterface> busInterface = new SimpleObjectProperty<>();

    @Override
    abstract public void processByte(int data);

    @Override
    public final PixelBuffer getBuffer() {
        return buffer.get();
    }

    @Override
    public final void setBuffer(PixelBuffer buffer) {
        this.buffer.set(buffer);
    }

    @Override
    public final void setBusInterface(GlcdBusInterface busInterface) {
        this.busInterface.set(busInterface);
    }

    @Override
    public final GlcdBusInterface getBusInterface() {
        return this.busInterface.get();
    }

    @Override
    public ObjectProperty<PixelBuffer> bufferProperty() {
        return buffer;
    }

    @Override
    public ObjectProperty<GlcdBusInterface> busInterfaceProperty() {
        return busInterface;
    }
}
