/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: FontCacheEntryAdapter.java
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
package com.ibasco.glcdemulator.utils.json.adapters;

import com.google.gson.*;
import com.ibasco.glcdemulator.model.FontCacheEntry;
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
