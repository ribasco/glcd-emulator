package com.ibasco.glcdemu.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibasco.glcdemu.enums.SerialBaudRate;
import com.ibasco.glcdemu.model.FontCacheEntry;
import com.ibasco.glcdemu.utils.json.adapters.*;
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
        builder = FxGson.createWithExtras().newBuilder();
        builder.setPrettyPrinting();
        //builder.registerTypeAdapter(Color.class, new ColorTypeAdapter());
        builder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter());
        builder.registerTypeAdapter(FontCacheEntry.class, new FontCacheEntryAdapter());
        builder.registerTypeAdapter(SerialBaudRate.class, new SerialBaudRateAdapter());
        builder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
        builder.registerTypeAdapter(GlcdDisplay.class, new GlcdDisplayAdapter());
        refreshGson();
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
