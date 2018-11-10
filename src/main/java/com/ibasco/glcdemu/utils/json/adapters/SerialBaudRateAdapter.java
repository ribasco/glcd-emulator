/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: SerialBaudRateAdapter.java
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
