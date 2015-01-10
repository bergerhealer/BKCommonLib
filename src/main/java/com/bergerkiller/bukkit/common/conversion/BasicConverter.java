package com.bergerkiller.bukkit.common.conversion;

/**
 * A basic implementation which stores the output type
 * 
 * @param <T> - output type
 */
public abstract class BasicConverter<T> implements Converter<T> {
	private final Class<T> outputType;

	public BasicConverter(Class<T> outputType) {
		this.outputType = outputType;
	}

	@Override
	public Class<T> getOutputType() {
		return outputType;
	}

	/**
	 * Called when a non-null and uncastable object needs to be converted.
	 * If such a thing is not supported, return def in the method body.<br><br>
	 * 
	 * This method needs to be implemented to satisfy the BasicConverter system.
	 * Null check and output assigning checks are already performed before this call.
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
	public final T convert(Object value) {
		return convert(value, null);
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}

	@Override
	public boolean isRegisterSupported() {
		return true;
	}

	@Override
	public <K> ConverterPair<T, K> formPair(Converter<K> converterB) {
		return new ConverterPair<T, K>(this, converterB);
	}

	@Override
	public <K> Converter<K> cast(Class<K> type) {
		return new CastingConverter<K>(type, this);
	}
}
