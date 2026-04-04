package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Common utilities for deserializing information from/to Map&lt;String, Object&gt; structures.
 * Used by the ItemStack and ItemMeta migrators (Bukkit).
 */
public class ItemStackDeserializerUtils {

    // >= 1.18.1
    public static Object deserializeSkullOwner(Map<String, Object> values) {
        return CraftItemStackHandle.T.deserializeSkullOwner.invoke(values);
    }

    // >= 1.21.4
    public static Object deserializeCustomModelData(Map<String, Object> values) {
        convertNumberListToFloatInMap(values, "floats");
        replaceListOfMapsInMap(values, "colors", ItemStackDeserializerItemMetaMigrator::deserializeColor);
        return CraftItemStackHandle.T.deserializeCustomModelData.invoke(values);
    }

    protected static ConfigurationSerializable deserializeFireworkEffect(Map<String, Object> values) {
        replaceListOfMapsInMap(values, "colors", ItemStackDeserializerItemMetaMigrator::deserializeColor);
        replaceListOfMapsInMap(values, "fade-colors", ItemStackDeserializerItemMetaMigrator::deserializeColor);
        return org.bukkit.FireworkEffect.deserialize(values);
    }

    protected static org.bukkit.Color deserializeColor(java.util.Map<String, Object> values) {
        convertNumberToIntegerInMapValues(values);
        return org.bukkit.Color.deserialize(values);
    }

    @SuppressWarnings("unchecked")
    protected static void replaceListOfMapsInMap(java.util.Map<String, Object> map, String key, Function<Map<String, Object>, ?> mapper) {
        Object value = map.get(key);
        if (value instanceof java.util.List) {
            LogicUtil.mapListItems((java.util.List<Object>) value, o -> {
                if (o instanceof java.util.Map) {
                    return mapper.apply((java.util.Map<String, Object>) o);
                } else {
                    return o;
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    protected static void replaceMapInMap(java.util.Map<String, Object> map, String key, Function<java.util.Map<String, Object>, ?> mapper) {
        map.computeIfPresent(key, (k, value) -> {
            if (value instanceof java.util.Map) {
                return mapper.apply((Map<String, Object>) value);
            } else {
                return value;
            }
        });
    }

    @SuppressWarnings("UnnecessaryBoxing")
    protected static Object convertNumberToInteger(Object key, Object value) {
        if (value instanceof Number && !(value instanceof Integer)) {
            return Integer.valueOf(((Number) value).intValue());
        } else {
            return value;
        }
    }

    protected static void convertNumberToIntegerInMapValues(java.util.Map<String, Object> map, String key) {
        Object mapAtKey = map.get(key);
        if (mapAtKey instanceof java.util.Map) {
            convertNumberToIntegerInMapValues((java.util.Map<?, ?>) mapAtKey);
        }
    }

    @SuppressWarnings({"unchecked"})
    protected static void convertNumberToIntegerInMap(Map<?, ?> map, Object key) {
        ((Map<Object, Object>) map).computeIfPresent(key, ItemStackDeserializerMigrator::convertNumberToInteger);
    }

    @SuppressWarnings({"unchecked"})
    protected static void convertNumberToIntegerInMapValues(Map<?, ?> map) {
        LogicUtil.mapMapValues((Map<Object, Object>) map, ItemStackDeserializerMigrator::convertNumberToInteger);
    }

    @SuppressWarnings({"unchecked"})
    protected static void convertNumberListToFloatInMap(Map<?, ?> map, String key) {
        Object atKey = map.get(key);
        if (atKey instanceof List) {
            List<?> raw = (List<?>) atKey;
            int size = raw.size();
            for (int i = 0; i < size; i++) {
                Object rawItem = raw.get(i);
                if (rawItem instanceof Float) {
                    continue;
                }

                List<Object> newList = new ArrayList<>(raw);
                while (i < size) {
                    rawItem = newList.get(i);
                    if (!(rawItem instanceof Float) && rawItem instanceof Number) {
                        newList.set(i, ((Number) rawItem).floatValue());
                    }
                    i++;
                }
                ((Map<Object, Object>) map).put(key, newList);
                return;
            }
        }
    }

    protected static <T> List<T> parseList(Object data, String key, Function<Object, T> parser) {
        if (!(data instanceof Map)) {
            return Collections.emptyList();
        }
        Object atKey = ((Map<?, ?>) data).get(key);
        if (!(atKey instanceof List)) {
            return Collections.emptyList();
        }
        List<?> raw = (List<?>) atKey;
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(raw.size());
        for (Object rawValue : raw) {
            result.add(parser.apply(rawValue));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected static Optional<Map<String, Object>> readMap(Object value) {
        if (value instanceof Map) {
            return Optional.of((Map<String, Object>) value);
        } else {
            return Optional.empty();
        }
    }

    protected static Optional<String> readString(Object value) {
        if (value instanceof String) {
            return Optional.of((String) value);
        } else if (value != null) {
            return Optional.of(value.toString());
        } else {
            return Optional.empty();
        }
    }

    protected static Optional<Integer> readInteger(Object value) {
        if (value instanceof Integer) {
            return Optional.of((Integer) value);
        } else if (value instanceof Number) {
            return Optional.of(Integer.valueOf(((Number) value).intValue()));
        } else {
            return Optional.empty();
        }
    }
}
