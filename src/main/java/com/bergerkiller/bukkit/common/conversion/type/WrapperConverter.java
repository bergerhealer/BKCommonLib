package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.mountiplex.conversion.type.ProxyConverter;

/**
 * Converter for converting to wrapper classes (from handles and other types)
 * <p/>
 * <T> - type of wrapper
 */
@Deprecated
public class WrapperConverter<T> extends ProxyConverter<T> {
 
    public WrapperConverter(Class<?> outputType) {
        super(outputType);
    }

    public WrapperConverter(Class<?> outputType, boolean isCastingSupported) {
        super(outputType, isCastingSupported);
    }

    public static <T> WrapperConverter<T> create(Class<?> outputType, boolean isCastingSupported) {
        return new WrapperConverter<T>(outputType, isCastingSupported);
    }

    public static <T> WrapperConverter<T> create(Class<?> outputType) {
        return new WrapperConverter<T>(outputType);
    }
}
