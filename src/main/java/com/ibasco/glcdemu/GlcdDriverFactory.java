package com.ibasco.glcdemu;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.emulator.GlcdEmulatorFactory;
import com.ibasco.glcdemu.utils.GlcdUtil;
import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.pidisplay.drivers.glcd.*;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdControllerType;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdRotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

/**
 * Factory class for constructing glcd drivers (virtual/non-virtual)
 *
 * @author Rafael Ibasco
 */
public class GlcdDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(GlcdDriverFactory.class);

    /**
     * Creates a virtual driver with the specified bus interface
     *
     * @param display
     *         The display setup where the virtual driver will be based on.
     * @param busInterface
     *         The bus interface that will currently be used by the virtual driver. If null, the default bus interface
     *         will be selected.
     *
     * @return The virtual {@link GlcdDriver} instance
     *
     * @see Glcd
     */
    public static GlcdDriver createVirtual(GlcdDisplay display, GlcdBusInterface busInterface) {
        GlcdEmulator emulator = GlcdEmulatorFactory.createFrom(display, busInterface);
        return createVirtual(display, emulator.getBusInterface(), emulator);
    }

    public static GlcdDriver createVirtual(GlcdDisplay display, GlcdBusInterface busInterface, PixelBuffer outputBuffer) {
        GlcdEmulator emulator = GlcdEmulatorFactory.createFrom(display, busInterface, outputBuffer);
        return createVirtual(display, busInterface, emulator);
    }

    public static GlcdDriver createVirtual(GlcdControllerType controller, GlcdBusInterface busInterface, PixelBuffer outputBuffer) {
        Predicate<GlcdDisplay> filterDimensions = p -> (p.getDisplaySize().getDisplayWidth() >= outputBuffer.getWidth()) &&
                (p.getDisplaySize().getDisplayHeight() >= outputBuffer.getHeight());
        Predicate<GlcdDisplay> filterBusInterface = p -> p.hasBusInterface(busInterface);
        List<GlcdDisplay> result = GlcdUtil.findDisplayByType(controller, filterDimensions.and(filterBusInterface));
        GlcdDisplay display = null;

        //select first entry
        if (result.size() >= 1) {
            display = result.get(0);
        }

        if (display == null)
            throw new IllegalStateException("No display found from the specified criteria");

        GlcdEmulator emulator = GlcdEmulatorFactory.createFrom(display, busInterface, outputBuffer);

        return createVirtual(display, busInterface, emulator);
    }

    public static GlcdDriver createVirtual(GlcdDisplay display, GlcdBusInterface busInterface, GlcdDriverEventHandler handler) {

        if (busInterface == null)
            throw new IllegalStateException("Bus interface not specified");

        if (handler == null)
            throw new IllegalStateException("No data processor assigned for display '" + display.getName() + "'");

        GlcdConfig config = GlcdConfigBuilder
                .create()
                .display(display)
                .rotation(GlcdRotation.ROTATION_NONE)
                .busInterface(busInterface)
                .build();

        return new GlcdDriver(config, true, handler);
    }
}
