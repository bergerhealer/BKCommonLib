package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Deserializes the major (and optional minor) version into a PackVersion.
 * Used for packs since 1.21.9.
 */
class PackVersionDeserializer implements JsonDeserializer<MapResourcePack.PackVersion> {
    @Override
    public MapResourcePack.PackVersion deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            if (array.size() == 1) {
                return MapResourcePack.PackVersion.of(array.get(0).getAsInt());
            } else {
                return MapResourcePack.PackVersion.of(array.get(0).getAsInt(), array.get(1).getAsInt());
            }
        } else {
            return MapResourcePack.PackVersion.of(jsonElement.getAsInt());
        }
    }
}
