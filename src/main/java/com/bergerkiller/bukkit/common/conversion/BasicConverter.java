package com.bergerkiller.bukkit.common.conversion;

/**
 * A basic implementation which stores the output type
 *
 * @param <T> - output type
 */
public abstract class BasicConverter<T> extends Converter<T> {

    public BasicConverter(Class<T> outputType) {
        super(outputType);
    }

    /**
     * Called when a non-null and uncastable object needs to be converted. If
     * such a thing is not supported, return def in the method body.<br><br>
     * <p/>
     * This method needs to be implemented to satisfy the BasicConverter system.
     * Null check and output assigning checks are already performed before this
     * call.
     *
     * @param value to convert (not null and can not be cast to the output type)
     * @param valueType of the value
     * @param def to return on failure
     * @return converted value, or def on failure
     */
    protected abstract T convertSpecial(Object value, Class<?> valueType, T def);

    @Override
    @SuppressWarnings("unchecked")
    public final T convert(Object value, T def) {
        if (value == null) {
            return def;
        } else if (getOutputType().isInstance(value)) {
            return (T) value;
        } else {
            return convertSpecial(value, value.getClass(), def);
        }
    }

    @Override
    public boolean isCastingSupported() {
        return false;
    }

    @Override
    public boolean isRegisterSupported() {
        return true;
    }

}
