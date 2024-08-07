package com.bergerkiller.bukkit.common.map.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class EmptyMapSerializer implements JsonSerializer<Map<?, ?>> {

    @Override
    public JsonElement serialize(Map<?, ?> o, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonObject();
    }
}
