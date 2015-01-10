package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.BasicConverter;

/**
 * An empty converter that does nothing but casting the values to the output type
 * 
 * @param <T> - type of output
 */
public class EmptyConverter<T> extends BasicConverter<T> {

	public EmptyConverter(Class<T> outputType) {
		super(outputType);
	}

	@Override
	public T convertSpecial(Object value, Class<?> valueType, T def) {
		return def;
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}
}
