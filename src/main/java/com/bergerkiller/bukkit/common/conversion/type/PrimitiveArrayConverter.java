package com.bergerkiller.bukkit.common.conversion.type;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;

/**
 * Converts values to a primitive array
 * 
 * @param <T> - type of primitive array
 */
public class PrimitiveArrayConverter<T> extends BasicConverter<T> {
	public static final PrimitiveArrayConverter<boolean[]> toBoolArr = new PrimitiveArrayConverter<boolean[]>(boolean[].class, PrimitiveConverter.toBool);
	public static final PrimitiveArrayConverter<char[]> toCharArr = new PrimitiveArrayConverter<char[]>(char[].class, PrimitiveConverter.toChar);
	public static final PrimitiveArrayConverter<byte[]> toByteArr = new PrimitiveArrayConverter<byte[]>(byte[].class, PrimitiveConverter.toByte);
	public static final PrimitiveArrayConverter<short[]> toShortArr = new PrimitiveArrayConverter<short[]>(short[].class, PrimitiveConverter.toShort);
	public static final PrimitiveArrayConverter<int[]> toIntArr = new PrimitiveArrayConverter<int[]>(int[].class, PrimitiveConverter.toInt);
	public static final PrimitiveArrayConverter<long[]> toLongArr = new PrimitiveArrayConverter<long[]>(long[].class, PrimitiveConverter.toLong);
	public static final PrimitiveArrayConverter<float[]> toFloatArr = new PrimitiveArrayConverter<float[]>(float[].class, PrimitiveConverter.toFloat);
	public static final PrimitiveArrayConverter<double[]> toDoubleArr = new PrimitiveArrayConverter<double[]>(double[].class, PrimitiveConverter.toDouble);

	private final PrimitiveConverter<?> elementConverter;

	public PrimitiveArrayConverter(Class<T> outputType, PrimitiveConverter<?> elementConverter) {
		super(outputType);
		this.elementConverter = elementConverter;
		if (elementConverter == null) {
			CommonPlugin.LOGGER_CONVERSION.log(Level.SEVERE, "Converter to " + outputType.getComponentType().getSimpleName() +
					"[] lacks a primitive element converter!");
		}
	}

	/**
	 * Converts a collection into a primitive array of the output component type
	 * 
	 * @param collection to convert
	 * @return converted array
	 */
	public T convert(Collection<?> collection) {
		final int length = collection.size();
		final Iterator<?> iter = collection.iterator();
		final T array = newInstance(length);
		for (int i = 0; i < length; i++) {
			Array.set(array, i, elementConverter.convertZero(iter.next()));
		}
		return array;
	}

	/**
	 * Converts a primitive array (e.g. int[]) to a different type of primitive array (e.g. long[])
	 * 
	 * @param primitiveArray to convert
	 * @return Converted array
	 */
	@SuppressWarnings("unchecked")
	public T convertPrimitiveArray(Object primitiveArray) {
		if (getOutputType().isAssignableFrom(primitiveArray.getClass())) {
			return (T) primitiveArray;
		}
		final int length = Array.getLength(primitiveArray);
		final T array = newInstance(length);
		for (int i = 0; i < length; i++) {
			Array.set(array, i, elementConverter.convertZero(Array.get(primitiveArray, i)));
		}
		return array;
	}

	/**
	 * Initializes a new instance of this type of primitive array
	 * 
	 * @param length of the new array
	 * @return a new instance of this array
	 */
	@SuppressWarnings("unchecked")
	public T newInstance(int length) {
		return (T) Array.newInstance(getOutputType().getComponentType(), length);
	}

	@Override
	public T convertSpecial(Object value, Class<?> valueType, T def) {
		if (value instanceof Collection) {
			return convert((Collection<?>) value);
		} else if (value instanceof Map) {
			return convert(((Map<?, ?>) value).values());
		}
		final Class<?> type = value.getClass();
		if (type.isArray()) {
			if (type.getComponentType().isPrimitive()) {
				return convertPrimitiveArray(value);
			} else {
				return convert(Arrays.asList((Object[]) value));
			}
		} else {
			return convert(Arrays.asList(value));
		}
	}
}
