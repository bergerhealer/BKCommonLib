package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.mountiplex.conversion.type.ProxyConverter;

/**
 * Converts values to a primitive array
 *
 * @param <T> - type of primitive array
 */
@Deprecated
public class PrimitiveArrayConverter<T> extends ProxyConverter<T> {

    private PrimitiveArrayConverter(Class<T> outputType) {
        super(outputType);
    }

    public static <T> PrimitiveArrayConverter<T> create(Class<T> type) {
        return new PrimitiveArrayConverter<T>(type);
    }
}
