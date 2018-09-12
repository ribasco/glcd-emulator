package com.ibasco.glcdemu.utils.json.adapters;

import com.google.gson.*;
import com.ibasco.glcdemu.enums.SerialBaudRate;

import java.lang.reflect.Type;

public class SerialBaudRateAdapter implements JsonSerializer<SerialBaudRate>, JsonDeserializer<SerialBaudRate> {
    @Override
    public SerialBaudRate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return SerialBaudRate.fromValue(json.getAsInt());
    }

    @Override
    public JsonElement serialize(SerialBaudRate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toValue());
    }
}
