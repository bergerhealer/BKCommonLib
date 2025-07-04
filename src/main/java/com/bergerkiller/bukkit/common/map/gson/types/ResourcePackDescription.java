package com.bergerkiller.bukkit.common.map.gson.types;

import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Custom marker type so that we can register our own deserializer for it.
 * Mojang also allows an array of components to make up the description, so that
 * must be handled.
 */
public class ResourcePackDescription {
    public final String plainContent;

    public ResourcePackDescription(String plainContent) {
        this.plainContent = plainContent;
    }

    public static class Deserializer implements JsonDeserializer<ResourcePackDescription> {
        private String deserializePart(JsonElement jsonElement) {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement el = jsonObject.get("text");
                return (el == null) ? "" : el.getAsString();
            } else {
                return jsonElement.getAsString();
            }
        }

        @Override
        public ResourcePackDescription deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < jsonArray.size(); i++) {
                    str.append(deserializePart(jsonArray.get(i)));
                }
                return new ResourcePackDescription(str.toString());
            } else {
                return new ResourcePackDescription(deserializePart(jsonElement));
            }
        }
    }
}