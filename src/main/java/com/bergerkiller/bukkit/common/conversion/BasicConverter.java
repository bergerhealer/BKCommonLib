package com.bergerkiller.bukkit.common.conversion;

/**
 * A basic implementation which stores the output type
 * 
 * @param <O> - output type
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
}
