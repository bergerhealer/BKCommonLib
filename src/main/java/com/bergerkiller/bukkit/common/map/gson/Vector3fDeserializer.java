package com.bergerkiller.bukkit.common.map.gson;

import java.lang.reflect.Type;

import com.bergerkiller.bukkit.common.map.util.Vector3f;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Vector3fDeserializer implements JsonDeserializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray arr = jsonElement.getAsJsonArray();
        return new Vector3f(
            arr.get(0).getAsFloat(),
            arr.get(1).getAsFloat(),
            arr.get(2).getAsFloat()
        );
    }
}