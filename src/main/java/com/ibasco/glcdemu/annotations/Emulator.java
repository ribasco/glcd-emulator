package com.ibasco.glcdemu.annotations;

import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdControllerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Emulator {

    /**
     * The {@link GlcdControllerType} associated with this emulator
     */
    GlcdControllerType controller();

    /**
     * Brief description of the emulator implementation
     */
    String description();

    /**
     * Returns an array of supported bus interfaces by this emulator implementation
     *
     * @return An array of {@link GlcdBusInterface}
     */
    GlcdBusInterface[] bus();

    /**
     * Returns the default bus interface if nothing has been explicitly specified to the emulator instance
     */
    GlcdBusInterface defaultBus();
}
