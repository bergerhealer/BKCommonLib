package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.utils.ParseUtil;

/**
 * Conversion to various types of Number implementations
 * 
 * @param <T> - type of Number
 */
public abstract class NumberConverter<T> extends PrimitiveConverter<T> {

	public NumberConverter(Class<T> outputType, T zero) {
		super(outputType, zero);
	}

	/**
	 * Converts the input number to the output type
	 * 
	 * @param number to convert (no null allowed)
	 * @return converted output type
	 */
	public abstract T convert(Number number);

	/**
	 * Parses the number from text (internal method)
	 * 
	 * @param text to parse
	 * @return the parsed value
	 * @throws NumberFormatException if parsing fails
	 */
	protected abstract T parse(String text) throws NumberFormatException;

	/**
	 * Converts the input text to the output type
	 * 
	 * @param text to convert
	 * @param def value to return when conversion fails
	 * @return converted output type
	 */
	public T convert(String text, T def) {
		try {
			return parse(ParseUtil.filterNumeric(text));
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	@Override
	public T convertSpecial(Object value, Class<?> valueType, T def) {
		if (value instanceof Number) {
			return convert((Number) value);
		} else {
			return convert(value.toString(), def);
		}
	}
}
