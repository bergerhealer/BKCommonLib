package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.mountiplex.conversion.BasicConverter;
import com.bergerkiller.mountiplex.conversion2.Conversion;
import com.bergerkiller.mountiplex.conversion2.type.InputConverter;

/**
 * Converter for converting to wrapper classes (from handles and other types)
 * <p/>
 * <T> - type of wrapper
 */
@Deprecated
public class WrapperConverter<T> extends BasicConverter<T> {
    private InputConverter<T> converter = null;
    private final boolean isCastingSupported;

    public WrapperConverter(Class<?> outputType) {
        this(outputType, false);
    }

    public WrapperConverter(Class<?> outputType, boolean isCastingSupported) {
        super(outputType);
        this.isCastingSupported = isCastingSupported;
    }

    @Override
    public final boolean isCastingSupported() {
        return this.isCastingSupported;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final T convertSpecial(Object value, Class<?> valueType, T def) {
        if (this.converter == null) {
            this.converter = (InputConverter<T>) Conversion.find(this.getOutput());
            if (this.converter == null) {
                throw new RuntimeException("Converter to " + this.getOutput() + " not found");
            }
        }
        T result = this.converter.convert(value, def);
        if (result == null) {
            Thread.dumpStack();
            System.out.println("Failed to convert input " + value.getClass().getName() + " TO " + this.getOutput());
        }
        return result;
    }

    public static <T> WrapperConverter<T> create(Class<?> outputType, boolean isCastingSupported) {
        return new WrapperConverter<T>(outputType, isCastingSupported);
    }

    public static <T> WrapperConverter<T> create(Class<?> outputType) {
        return new WrapperConverter<T>(outputType);
    }
}
