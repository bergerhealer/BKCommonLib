package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.utils.ParseUtil;

/**
 * Conversion to various types of Number implementations
 * 
 * @param <T> - type of Number
 */
public abstract class NumberConverter<T> extends PrimitiveConverter<T> {
	public static final NumberConverter<Byte> toByte = new NumberConverter<Byte>(Byte.class, (byte) 0) {
		@Override
		public Byte convert(Number value) {
			if (value instanceof Byte) {
				return (Byte) value;
			}
			return Byte.valueOf(value.byteValue());
		}

		@Override
		protected Byte parse(String text) throws NumberFormatException {
			return Byte.valueOf(text);
		}
	};
	public static final NumberConverter<Short> toShort = new NumberConverter<Short>(Short.class, (short) 0) {
		@Override
		public Short convert(Number value) {
			if (value instanceof Short) {
				return (Short) value;
			}
			return Short.valueOf(value.shortValue());
		}

		@Override
		protected Short parse(String text) throws NumberFormatException {
			return Short.valueOf(text);
		}
	};
	public static final NumberConverter<Integer> toInt = new NumberConverter<Integer>(Integer.class, 0) {
		@Override
		public Integer convert(Number value) {
			if (value instanceof Integer) {
				return (Integer) value;
			}
			return Integer.valueOf(value.intValue());
		}

		@Override
		protected Integer parse(String text) throws NumberFormatException {
			return Integer.valueOf(text);
		}
	};
	public static final NumberConverter<Long> toLong = new NumberConverter<Long>(Long.class, (long) 0) {
		@Override
		public Long convert(Number value) {
			if (value instanceof Long) {
				return (Long) value;
			}
			return Long.valueOf(value.longValue());
		}

		@Override
		protected Long parse(String text) throws NumberFormatException {
			return Long.valueOf(text);
		}
	};
	public static final NumberConverter<Float> toFloat = new NumberConverter<Float>(Float.class, 0.0f) {
		@Override
		public Float convert(Number value) {
			if (value instanceof Float) {
				return (Float) value;
			}
			return Float.valueOf(value.floatValue());
		}

		@Override
		protected Float parse(String text) throws NumberFormatException {
			return Float.valueOf(text);
		}
	};
	public static final NumberConverter<Double> toDouble = new NumberConverter<Double>(Double.class, 0.0) {
		@Override
		public Double convert(Number value) {
			if (value instanceof Double) {
				return (Double) value;
			}
			return Double.valueOf(value.doubleValue());
		}

		@Override
		protected Double parse(String text) throws NumberFormatException {
			return Double.valueOf(text);
		}
	};

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
