package com.ibasco.glcdemu.utils.json.adapters;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class ClassTypeAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClassTypeAdapter.class);

    @Override
    public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String className = json.getAsString();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Could not deserialize class : {}", className);
        }
        return null;
    }

    @Override
    public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName());
    }
}
