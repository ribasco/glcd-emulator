/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: GlcdDisplayAdapter.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
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
package com.ibasco.glcdemu.utils.json.adapters;

import com.google.gson.*;
import com.ibasco.glcdemu.utils.GlcdUtil;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;

public class GlcdDisplayAdapter implements JsonSerializer<GlcdDisplay>, JsonDeserializer<GlcdDisplay> {

    private static final Logger log = LoggerFactory.getLogger(GlcdDisplayAdapter.class);

    @Override
    public GlcdDisplay deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String key = jsonElement.getAsString();
        if (StringUtils.isBlank(key) || !key.contains(":")) {
            log.warn("Invalid display key in configuration");
            return null;
        }
        String[] tokens = StringUtils.splitPreserveAllTokens(key, ":");
        GlcdControllerType controllerType = GlcdControllerType.valueOf(tokens[0]);
        String displayName = tokens[1];
        List<GlcdDisplay> res = GlcdUtil.findDisplay(p -> p.getController().equals(controllerType) && p.getName().equalsIgnoreCase(displayName));
        return res != null && !res.isEmpty() ? res.get(0) : null;
    }

    @Override
    public JsonElement serialize(GlcdDisplay display, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(String.format("%s:%s", display.getController().name(), display.getName()));
    }
}
