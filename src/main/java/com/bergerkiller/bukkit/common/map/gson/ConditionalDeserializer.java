package com.bergerkiller.bukkit.common.map.gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ConditionalDeserializer implements JsonDeserializer<BlockModelState.Condition> {

    @Override
    public BlockModelState.Condition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        BlockModelState.Condition result = new BlockModelState.Condition();
        result.mode = BlockModelState.Condition.Mode.AND;
        result.conditions = new ArrayList<BlockModelState.Condition>(1);

        if (jsonElement.isJsonPrimitive()) {
            // Options stored in a single String token
            Map<String, String> options = new BlockRenderOptions(BlockData.AIR, jsonElement.getAsString());
            for (Map.Entry<String, String> option : options.entrySet()) {
                BlockModelState.Condition condition = new BlockModelState.Condition();
                condition.mode = BlockModelState.Condition.Mode.SELF;
                condition.conditions = Collections.emptyList();
                condition.key = option.getKey();
                condition.value = option.getValue();
                result.conditions.add(condition);
            }
        } else {
            // Handle operator types in the condition structure
            JsonObject obj = jsonElement.getAsJsonObject();
            for (Map.Entry<String,JsonElement> entry : obj.entrySet()) {
                // Start a new sub-condition, defaulting to mode SELF
                BlockModelState.Condition subCondition = new BlockModelState.Condition();
                subCondition.mode = BlockModelState.Condition.Mode.SELF;
                for (BlockModelState.Condition.Mode mode : BlockModelState.Condition.Mode.values()) {
                    if (entry.getKey().equals(mode.name())) {
                        subCondition.mode = mode;
                        break;
                    }
                }

                if (subCondition.mode == BlockModelState.Condition.Mode.SELF) {
                    // Self: store key:value pair
                    subCondition.conditions = Collections.emptyList();
                    subCondition.key = entry.getKey();
                    subCondition.value = entry.getValue().getAsString();
                } else {
                    // Create a sub-tree with this operator mode
                    if (entry.getValue().isJsonArray()) {
                        // Array of Object conditions
                        JsonArray condArr = entry.getValue().getAsJsonArray();
                        subCondition.conditions = new ArrayList<BlockModelState.Condition>(condArr.size());
                        for (JsonElement condElem : condArr) {
                            subCondition.conditions.add(deserialize(condElem, type, jsonDeserializationContext));
                        }
                    } else {
                        // Single Object condition
                        subCondition.conditions = Arrays.asList(deserialize(entry.getValue(), type, jsonDeserializationContext));
                    }
                }
                result.conditions.add(subCondition);
            }
        }

        // Simplify if only one element
        if (result.conditions.size() == 1) {
            return result.conditions.get(0);
        } else {
            return result;
        }
    }
    
}
