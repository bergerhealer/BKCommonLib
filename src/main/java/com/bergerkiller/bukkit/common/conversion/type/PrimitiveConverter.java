package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.mountiplex.conversion.type.ProxyConverter;

/**
 * Base class for a type converter that deals with primitives that can not be
 * null
 *
 * @param <T> - type of primitive (boxed type)
 */
@Deprecated
public final class PrimitiveConverter<T> extends ProxyConverter<T> {
    private final T defaultValue;

    private PrimitiveConverter(Class<T> outputType, T defaultValue) {
        super(outputType);
        this.defaultValue = defaultValue;
    }

    /**
     * Converts the input value to the output type<br>
     * If this failed, the ZERO (e.g. 0.0, false, etc.) value for the type is
     * returned instead
     *
     * @param value to convert
     * @return converted value (never null)
     */
    public T convertZero(Object value) {
        return convert(value, defaultValue);
    }

    public static <T> PrimitiveConverter<T> create(Class<T> type, T defaultValue) {
        return new PrimitiveConverter<T>(type, defaultValue);
    }
}
