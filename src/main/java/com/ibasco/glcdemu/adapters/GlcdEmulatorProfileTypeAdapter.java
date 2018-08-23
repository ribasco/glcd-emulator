package com.ibasco.glcdemu.adapters;

import com.google.gson.*;
import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.model.GlcdEmulatorProfile;

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
