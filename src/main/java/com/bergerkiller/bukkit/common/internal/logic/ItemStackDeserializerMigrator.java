package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Data-versioned data migrator for the before-deserialization Map format
 */
public abstract class ItemStackDeserializerMigrator {
    private final int curr_version;
    private int max_version;
    private final List<Entry> entries = new ArrayList<>();

    public ItemStackDeserializerMigrator() {
        curr_version = CraftMagicNumbersHandle.getDataVersion();
        max_version = curr_version;
    }

    /**
     * Gets the current data version of the server
     *
     * @return Current data version
     */
    public int getCurrentDataVersion() {
        return curr_version;
    }

    /**
     * Gets the maximum supported data version of this migrator
     *
     * @return Maximum data version
     */
    public int getMaximumDataVersion() {
        return max_version;
    }

    /**
     * Sets the maximum data version supported by this migrator. Newer than this and it cannot
     * process it.
     *
     * @param max Max data version supported
     */
    public void setMaximumDataVersion(int max) {
        this.max_version = Math.max(this.max_version, max);
    }

    // Registers a converter if it can convert from a future data version only
    // Current or older data versions are already natively supported by the server
    public void register(int data_version, ConverterFunction converter) {
        if (data_version <= this.curr_version && !this.entries.isEmpty()) {
            this.entries.remove(0);
        }
        this.entries.add(0, new Entry(data_version, converter));
        this.max_version = Math.max(max_version, data_version);
    }

    /**
     * Performs migrations/cleanup of the input data. This is always executed
     * before actual migration begins, regardless of data version.
     *
     * @param args Input data
     */
    protected abstract void baseMigrate(Map<String, Object> args);

    /**
     * Performs migration of the raw before-deserialization Map
     *
     * @param args Input data
     * @param dataVersionKey Key into the data containing the data version ("v")
     */
    public void migrate(Map<String, Object> args, String dataVersionKey) {
        baseMigrate(args);

        Object version_raw = args.get(dataVersionKey);
        if (version_raw instanceof Number) {
            int version = ((Number) version_raw).intValue();
            if (version > this.curr_version && version <= this.max_version) {
                // Requires conversion, go down the list of item stack converters and process those applicable
                for (Entry entry : this.entries) {
                    if (version > entry.output_version) {
                        if (entry.converter.convert(args)) {
                            // Successful conversion
                            version = entry.output_version;
                        } else {
                            // Item is not supported past this point
                            break;
                        }
                    }
                }

                // Update version
                if (version == 0) {
                    args.remove(dataVersionKey);
                } else {
                    args.put(dataVersionKey, Integer.valueOf(version));
                }
            }
        }
    }

    protected static void logFailDeserialize(Map<String, Object> args) {
        Logging.LOGGER_CONFIG.warning("Failed to deserialize ItemStack: " + stringifyPrintDocument(args));
    }

    private static String stringifyPrintDocument(Object value) {
        StringBuilder str = new StringBuilder();
        stringifyPrintDocument(str, 0, value);
        return str.toString();
    }

    private static void stringifyPrintDocument(StringBuilder str, int indent, Object value) {
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (map.isEmpty()) {
                str.append("{}");
                return;
            }

            str.append('\n');
            for (Map.Entry<?, ?> e : map.entrySet()) {
                stringifyIndent(str, indent + 1);
                str.append(e.getKey()).append(": ");
                stringifyPrintDocument(str, indent + 1, e.getValue());
                str.append('\n');
            }
        } else if (value instanceof Collection) {
            Collection<?> list = (Collection<?>) value;
            if (list.isEmpty()) {
                str.append("[]");
                return;
            }
            str.append('\n');
            for (Object listItem : list) {
                stringifyIndent(str, indent);
                str.append("- ");
                stringifyPrintDocument(str, indent + 1, listItem);
            }
        } else {
            str.append(value);
        }
    }

    private static void stringifyIndent(StringBuilder str, int indent) {
        for (int i = 0; i < indent; i++) {
            str.append("  ");
        }
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

    /**
     * Converts the raw YAML from a data version to an older data version
     */
    private static final class Entry {
        public final int output_version;
        public final ConverterFunction converter;

        public Entry(int output_version, ConverterFunction converter) {
            if (converter == null) {
                throw new IllegalArgumentException("Converter can not be null");
            }
            this.output_version = output_version;
            this.converter = converter;
        }
    }

    @FunctionalInterface
    public interface ConverterFunction {
        ConverterFunction NO_CONVERSION = map -> { return true; };

        boolean convert(Map<String, Object> values);
    }
}
