package com.bergerkiller.bukkit.common.natives;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.NBTUtil;
import com.bergerkiller.bukkit.common.wrappers.nbt.CommonTag;

import net.minecraft.server.v1_4_R1.*;

/**
 * Stores the information to obtain and use NBT Tags
 */
public class NBTTagInfo {
	private static final SafeField<Byte> nbtListType = new SafeField<Byte>(NBTTagList.class, "type");
	private static final Map<Class<?>, NBTTagInfo> dataTags = new HashMap<Class<?>, NBTTagInfo>();
	private static final Map<Class<?>, NBTTagInfo> nbtTags = new HashMap<Class<?>, NBTTagInfo>();

	private static void registerNBTTag(Class<?> nbtType) {
		try {
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
		registerNBTTag(NBTTagByte.class);
		registerNBTTag(NBTTagShort.class);
		registerNBTTag(NBTTagInt.class);
		registerNBTTag(NBTTagLong.class);
		registerNBTTag(NBTTagFloat.class);
		registerNBTTag(NBTTagDouble.class);
		registerNBTTag(NBTTagString.class);
		registerNBTTag(NBTTagByteArray.class);
		registerNBTTag(NBTTagIntArray.class);
		registerNBTTag(NBTTagList.class);
		registerNBTTag(NBTTagCompound.class);
	}

	public static NBTTagInfo findInfo(Object handle) {
		if (handle == null) {
			throw new RuntimeException("Can not find proper information for a null handle");
		}
		NBTTagInfo info = nbtTags.get(handle.getClass());
		if (info == null) {
			throw new RuntimeException("Unsupported NBTTag Handle: " + handle.getClass().getName());
		}
		return info;
	}

	public static NBTTagInfo findInfo(String name, Object data) {
		if (data == null) {
			throw new RuntimeException("Can not find a tag for null data");
		}
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

	public final Class<?> nbtType;
	public final Constructor<?> constructor;
	public final Field dataField;
	public final Class<?> dataType;

	public NBTTagInfo(Class<?> nbtClass) throws Throwable {
		this.nbtType = nbtClass;
		if (nbtClass.equals(NBTTagList.class)) {
			this.dataField = nbtClass.getDeclaredField("list");
			this.dataType = List.class;
			this.constructor = nbtClass.getDeclaredConstructor(String.class);
		} else if (nbtClass.equals(NBTTagCompound.class)) {
			this.dataField = nbtClass.getDeclaredField("map");
			this.dataType = Map.class;
			this.constructor = nbtClass.getDeclaredConstructor(String.class);
		} else {
			this.dataField = nbtClass.getDeclaredField("data");
			this.dataType = this.dataField.getType();
			this.constructor = nbtClass.getDeclaredConstructor(String.class, this.dataType);
		}
		this.dataField.setAccessible(true);
	}

	public byte getListType(Object handle) {
		validateHandle(handle);
		return nbtListType.get(handle);
	}

	public void setListType(Object handle, byte type) {
		validateHandle(handle);
		nbtListType.set(handle, type);
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
		try {
			if (data == null) {
				throw new RuntimeException("Can not create a tag for null data");
			}
			if (!dataType.isAssignableFrom(data.getClass())) {
				throw new RuntimeException("Incompatible data for this type of tag");
			}
			final Object handle;
			if (nbtType.equals(NBTTagList.class)) {
				// Create a new list of valid NBT handles
				List<Object> oldData = (List<Object>) data;
				ArrayList<Object> newData = new ArrayList<Object>(oldData.size());
				byte type = 0;
				for (Object element : oldData) {
					NBTBase base;
					if (element instanceof NBTBase) {
						base = (NBTBase) element;
					} else if (element instanceof CommonTag) {
						base = (NBTBase) ((CommonTag<?>) element).getHandle();
					} else {
						base = (NBTBase) NBTUtil.createHandle(null, element);
					}
					type = base.getTypeId();
				}
				// Assign this data to a new valid NBT Tag List
				handle = constructor.newInstance(name);
				nbtListType.set(handle, type);
				dataField.set(handle, newData);
			} else if (nbtType.equals(NBTTagCompound.class)) {
				// Fix up the map data
				Map<Object, Object> oldData = (Map<Object, Object>) data;
				Map<String, Object> newData = new HashMap<String, Object>(oldData.size());
				for (Entry<Object, Object> entry : oldData.entrySet()) {
					NBTBase base;
					final String key = entry.getKey().toString();
					if (entry.getValue() instanceof NBTBase) {
						base = (NBTBase) entry.getValue();
					} else if (entry.getValue() instanceof CommonTag) {
						base = (NBTBase) ((CommonTag<?>) entry.getValue()).getHandle();
					} else {
						base = (NBTBase) NBTUtil.createHandle(key, entry.getValue());
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
