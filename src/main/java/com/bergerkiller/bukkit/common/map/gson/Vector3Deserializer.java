package com.bergerkiller.bukkit.common.map.gson;

import java.lang.reflect.Type;

import com.bergerkiller.bukkit.common.math.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Vector3Deserializer implements JsonDeserializer<Vector3> {
    @Override
    public Vector3 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray arr = jsonElement.getAsJsonArray();
        return new Vector3(
            arr.get(0).getAsDouble(),
            arr.get(1).getAsDouble(),
            arr.get(2).getAsDouble()
        );
    }
}