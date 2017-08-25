package com.bergerkiller.bukkit.common.map.gson;

import java.lang.reflect.Type;

import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class VariantListDeserializer implements JsonDeserializer<BlockModelState.VariantList> {

    @Override
    public BlockModelState.VariantList deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        BlockModelState.VariantList list = new BlockModelState.VariantList();
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (JsonElement arrayElement : array) {
                list.add(jsonDeserializationContext.deserialize(arrayElement, BlockModelState.Variant.class));
            }
        } else {
            list.add(jsonDeserializationContext.deserialize(jsonElement, BlockModelState.Variant.class));
        }
        return list;
    }

}
