package com.ibasco.glcdemu.utils.json.adapters;

import com.google.gson.*;
import com.ibasco.glcdemu.model.FontCacheEntry;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdFont;

import java.io.File;
import java.lang.reflect.Type;

public class FontCacheEntryAdapter implements JsonDeserializer<FontCacheEntry>, JsonSerializer<FontCacheEntry> {
    @Override
    public FontCacheEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        int ascent = obj.get("ascent").getAsInt();
        int descent = obj.get("descent").getAsInt();
        String fontKey = obj.get("font").getAsString();
        String imagePath = obj.get("image").getAsString();
        return new FontCacheEntry(ascent, descent, GlcdFont.valueOf(fontKey), new File(imagePath));
    }

    @Override
    public JsonElement serialize(FontCacheEntry src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("ascent", src.getAscent());
        obj.addProperty("descent", src.getDescent());
        obj.addProperty("font", src.getFont().name());
        obj.addProperty("image", src.getImage().getAbsolutePath());
        return obj;
    }
}
