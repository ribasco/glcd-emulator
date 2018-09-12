package com.ibasco.glcdemu.utils.json.adapters;

import com.google.gson.*;
import com.ibasco.glcdemu.utils.NodeUtil;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;

public class ColorTypeAdapter implements JsonDeserializer<Color>, JsonSerializer<Color> {
    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String str = json.getAsString();
        return Color.web(str);
    }

    @Override
    public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(NodeUtil.toHexString(src));
    }
}
