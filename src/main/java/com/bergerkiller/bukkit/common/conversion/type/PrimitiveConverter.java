package com.bergerkiller.bukkit.common.conversion.type;

import java.lang.reflect.Array;
import java.util.Collection;

import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;

/**
 * Base class for a type converter that deals with primitives that can not be null
 * 
 * @param <T> - type of primitive (boxed type)
 */
public abstract class PrimitiveConverter<T> extends BasicConverter<T> {
	public static final NumberConverter<Byte> toByte = NumberConverter.toByte;
	public static final NumberConverter<Short> toShort = NumberConverter.toShort;
	public static final NumberConverter<Integer> toInt = NumberConverter.toInt;
	public static final NumberConverter<Long> toLong = NumberConverter.toLong;
	public static final NumberConverter<Float> toFloat = NumberConverter.toFloat;
	public static final NumberConverter<Double> toDouble = NumberConverter.toDouble;
	public static final PrimitiveConverter<Boolean> toBool = new PrimitiveConverter<Boolean>(Boolean.class, false) {
		@Override
		public Boolean convert(Object value, Boolean def) {
			if (value == null) {
				return def;
			} else if (value instanceof Boolean) {
				return (Boolean) value;
			} else if (value instanceof Number) {
				return ((Number) value).doubleValue() != 0.0;
			} else {
				return ParseUtil.parseBool(value.toString(), def);
			}
		}
	};
	public static final PrimitiveConverter<Character> toChar = new PrimitiveConverter<Character>(Character.class, '\0') {
		@Override
		public Character convert(Object value, Character def) {
			if (value == null) {
				return def;
			} else if (value instanceof Character) {
				return (Character) value;
			} else if (value instanceof Number) {
				return (char) ((Number) value).byteValue();
			} else {
				final String text = value.toString();
				return LogicUtil.nullOrEmpty(text) ? def : text.charAt(0);
			}
		}
	};
	public static final PrimitiveConverter<String> toString = new PrimitiveConverter<String>(String.class, "") {
		@Override
		public String convert(Object value, String def) {
			if (value == null) {
				return def;
			} else if (value instanceof String) {
				return (String) value;
			} else if (value instanceof CharSequence) {
				return value.toString();
			} else if (value instanceof char[]) {
				return String.copyValueOf((char[]) value);
			}
			if (value.getClass().isArray()) {
				if (value.getClass().getComponentType().isPrimitive()) {
					// Primitive type array - simply append elements with a space
					final int length = Array.getLength(value);
					StringBuilder builder = new StringBuilder(length * 5);
					for (int i = 0; i < length; i++) {
						if (i > 0) {
							builder.append(' ');
						}
						builder.append(toString.convert(Array.get(value, i), "0"));
					}
					return builder.toString();
				} else {
					// Let the collection based conversion deal with it
					value = CollectionConverter.toList.convert(value);
					if (value == null) {
						return def;
					}
				}
			}
			if (value instanceof Collection) {
				Collection<?> collection = (Collection<?>) value;
				StringBuilder builder = new StringBuilder(collection.size() * 100);
				boolean first = true;
				for (Object element : collection) {
					if (!first) {
						builder.append('\n');
					}
					builder.append(toString.convert(element, ""));
					first = false;
				}
				return builder.toString();
			} else {
				return value.toString();
			}
		}
	};

	/**
	 * A constant describing the 0-value of the primitive type
	 */
	public final T ZERO;

	public PrimitiveConverter(Class<T> outputType, T zero) {
		super(outputType);
		this.ZERO = zero;
	}

	/**
	 * Converts the input value to the output type<br>
	 * If this failed, the ZERO (e.g. 0.0, false, etc.) value for the type is returned instead
	 * 
	 * @param value to convert
	 * @return converted value (never null)
	 */
	public T convertZero(Object value) {
		return convert(value, ZERO);
	}
}
