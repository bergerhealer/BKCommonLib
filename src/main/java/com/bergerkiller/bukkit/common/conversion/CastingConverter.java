package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Extends another converter and tries to cast the returned type
 * 
 * @param <T> - type to cast the output to
 */
public class CastingConverter<T> extends BasicConverter<T> {
	private final Converter<?> baseConvertor;

	public CastingConverter(Class<T> outputType, Converter<?> baseConvertor) {
		super(outputType);
		this.baseConvertor = baseConvertor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T convert(Object value, T def) {
		if (value == null) {
			return def;
		}
		Object val = CommonUtil.tryCast(this.baseConvertor.convert(value), this.getOutputType());
		if (val != null) {
			return (T) val;
		} else {
			return def;
		}
	}
}
