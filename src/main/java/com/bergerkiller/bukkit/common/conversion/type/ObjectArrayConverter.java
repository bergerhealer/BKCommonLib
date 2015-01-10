package com.bergerkiller.bukkit.common.conversion.type;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Converter for converting objects into various kinds of Object[] arrays
 */
public class ObjectArrayConverter<T> extends BasicConverter<T> {
	public static final ObjectArrayConverter<Object[]> toObjectArr = new ObjectArrayConverter<Object[]>(Object.class);
	public static final ObjectArrayConverter<ItemStack[]> toItemStackArr = new ObjectArrayConverter<ItemStack[]>(ItemStack.class);
	public static final ObjectArrayConverter<Object[]> toItemStackHandleArr = new ObjectArrayConverter<Object[]>(CommonUtil.getNMSClass("ItemStack"));

	@SuppressWarnings("unchecked")
	public ObjectArrayConverter(Class<?> componentOutputType) {
		super((Class<T>) LogicUtil.getArrayType(componentOutputType));
	}

	@Override
	public boolean isCastingSupported() {
		return false;
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

	/**
	 * Converts a value to a new Object[] array of the given component type
	 * 
	 * @param value to convert
	 * @param componentType of the Object array to convert to
	 * @param def value to return on failure (null, or an array of the component type)
	 * @return Converted array
	 */
	@SuppressWarnings("unchecked")
	public <E> E[] convert(Object value, Class<E> componentType, Object def) {
		if (value == null) {
			return (E[]) def;
		} else if (value instanceof Collection) {
			return convert((Collection<?>) value, componentType);
		} else if (value instanceof Map) {
			return convert(((Map<?, ?>) value).values(), componentType);
		}
		final Class<?> type = value.getClass();
		if (type.isArray()) {
			if (type.getComponentType().isPrimitive()) {
				return convertPrimitiveArray(value, componentType);
			} else if (type.getComponentType().equals(componentType)) {
				return (E[]) value;
			} else {
				return convert(Arrays.asList((Object[]) value), componentType);
			}
		} else {
			return convert(Arrays.asList(value), componentType);
		}
	}

	/**
	 * Converts a collection to an Object[] array
	 * 
	 * @param collection to convert
	 * @return converted Object[] array
	 */
	@SuppressWarnings("unchecked")
	public T convert(Collection<?> collection) {
		if (getOutputType().equals(Object[].class)) {
			return (T) collection.toArray();
		} else {
			return (T) convert(collection, getOutputType().getComponentType());
		}
	}

	/**
	 * Converts a collection to an array of the given component type<br>
	 * 
	 * Example:<br>
	 * Integer[] values = convert(list, Integer.class);
	 * 
	 * @param collection to convert
	 * @param componentType of the array to return
	 * @return Array of the specified componentType containing the elements from the collection
	 */
	public <E> E[] convert(Collection<?> collection, Class<E> componentType) {
		if (componentType.isPrimitive()) {
			throw new IllegalArgumentException("Can not convert to a primitve array type");
		} else {
			final int length = collection.size();
			final E[] array = LogicUtil.createArray(componentType, length);
			final Iterator<?> iter = collection.iterator();
			for (int i = 0; i < length; i++) {
				array[i] = Conversion.convert(iter.next(), componentType, null);
			}
			return array;
		}
	}

	/**
	 * Converts a primitive array (e.g. int[]) to an Object array of a requested type (Double[])
	 * 
	 * @param primitiveArray to convert
	 * @param componentType of the converted Object[] array
	 * @return Converted array of the given componentType
	 */
	public <E> E[] convertPrimitiveArray(Object primitiveArray, Class<E> componentType) {
		final int length = Array.getLength(primitiveArray);
		if (componentType.isPrimitive()) {
			throw new RuntimeException("Can not create an Object array of a primitive component type");
		}
		// Transfer data to the new array
		final E[] objectArray = LogicUtil.createArray(componentType, length);
		for (int i = 0; i < length; i++) {
			objectArray[i] = Conversion.convert(Array.get(primitiveArray, i), componentType, null);
		}
		return objectArray;
	}

	/**
	 * Converts a primitive array (e.g. int[]) to an Object array (Object[])
	 * 
	 * @param primitiveArray to convert
	 * @return Converted array
	 */
	@SuppressWarnings("unchecked")
	public T convertPrimitiveArray(Object primitiveArray) {
		if (getOutputType().equals(Object[].class)) {
			final int length = Array.getLength(primitiveArray);
			// Transfer data to the new array
			final Object[] objectArray = new Object[length];
			for (int i = 0; i < length; i++) {
				objectArray[i] = Array.get(primitiveArray, i);
			}
			return (T) objectArray;
		} else {
			return (T) convertPrimitiveArray(primitiveArray, getOutputType().getComponentType());
		}
	}
}
