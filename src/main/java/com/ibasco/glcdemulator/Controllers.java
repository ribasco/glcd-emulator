/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: Controllers.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Luis Ibasco
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
package com.ibasco.glcdemulator;

import com.ibasco.glcdemulator.controllers.*;
import com.ibasco.glcdemulator.exceptions.ControllerLoadException;
import com.ibasco.glcdemulator.model.FontCacheDetails;
import com.ibasco.glcdemulator.services.FontCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for all available JavaFX Controllers
 *
 * @author Rafael Ibasco
 */
public class Controllers {

    private static final Map<Class<?>, Controller> controllerMap = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(Controllers.class);

    public static <T extends Controller> T getController(Class<?> key, Object... args) {
        if (args == null || args.length == 0) {
            return getController(key, null, (Object) null);
        }
        return getController(key, getParamsFromArgs(args), args);
    }

    private static Class<?>[] getParamsFromArgs(Object[] args) {
        if (args == null || args.length == 0)
            throw new IllegalArgumentException("Arguments must not be null or empty");
        Class<?>[] params = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            params[i] = arg.getClass();
        }
        return params;
    }

    /**
     * Retrieves the controller from the cache. If it does not yet exists, then a new instance will be created.
     *
     * @param key
     *         The controller class
     * @param <T>
     *         The type extending {@link Controller}
     *
     * @return The cached controller instance
     */
    public static <T extends Controller> T getController(Class<?> key, Class<?>[] params, Object... args) {
        Controller controller = controllerMap.computeIfAbsent(key, aClass -> {
            try {
                T ctrl;
                if (params != null && (args != null && args.length != 0)) {
                    if (params.length != args.length)
                        throw new IllegalArgumentException("Parameter count does not match the argument count");

                    //noinspection unchecked
                    ctrl = (T) aClass.getConstructor(params).newInstance(args);
                } else {
                    //noinspection unchecked
                    ctrl = (T) aClass.newInstance();
                }
                log.debug("Computing new instance for controller {} = {}", key, ctrl);
                return ctrl;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new ControllerLoadException(e);
            }
        });
        //noinspection unchecked
        return (T) controller;
    }

    public static GlcdDeveloperController getDeveloperController() {
        return getController(GlcdDeveloperController.class);
    }

    public static GlcdEmulatorController getEmulatorController() {
        return getController(GlcdEmulatorController.class);
    }

    public static GlcdEditProfileController getEditProfileController() {
        return getController(GlcdEditProfileController.class);
    }

    public static GlcdFontBrowserController getFontBrowserController() {
        return getController(GlcdFontBrowserController.class, new FontCacheService(), new FontCacheDetails());
    }

    public static GlcdAboutController getAboutController() {
        return getController(GlcdAboutController.class);
    }
}
