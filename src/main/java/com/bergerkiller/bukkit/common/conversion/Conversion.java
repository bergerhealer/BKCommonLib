package com.bergerkiller.bukkit.common.conversion;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import com.bergerkiller.bukkit.common.conversion.type.ConversionTypes;
import com.bergerkiller.bukkit.common.conversion.type.EmptyConverter;
import com.bergerkiller.bukkit.common.conversion.type.EnumConverter;
import com.bergerkiller.bukkit.common.conversion.type.ObjectArrayConverter;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
/**
* Stores all available converters to convert Object values to a requested type
*/
public class Conversion extends ConversionTypes {
private static final Map<Class<?>, Converter<Object>> converters = new ConcurrentHashMap<Class<?>, Converter<Object>>();
static {
try {
registerAll(ConversionTypes.class);
ConversionPairs.class.getModifiers(); // Load this class
} catch (Throwable t) {
CommonPlugin.LOGGER_CONVERSION.log(Level.SEVERE, "Failed to initialize default converters", t);
}
}
/**
* Registers all available static convertor constants found in the Class or enum
*
* @param convertorConstants class container to register
*/
public static void registerAll(Class<?> convertorConstants) {
for (Object convertor : CommonUtil.getClassConstants(convertorConstants, Converter.class)) {
if (convertor instanceof Converter) {
register((Converter<?>) convertor);
}
}
}
/**
* Registers a converter so it can be used to convert to the output type it represents.
* If the converter does not support registration, it is ignored
*
* @param converter to register
*/
@SuppressWarnings("unchecked")
public static void register(Converter<?> converter) {
if (!converter.isRegisterSupported()) {
return;
}
converters.put(converter.getOutputType(), (Converter<Object>) converter);
}
/**
* Obtains the converter used to convert to the type specified<br>
* If none is available yet for the type, a new one is created
*
* @param type to convert to
* @return converter
*/
@SuppressWarnings({"unchecked", "rawtypes"})
public static <T> Converter<T> getConverter(Class<T> type) {
if (type.isPrimitive()) {
type = (Class<T>) LogicUtil.getBoxedType(type);
}
Converter<T> converter = (Converter<T>) converters.get(type);
if (converter == null) {
if (type.isArray()) {
// Maybe converting to an Object array of a certain component type?
// Note: Primitives are already dealt with and registered in the map
final Class<?> componentType = type.getComponentType();
if (!componentType.isPrimitive()) {
// Use the ObjectArrayConvertor to deal with this
converter = new ObjectArrayConverter(componentType);
}
} else if (type.isEnum()) {
// Converting to an enum type - construct a new EnumConverter
converter = new EnumConverter<T>(type);
} else {
// Maybe the requested type is an extension?
// If so, put a new casting converter in place to deal with it
for (Converter<Object> conv : converters.values()) {
if (conv.isCastingSupported() && conv.getOutputType().isAssignableFrom(type)) {
converter = new CastingConverter(type, conv);
break;
}
}
}
// Resolve to the default casting-based converter if not found
if (converter == null) {
converter = new EmptyConverter(type);
}
// Found. Put into map for faster look-up
converters.put(type, (Converter<Object>) converter);
}
return (Converter<T>) converter;
}
/**
* Converts an object to the given type using previously registered converters
*
* @param value to convert
* @param def value to return on failure (can not be null)
* @return the converted value
*/
@SuppressWarnings("unchecked")
public static <T> T convert(Object value, T def) {
return convert(value, (Class<T>) def.getClass(), def);
}
/**
* Converts an object to the given type using previously registered converters
*
* @param value to convert
* @param type to convert to
* @return the converted value, or null on failure
*/
public static <T> T convert(Object value, Class<T> type) {
return convert(value, type, null);
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
return getConverter(type).convert(value, def);
}
}
