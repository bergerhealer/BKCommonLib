package com.bergerkiller.bukkit.common.conversion;

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
		public Byte convert(String text, Byte def) {
			return ParseUtil.parseByte(text, def);
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
		public Short convert(String text, Short def) {
			return ParseUtil.parseShort(text, def);
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
		public Integer convert(String text, Integer def) {
			return ParseUtil.parseInt(text, def);
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
		public Long convert(String text, Long def) {
			return ParseUtil.parseLong(text, def);
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
		public Float convert(String text, Float def) {
			return ParseUtil.parseFloat(text, def);
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
		public Double convert(String text, Double def) {
			return ParseUtil.parseDouble(text, def);
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
	 * Converts the input text to the output type
	 * 
	 * @param text to convert
	 * @param def value to return when conversion fails
	 * @return converted output type
	 */
	public abstract T convert(String text, T def);

	@Override
	public T convert(Object value, T def) {
		if (value == null) {
			return def;
		} else if (value instanceof Number) {
			return convert((Number) value);
		} else {
			return convert(value.toString(), def);
		}
	}
}
