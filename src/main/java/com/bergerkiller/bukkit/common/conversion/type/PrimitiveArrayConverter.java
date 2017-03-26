package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.BasicConverter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * Converts values to a primitive array
 *
 * @param <T> - type of primitive array
 */
public class PrimitiveArrayConverter<T> extends BasicConverter<T> {
    public static final PrimitiveArrayConverter<boolean[]> toBoolArr = create(boolean[].class, PrimitiveConverter.toBool);
    public static final PrimitiveArrayConverter<char[]> toCharArr = create(char[].class, PrimitiveConverter.toChar);
    public static final PrimitiveArrayConverter<byte[]> toByteArr = create(byte[].class, PrimitiveConverter.toByte);
    public static final PrimitiveArrayConverter<short[]> toShortArr = create(short[].class, PrimitiveConverter.toShort);
    public static final PrimitiveArrayConverter<int[]> toIntArr = create(int[].class, PrimitiveConverter.toInt);
    public static final PrimitiveArrayConverter<long[]> toLongArr = create(long[].class, PrimitiveConverter.toLong);
    public static final PrimitiveArrayConverter<float[]> toFloatArr = create(float[].class, PrimitiveConverter.toFloat);
    public static final PrimitiveArrayConverter<double[]> toDoubleArr = create(double[].class, PrimitiveConverter.toDouble);

    private final PrimitiveConverter<?> elementConverter;

    private static <T> PrimitiveArrayConverter<T> create(Class<T> outputType, PrimitiveConverter<?> elementConverter) {
    	return new PrimitiveArrayConverter<T>(outputType, elementConverter);
    }

    private PrimitiveArrayConverter(Class<T> outputType, PrimitiveConverter<?> elementConverter) {
        super(outputType);
        this.elementConverter = elementConverter;
        if (elementConverter == null) {
        	Logging.LOGGER_CONVERSION.log(Level.SEVERE, "Converter to " + outputType.getComponentType().getSimpleName()
                    + "[] lacks a primitive element converter!");
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
     * Converts a primitive array (e.g. int[]) to a different type of primitive
     * array (e.g. long[])
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
