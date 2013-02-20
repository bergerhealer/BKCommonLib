package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.utils.ParseUtil;

/**
 * Converter implementation for converting to enum Class types<br>
 * Dynamically constructed when finding a suitable converter
 * 
 * @param <T> - type of Enum
 */
public class EnumConverter<T> extends BasicConverter<T> {

	public EnumConverter(Class<T> outputType) {
		super(outputType);
	}

	@Override
	public T convertSpecial(Object value, Class<?> valueType, T def) {
		String text = ConversionTypes.toString.convert(value);
		if (text != null) {
			return ParseUtil.parseEnum(getOutputType(), text, def);
		} else {
			return def;
		}
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}
}
