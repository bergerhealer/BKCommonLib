package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.CastingConverter;
import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;

/**
 * An empty casting converter that casts to an unknown type, unsafe.
 * Casting errors may occur after calling convert, as the type returned violates the generic type.
 * 
 * @param <T> - type of the converter
 */
public final class EmptyConverterUnsafe<T> implements Converter<T> {
	@SuppressWarnings("rawtypes")
	public static final EmptyConverterUnsafe INSTANCE = new EmptyConverterUnsafe();

	private EmptyConverterUnsafe() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public T convert(Object value, T def) {
		return (T) value;
	}

	@Override
	public T convert(Object value) {
		return convert(value, null);
	}

	@Override
	public Class<T> getOutputType() {
		return null;
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}

	@Override
	public boolean isRegisterSupported() {
		return false;
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
