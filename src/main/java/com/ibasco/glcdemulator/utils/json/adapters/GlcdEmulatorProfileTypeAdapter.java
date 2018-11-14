/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdEmulatorProfileTypeAdapter.java
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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.model.GlcdEmulatorProfile;

import java.lang.reflect.Type;

public class GlcdEmulatorProfileTypeAdapter  implements /*JsonDeserializer<GlcdEmulatorProfile>,*/ JsonSerializer<GlcdEmulatorProfile> {
    /*@Override
    public GlcdEmulatorProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonPrimitive primitive = json.getAsJsonPrimitive();
        int profileId = primitive.getAsInt();
        GlcdEmulatorProfile profile = Context.getInstance().getProfileManager().getProfileFromFS(profileId);
        return profile != null ? profile : new GlcdEmulatorProfile();
    }
*/
    @Override
    public JsonElement serialize(GlcdEmulatorProfile src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.getId() == -1) {
            src.setId(Context.getInstance().getAppConfig().nextProfileId());
        }
        return new JsonPrimitive(src.getId());
    }
}
