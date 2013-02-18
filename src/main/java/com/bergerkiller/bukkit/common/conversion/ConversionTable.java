package com.bergerkiller.bukkit.common.conversion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Stores all available converters to convert Object values to a requested type
 */
public class ConversionTable {
	private static final Map<Class<?>, Converter<Object>> converters = new ConcurrentHashMap<Class<?>, Converter<Object>>();
	static {
		registerAll(ArrayConverter.class);
		registerAll(CollectionConverter.class);
		registerAll(PrimitiveConverter.class);
		registerAll(HandleConverter.class);
		registerAll(WrapperConverter.class);
	}

	/**
	 * Registers all available static convertor constants found in the Class or enum
	 * 
	 * @param convertorConstants class container to register
	 */
	public static void registerAll(Class<?> convertorConstants) {
		for (Object convertor : CommonUtil.getClassConstants(convertorConstants)) {
			if (convertor instanceof Converter) {
				register((Converter<?>) convertor);
			}
		}
	}

	/**
	 * Registers a convertor so it can be used to convert to the output type it represents
	 * 
	 * @param convertor to register
	 */
	@SuppressWarnings("unchecked")
	public static void register(Converter<?> convertor) {
		converters.put(convertor.getOutputType(), (Converter<Object>) convertor);
	}

	/**
	 * Obtains the converter used to convert to the type specified
	 * 
	 * @param type to convert to
	 * @return converter
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> Converter<T> getConverter(Class<T> type) {
		Converter<T> converter = (Converter<T>) converters.get(type);
		if (converter == null) {
			if (type.isArray()) {
				// Maybe converting to an Object array of a certain component type?
				// Note: Primitives are already dealt with and registered in the map
				final Class<?> componentType = type.getComponentType();
				if (!componentType.isPrimitive()) {
					// Use the ObjectArrayConvertor to deal with this
					converter = new ObjectArrayConverter(type);
					converters.put(type, (Converter<Object>) converter);
				}
			} else {
				// Maybe the requested type is an extension?
				// If so, put a new casting converter in place to deal with it
				for (Converter<Object> conv : converters.values()) {
					if (conv.isCastingSupported() && conv.getOutputType().isAssignableFrom(type)) {
						converter = new CastingConverter(type, conv);
						converters.put(type, (Converter<Object>) converter);
						break;
					}
				}
			}
			return null;
		}
		return (Converter<T>) converter;
	}

	/**
	 * Converts an object to the given type using previously registered converters
	 * 
	 * @param value to convert
	 * @param type to convert to
	 * @param def value to return on failure
	 * @return the converted value
	 */
	public static <T> T convert(Object value, Class<T> type, T def) {
		if (value == null) {
			return def;
		}
		final Class<?> valueType = value.getClass();
		if (type.isAssignableFrom(valueType)) {
			return type.cast(value);
		}
		Converter<T> converter = getConverter(type);
		if (converter != null) {
			return converter.convert(valueType, def);
		} else {
			return def;
		}
	}
}
