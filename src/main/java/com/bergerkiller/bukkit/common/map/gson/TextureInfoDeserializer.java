package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.map.util.Model;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Deserializes the TextureInfo. Usually just parses a String as a sprite. Newer versions of the game
 * also support additional properties, with the value being a json object.
 */
class TextureInfoDeserializer implements JsonDeserializer<Model.TextureInfo> {
    @Override
    public Model.TextureInfo deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            String sprite = obj.get("sprite").getAsString();
            Model.TextureInfo textureInfo = Model.TextureInfo.of(sprite);
            textureInfo.forceTranslucent = obj.get("force_translucent") != null && obj.get("force_translucent").getAsBoolean();
            return textureInfo;
        } else {
            return Model.TextureInfo.of(jsonElement.getAsString());
        }
    }
}
