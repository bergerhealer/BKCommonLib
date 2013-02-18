package com.bergerkiller.bukkit.common.wrappers.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;

import com.bergerkiller.bukkit.common.natives.NBTTagInfo;
import com.bergerkiller.bukkit.common.utils.NBTUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;

import net.minecraft.server.v1_4_R1.*;

public class CommonTag<T extends NBTBase> extends BasicWrapper<T> {
	protected NBTTagInfo info;

	protected CommonTag() {
	}

	public CommonTag(String name, Object data) {
		info = NBTTagInfo.findInfo(name, data);
		super.setHandle(info.createHandle(name, data));
	}

	protected void setRawData(Object data) {
		info.setData(handle, data);
	}

	protected Object getRawData() {
		return info.getData(handle);
	}

	/**
	 * Gets the data stored by the tag
	 * 
	 * @return Tag data
	 */
	public Object getData() {
		return nbtToCommon(getRawData());
	}

	/**
	 * Sets the data stored by the tag
	 * 
	 * @param data to set to
	 */
	public void setData(Object data) {
		setRawData(commonToNbt(data));
	}

	/**
	 * Gets the name of this tag
	 * 
	 * @return tag name
	 */
	public String getName() {
		return handle.getName();
	}

	/**
	 * Sets a new name for this tag
	 * 
	 * @param newName to set to
	 */
	public void setName(String newName) {
		handle.setName(newName);
	}

	@Override
	public CommonTag<?> clone() {
		return create(handle.clone());
	}

	/**
	 * Obtains the value of a given object, converting elements to proper
	 * wrapped CommonTags if needed
	 * 
	 * @param handle
	 * @return Handle value, or null if none is found or contained
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T getData(Object handle) {
		return (T) nbtToCommon(NBTUtil.getData(handle));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected static Object commonToNbt(Object data) {
		if (data instanceof CommonTag) {
			return ((CommonTag<?>) data).getHandle();
		} else if (data instanceof NBTBase || data == null) {
			return data;
		} else if (data instanceof Entry) {
			Entry old = (Entry) data;
			// Replace
			Object newKey = commonToNbt(old.getKey());
			Object newValue = commonToNbt(old.getValue());
			// If changed, return new type
			if (newKey != old.getKey() || newValue != old.getValue()) {
				return new SimpleEntry(newKey, newValue);
			} else {
				return data;
			}
		} else if (data instanceof Set) {
			Set<Object> elems = (Set<Object>) data;
			HashSet<Object> tags = new HashSet<Object>(elems.size());
			// Replace
			for (Object value : elems) {
				tags.add(commonToNbt(value));
			}
			return tags;
		} else if (data instanceof Map) {
			Map<String, Object> elems = (Map<String, Object>) data;
			HashMap<String, Object> tags = new HashMap<String, Object>(elems.size());
			// Replace
			for (Entry<String, Object> entry : elems.entrySet()) {
				tags.put(entry.getKey(), commonToNbt(entry.getValue()));
			}
			return tags;
		} else if (data instanceof Collection) {
			Collection<Object> elems = (Collection<Object>) data;
			ArrayList<Object> tags = new ArrayList<Object>(elems.size());
			// Replace
			for (Object value : elems) {
				tags.add(commonToNbt(value));
			}
			return tags;
		} else {
			throw new RuntimeException("Data can not be converted to a handle: " + data.getClass().getName());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected static Object nbtToCommon(Object data) {
		if (data instanceof NBTBase) {
			return create(data);
		} else if (data instanceof CommonTag || data == null) {
			return data;
		} else if (data instanceof Entry) {
			Entry old = (Entry) data;
			// Replace
			Object newKey = nbtToCommon(old.getKey());
			Object newValue = nbtToCommon(old.getValue());
			// If changed, return new type
			if (newKey != old.getKey() || newValue != old.getValue()) {
				return new SimpleEntry(newKey, newValue);
			} else {
				return data;
			}
		} else if (data instanceof Set) {
			Set<Object> elems = (Set<Object>) data;
			HashSet<Object> tags = new HashSet<Object>(elems.size());
			// Replace
			for (Object value : elems) {
				tags.add(nbtToCommon(value));
			}
			return tags;
		} else if (data instanceof Map) {
			Map<String, Object> elems = (Map<String, Object>) data;
			HashMap<String, Object> tags = new HashMap<String, Object>(elems.size());
			// Replace
			for (Entry<String, Object> entry : elems.entrySet()) {
				tags.put(entry.getKey(), nbtToCommon(entry.getValue()));
			}
			return tags;
		} else if (data instanceof Collection) {
			Collection<Object> elems = (Collection<Object>) data;
			ArrayList<Object> tags = new ArrayList<Object>(elems.size());
			// Replace
			for (Object value : elems) {
				tags.add(nbtToCommon(value));
			}
			return tags;
		} else {
			throw new RuntimeException("Data can not be converted to CommonTag: " + data.getClass().getName());
		}
	}

	/**
	 * Creates a CommonTag storing the data under the name specified<br>
	 * The most suitable tag type and handle to represent the data are created
	 * 
	 * @param name of the new tag
	 * @param data to store
	 * @return a new CommonTag instance
	 */
	public static CommonTag<?> create(String name, Object data) {
		return create(NBTUtil.createHandle(name, data));
	}

	/**
	 * Creates a CommonTag from the handle specified<br>
	 * If the handle is null, null is returned
	 * 
	 * @param handle to create a wrapper class for
	 * @return Wrapper class suitable for the given handle
	 */
	public static CommonTag<?> create(Object handle) {
		if (handle == null) {
			return null;
		}
		CommonTag<?> tag;
		if (handle instanceof NBTTagCompound) {
			tag = new CommonTagCompound();
		} else if (handle instanceof NBTTagList) {
			tag = new CommonTagList();
		} else {
			tag = new CommonTag<NBTBase>();
		}
		tag.setHandle(handle);
		return tag;
	}
}
