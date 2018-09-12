package com.ibasco.glcdemu.annotations;

import com.ibasco.pidisplay.drivers.glcd.enums.GlcdControllerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Emulator {
    GlcdControllerType controller();

    String description();
}
