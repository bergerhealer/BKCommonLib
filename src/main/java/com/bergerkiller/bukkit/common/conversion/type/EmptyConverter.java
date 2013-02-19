package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

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
	public T convert(Object value, T def) {
		return LogicUtil.fixNull(CommonUtil.tryCast(value, getOutputType()), def);
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}
}
