package com.bergerkiller.bukkit.common.config;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Helper class to make GSON accessible from outside BKCommonLib.
 * Can serialize and deserialize various objects from/to a JSON String.
 */
public class JsonSerializer {
    private final com.google.gson.Gson gson = new com.google.gson.Gson();

    public ItemStack fromJsonToItemStack(String json) throws JsonSyntaxException {
        return ItemStackDeserializer.INSTANCE.apply(jsonToMap(json));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> jsonToMap(String json) throws JsonSyntaxException {
        return fromJson(json, Map.class);
    }

    public <T> T fromJson(String json, Class<T> type) throws JsonSyntaxException {
        try {
            return this.gson.fromJson(json, type);
        } catch (com.google.gson.JsonSyntaxException ex) {
            throw new JsonSyntaxException(ex.getMessage());
        }
    }

    public String itemStackToJson(ItemStack item) {
        return mapToJson(LogicUtil.serializeDeep(item));
    }

    public String mapToJson(Map<String, Object> map) {
        return this.gson.toJson(map);
    }

    public String toJson(Object value) {
        return this.gson.toJson(value);
    }

    /**
     * Exception thrown when the input json String is of an invalid format
     */
    public static class JsonSyntaxException extends Exception {
        private static final long serialVersionUID = -626531316069912407L;

        public JsonSyntaxException(String message) {
            super(message);
        }
    }
}
