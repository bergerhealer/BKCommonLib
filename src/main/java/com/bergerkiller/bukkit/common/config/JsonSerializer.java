package com.bergerkiller.bukkit.common.config;

import java.util.Collections;
import java.util.Map;

import com.bergerkiller.bukkit.common.config.yaml.YamlDeserializer;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializer;
import com.bergerkiller.bukkit.common.map.gson.EmptyMapSerializer;
import com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Helper class to make GSON accessible from outside BKCommonLib.
 * Can serialize and deserialize various objects from/to a JSON String.
 */
public class JsonSerializer {
    private final com.google.gson.Gson gson = new GsonBuilder().disableHtmlEscaping()
            .registerTypeAdapter(Collections.emptyMap().getClass(), new EmptyMapSerializer())
            .create();

    public ItemStack fromJsonToItemStack(String json) throws JsonSyntaxException {
        if ("null".equals(json)) {
            return null;
        }

        Map<String, Object> mapping = jsonToMap(json);
        Object result = YamlDeserializer.INSTANCE.deserializeMapping(mapping);
        if (result instanceof Map) {
            return ItemStackDeserializer.INSTANCE.apply((Map<String, Object>) result);
        } else if (result instanceof ItemStack) {
            return (ItemStack) result;
        } else {
            throw new JsonSyntaxException("Deserialized type is not an ItemStack or mapping: " + result);
        }
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
        if (item == null) {
            return "null";
        }

        Map<String, Object> map = LogicUtil.serializeDeep(item);
        map.remove("==");
        return mapToJson(map);
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
