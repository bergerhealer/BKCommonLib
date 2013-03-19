package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;

/**
 * Converter for converting to the handle of BasicWrapper types
 */
public class WrapperHandleConverter extends HandleConverter {

	public WrapperHandleConverter(Class<?> outputType) {
		super(outputType);
	}

	public WrapperHandleConverter(String outputTypeName) {
		super(outputTypeName);
	}

	@Override
	protected Object convertSpecial(Object value, Class<?> valueType, Object def) {
		if (value instanceof BasicWrapper) {
			return CommonUtil.tryCast(((BasicWrapper) value).getHandle(), getOutputType(), def);
		}
		return def;
	}
}
