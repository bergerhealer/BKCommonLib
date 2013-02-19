package com.bergerkiller.bukkit.common.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.reflection.classes.NBTRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.NBTUtil;

/**
 * Stores the information to obtain and use NBT Tags
 */
public class NBTTagInfo {
	private static final Map<Class<?>, NBTTagInfo> dataTags = new HashMap<Class<?>, NBTTagInfo>();
	private static final Map<Class<?>, NBTTagInfo> nbtTags = new HashMap<Class<?>, NBTTagInfo>();

	private static void registerNBTTag(String name) {
		try {
			Class<?> nbtType = CommonUtil.getNMSClass(name);
			NBTTagInfo dataTag = new NBTTagInfo(nbtType);
			Class<?> unboxed = LogicUtil.getUnboxedType(dataTag.dataType);
			if (unboxed != null) {
				dataTags.put(unboxed, dataTag);
			}
			Class<?> boxed = LogicUtil.getBoxedType(dataTag.dataType);
			if (boxed != null) {
				dataTags.put(boxed, dataTag);
			}
			dataTags.put(dataTag.dataType, dataTag);
			nbtTags.put(dataTag.nbtType, dataTag);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	static {
		registerNBTTag("NBTTagByte");
		registerNBTTag("NBTTagShort");
		registerNBTTag("NBTTagInt");
		registerNBTTag("NBTTagLong");
		registerNBTTag("NBTTagFloat");
		registerNBTTag("NBTTagDouble");
		registerNBTTag("NBTTagString");
		registerNBTTag("NBTTagByteArray");
		registerNBTTag("NBTTagIntArray");
		registerNBTTag("NBTTagList");
		registerNBTTag("NBTTagCompound");
	}

	public static NBTTagInfo findInfo(Object data) {
		if (data == null) {
			throw new RuntimeException("Can not find tag information of null data");
		} else if (data instanceof CommonTag) {
			return ((CommonTag) data).info;
		} else if (NBTRef.NBTBase.isInstance(data)) {
			// Get from handle
			NBTTagInfo info = nbtTags.get(data.getClass());
			if (info == null) {
				throw new RuntimeException("Unsupported NBTTag Handle: " + data.getClass().getName());
			}
			return info;
		} else {
			// Get from data
			final Class<?> dataType;
			if (data instanceof List) {
				dataType = List.class;
			} else if (data instanceof Map) {
				dataType = Map.class;
			} else {
				dataType = data.getClass();
			}
			NBTTagInfo info = dataTags.get(dataType);
			if (info == null) {
				throw new RuntimeException("Unknown tag data type: " + dataType.getName());
			}
			return info;
		}
	}

	public final Class<?> nbtType;
	public final Constructor<?> constructor;
	public final Field dataField;
	public final Class<?> dataType;

	public NBTTagInfo(Class<?> nbtClass) throws Throwable {
		this.nbtType = nbtClass;
		if (NBTRef.NBTTagList.isType(nbtClass)) {
			this.dataField = nbtClass.getDeclaredField("list");
			this.dataType = List.class;
			this.constructor = nbtClass.getDeclaredConstructor(String.class);
		} else if (NBTRef.NBTTagCompound.isType(nbtClass)) {
			this.dataField = nbtClass.getDeclaredField("map");
			this.dataType = Map.class;
			this.constructor = nbtClass.getDeclaredConstructor(String.class);
		} else {
			this.dataField = nbtClass.getDeclaredField("data");
			final Class<?> dataType = this.dataField.getType();
			this.constructor = nbtClass.getDeclaredConstructor(String.class, dataType);
			// Box it
			final Class<?> boxed = LogicUtil.getBoxedType(dataType);
			if (boxed == null) {
				this.dataType = dataType;
			} else {
				this.dataType = boxed;
			}
		}
		this.dataField.setAccessible(true);
	}

	public void setData(Object handle, Object data) {
		validateHandle(handle);
		if (data == null) {
			throw new IllegalArgumentException("Can not set a handle data to null");
		} else if (!dataType.isAssignableFrom(data.getClass())) {
			throw new IllegalArgumentException("NBTInfo not suitable for specified data");
		}
		try {
			dataField.set(handle, data);
		} catch (Throwable t) {
			throw new RuntimeException("Unable to write data to handle", t);
		}
	}

	public Object getData(Object handle) {
		validateHandle(handle);
		try {
			return dataField.get(handle);
		} catch (Throwable t) {
			throw new RuntimeException("Unable to read data from handle", t);
		}
	}

	private void validateHandle(Object handle) {
		if (handle == null) {
			throw new IllegalArgumentException("Handle is null");
		} else if (!nbtType.isAssignableFrom(handle.getClass())) {
			throw new IllegalArgumentException("Handle - NBTTagInfo type mismatch");
		}
	}

	@SuppressWarnings("unchecked")
	public Object createHandle(String name, Object data) {
		if (data == null) {
			throw new RuntimeException("Can not create a tag for null data");
		}
		if (nbtType.isAssignableFrom(data.getClass())) {
			NBTRef.setName.invoke(data, name);
			return data;
		}
		if (!dataType.isAssignableFrom(data.getClass())) {
			throw new RuntimeException("Can not store " + data.getClass().getName() + " in tag " + dataType.getSimpleName());
		}
		// Create a new handle from data
		try {
			final Object handle;
			if (NBTRef.NBTTagList.isType(nbtType)) {
				// Create a new list of valid NBT handles
				List<Object> oldData = (List<Object>) data;
				ArrayList<Object> newData = new ArrayList<Object>(oldData.size());
				byte type = 0;
				for (Object element : oldData) {
					Object base;
					if (NBTRef.NBTBase.isInstance(element)) {
						base = element;
					} else if (element instanceof CommonTag) {
						base = ((CommonTag) element).getHandle();
					} else {
						base = NBTUtil.createHandle(null, element);
					}
					type = NBTUtil.getTypeId(base);
				}
				// Assign this data to a new valid NBT Tag List
				handle = constructor.newInstance(name);
				NBTRef.nbtListType.set(handle, type);
				dataField.set(handle, newData);
			} else if (NBTRef.NBTTagCompound.isType(nbtType)) {
				// Fix up the map data
				Map<Object, Object> oldData = (Map<Object, Object>) data;
				Map<String, Object> newData = new HashMap<String, Object>(oldData.size());
				for (Entry<Object, Object> entry : oldData.entrySet()) {
					Object base;
					final String key = entry.getKey().toString();
					if (NBTRef.NBTBase.isInstance(entry.getValue())) {
						base = entry.getValue();
					} else if (entry.getValue() instanceof CommonTag) {
						base = ((CommonTag) entry.getValue()).getHandle();
					} else {
						base = NBTUtil.createHandle(key, entry.getValue());
					}
					newData.put(key, base);
				}
				// Assign this data to a new valid NBT Tag Compound
				handle = constructor.newInstance(name);
				dataField.set(handle, newData);
			} else {
				handle = constructor.newInstance(name, data);
			}
			return handle;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to create a new handle", t);
		}
	}
}
