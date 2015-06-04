package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.BasicConverter;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Converter implementation for converting to various kinds of collections
 *
 * @param <T> - type of collection
 */
public abstract class CollectionConverter<T extends Collection<?>> extends BasicConverter<T> {

    public static final CollectionConverter<List<?>> toList = new CollectionConverter<List<?>>(List.class) {
        @Override
        public List<?> convert(Collection<?> collection) {
            if (collection instanceof List) {
                return (List<?>) collection;
            } else {
                return new ArrayList<Object>(collection);
            }
        }
    };
    public static final CollectionConverter<Set<?>> toSet = new CollectionConverter<Set<?>>(Set.class) {
        @Override
        public Set<?> convert(Collection<?> collection) {
            if (collection instanceof Set) {
                return (Set<?>) collection;
            } else {
                return new HashSet<Object>(collection);
            }
        }
    };

    @SuppressWarnings("unchecked")
    public CollectionConverter(Class<?> outputType) {
        super((Class<T>) outputType);
    }

    /**
     * Converts a collection to the output type
     *
     * @param collection to convert
     * @return converted output type
     */
    public abstract T convert(Collection<?> collection);

    @Override
    public T convertSpecial(Object value, Class<?> valueType, T def) {
        if (value instanceof Collection) {
            return convert((Collection<?>) value);
        } else if (value instanceof Map) {
            return convert(((Map<?, ?>) value).values());
        } else {
            final Class<?> type = value.getClass();
            if (type.isArray()) {
                if (type.getComponentType().isPrimitive()) {
                    // Slower packing logic
                    final int arrayLength = Array.getLength(value);
                    final List<Object> list = new ArrayList<Object>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        list.add(Array.get(value, i));
                    }
                    return convert(list);
                } else {
                    return convert(Arrays.asList((Object[]) value));
                }
            } else {
                return def;
            }
        }
    }
}
