package com.bergerkiller.bukkit.common.map.gson;

import java.lang.reflect.Type;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class VariantListDeserializer implements JsonDeserializer<BlockModelState.VariantList> {

    @Override
    public BlockModelState.VariantList deserialize(JsonElement jsonElement, Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        BlockModelState.VariantList list = new BlockModelState.VariantList();
        if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (JsonElement arrayElement : array) {
                list.add((BlockModelState.Variant) jsonDeserializationContext.deserialize(arrayElement, BlockModelState.Variant.class));
            }
        } else {
            list.add((BlockModelState.Variant) jsonDeserializationContext.deserialize(jsonElement, BlockModelState.Variant.class));
        }

        // On legacy, make sure the model name is an absolute path
        if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            for (BlockModelState.Variant variant : list) {
                if (!variant.modelName.startsWith("block/")) {
                    variant.modelName = "block/" + variant.modelName;
                }
            }
        }

        return list;
    }

}
