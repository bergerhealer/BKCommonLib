package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Extends another converter and tries to cast the returned type
 * 
 * @param <T> - type to cast the output to
 */
public class CastingConverter<T> implements Converter<T> {
	private final Class<T> outputType;
	private final Converter<?> baseConvertor;

	public CastingConverter(Class<T> outputType, Converter<?> baseConvertor) {
		this.outputType = outputType;
		this.baseConvertor = baseConvertor;
	}

	@Override
	public T convert(Object value, T def) {
		return CommonUtil.tryCast(baseConvertor.convert(value), this.getOutputType(), def);
	}

	@Override
	public final T convert(Object value) {
		return convert(value, null);
	}

	@Override
	public Class<T> getOutputType() {
		return outputType;
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
