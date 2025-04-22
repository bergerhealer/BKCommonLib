package com.bergerkiller.bukkit.common.map.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Deserializes lists but excludes null values in those lists.
 * Mojang never uses null values in arrays, so it's safest to get rid of those.
 *
 * @param <T>
 */
class NonNullListDeserializer<T> implements JsonDeserializer<List<T>> {

    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Type valueType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];

        JsonArray input = json.getAsJsonArray();
        ArrayList<T> result = new ArrayList<>(input.size());
        for (JsonElement el : input) {
            if (!el.isJsonNull()) {
                result.add(context.deserialize(el, valueType));
            }
        }
        return result;
    }
}
