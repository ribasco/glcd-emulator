/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: JsonUtils.java
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibasco.glcdemulator.enums.SerialBaudRate;
import com.ibasco.glcdemulator.exceptions.StaticInitializationException;
import com.ibasco.glcdemulator.model.FontCacheEntry;
import com.ibasco.glcdemulator.utils.json.adapters.*;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import org.hildan.fxgson.FxGson;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

/**
 * Utility methods for creating/reading JSON. Special type adapters and strategies are used.
 *
 * @author Rafael Ibasco
 */
public class JsonUtils {

    private static final GsonBuilder builder;

    private static Gson gson;

    static {
        try {
            builder = FxGson.createWithExtras().newBuilder();
            builder.setPrettyPrinting();
            //builder.registerTypeAdapter(Color.class, new ColorTypeAdapter());
            builder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter());
            builder.registerTypeAdapter(FontCacheEntry.class, new FontCacheEntryAdapter());
            builder.registerTypeAdapter(SerialBaudRate.class, new SerialBaudRateAdapter());
            builder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
            builder.registerTypeAdapter(GlcdDisplay.class, new GlcdDisplayAdapter());
            refreshGson();
        } catch (Exception e) {
            throw new StaticInitializationException(e);
        }
    }

    public static void refreshGson() {
        gson = builder.create();
    }

    public static GsonBuilder getBuilder() {
        return builder;
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }
}
