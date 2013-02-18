package com.bergerkiller.bukkit.common.wrappers.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.bergerkiller.bukkit.common.utils.NBTUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;

import net.minecraft.server.v1_4_R1.NBTBase;
import net.minecraft.server.v1_4_R1.NBTTagCompound;

@SuppressWarnings("unchecked")
public class CommonTagCompound extends CommonTag<NBTTagCompound> implements Map<String, CommonTag<?>> {

	protected CommonTagCompound() {
	}

	public CommonTagCompound(String name, Map<String, ?> data) {
		super(name, data);
	}

	@Override
	public Map<String, CommonTag<?>> getData() {
		return (Map<String, CommonTag<?>>) super.getData();
	}

	@Override
	protected Map<String, Object> getRawData() {
		return (Map<String, Object>) super.getRawData();
	}

	private Collection<Object> getHandleValues() {
		return handle.c();
	}

	@Override
	public CommonTagCompound clone() {
		return (CommonTagCompound) super.clone();
	}

	/**
	 * Removes a single key and returns the data of the removed CommonTag
	 * 
	 * @param key to remove
	 * @return the removed Data at the key, or null if nothing was removed
	 */
	public Object removeData(String key) {
		if (key == null) {
			return null;
		}
		Object removedHandle = getRawData().remove(key);
		return removedHandle == null ? null : NBTUtil.getData(removedHandle);
	}

	/**
	 * Removes a single key and returns the removed CommonTag
	 * 
	 * @param key to remove
	 * @return the removed CommonTag, or null if nothing was removed
	 */
	public CommonTag<?> remove(Object key) {
		if (key == null) {
			return null;
		}
		return create(getRawData().remove(key.toString()));
	}

	@Override
	public CommonTag<?> put(String key, CommonTag<?> value) {
		final CommonTag<?> prev = get(key);
		putData(key, value);
		return prev;
	}

	/**
	 * Sets the data representing a given key<br>
	 * Use a data of null to remove the data at the key<br>
	 * If the data is an NBT Tag or Common Tag, the tag is directly set (not it's data)
	 * 
	 * @param key to set
	 * @param data to assign to this key
	 * @throws IllegalArgumentException if the value can not be stored
	 */
	public void putData(String key, Object data) {
		if (data == null) {
			handle.remove(key);
		} else if (data instanceof CommonTag) {
			handle.set(key, (NBTBase) ((CommonTag<?>) data).getHandle());
		} else if (data instanceof NBTBase) {
			handle.set(key, (NBTBase) data);
		} else {
			handle.set(key, (NBTBase) NBTUtil.createHandle(key, data));
		}
	}

	/**
	 * Gets an UUID value from two previously-stored keys<br>
	 * If the key is 'Entity' then two long values 'EntityUUIDMost' and 'EntityUUIDLeast' are read<br>
	 * If either of the two values are missing, null is returned instead
	 * 
	 * @param key to read
	 * @return data at the key
	 */
	public UUID getUUID(String key) {
		Object most = getData(key + "UUIDMost");
		Object least = getData(key + "UUIDLeast");
		if (most instanceof Long && least instanceof Long) {
			return new UUID((Long) most, (Long) least);
		} else {
			return null;
		}
	}

	/**
	 * Puts an UUID value by storing it under two keys<br>
	 * If the key is 'Entity' then two long values 'EntityUUIDMost' and 'EntityUUIDLeast' are stored<br>
	 * If the data is null, the elements at the key are removed
	 * 
	 * @param key to put at
	 * @param data to put
	 */
	public void putUUID(String key, UUID data) {
		if (data == null) {
			handle.remove(key + "UUIDMost");
			handle.remove(key + "UUIDLeast");
			return;
		}
		handle.setLong(key + "UUIDMost", data.getMostSignificantBits());
		handle.setLong(key + "UUIDLeast", data.getLeastSignificantBits());
	}

	/**
	 * Gets the value associated with a key<br>
	 * This is the value, not the tag itself<br>
	 * Returns null if no value is contained
	 * 
	 * @param key to get
	 * @return value of the tag at the key
	 */
	public Object getValue(String key) {
		if (key == null) {
			return null;
		}
		return getData(handle.get(key));
	}

	/**
	 * Gets the value associated with a key<br>
	 * This is the value, not the tag itself<br>
	 * Returns the def value if no value is contained
	 * 
	 * @param key to get
	 * @param def value to return when not found (can not be null)
	 * @return value of the tag at the key
	 */
	public <T> T getValue(String key, T def) {
		return ParseUtil.convert(getValue(key), def);
	}

	/**
	 * Gets the value associated with a key<br>
	 * This is the value, not the tag itself<br>
	 * Returns null if no value is contained
	 * 
	 * @param key to get
	 * @param type to case the value to
	 * @return value of the tag at the key
	 */
	public <T> T getValue(String key, Class<T> type) {
		return ParseUtil.convert(getValue(key), type);
	}

	/**
	 * Gets the value associated with a key<br>
	 * This is the value, not the tag itself
	 * 
	 * @param key to get
	 * @return value of the tag at the key
	 */
	public <T> T getValue(String key, Class<T> type, T def) {
		return ParseUtil.convert(getValue(key), type, def);
	}

	/**
	 * Gets the tag representation of a given key
	 * 
	 * @param key to get
	 * @return Tag at the key, or null if not contained
	 */
	@Override
	public CommonTag<?> get(Object key) {
		if (key == null) {
			return null;
		}
		return create(handle.get(key.toString()));
	}

	@Override
	public void clear() {
		getRawData().clear();
	}

	@Override
	public int size() {
		return getRawData().size();
	}

	@Override
	public boolean isEmpty() {
		return handle.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return handle.hasKey(key.toString());
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
			return false;
		} else if (value instanceof CommonTag<?>) {
			value = ((CommonTag<?>) value).getHandle();
		}
		if (value instanceof NBTBase) {
			// Compare NBT elements
			return getHandleValues().contains(value);
		} else {
			// Compare the data of the NBT elements
			for (Object base : getHandleValues()) {
				if (NBTUtil.getData(base).equals(value)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void putAll(Map<? extends String, ? extends CommonTag<?>> m) {
		for (Entry<? extends String, ? extends CommonTag<?>> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Set<String> keySet() {
		return getRawData().keySet();
	}

	@Override
	public Collection<CommonTag<?>> values() {
		return Collections.unmodifiableCollection((Collection<CommonTag<?>>) nbtToCommon(getHandleValues()));
	}

	@Override
	public Set<Entry<String, CommonTag<?>>> entrySet() {
		return Collections.unmodifiableSet((Set<Entry<String, CommonTag<?>>>) nbtToCommon(getRawData()));
	}
	
	/**
	 * Writes this CommonTagCompound to the OutputStream specified
	 * 
	 * @param stream to write to
	 * @throws IOException
	 */
	public void writeTo(OutputStream stream) throws IOException {
		NBTUtil.writeCompound(getHandle(), stream);
	}

	/**
	 * Reads a CommonTagCompound from the InputStream specified
	 * 
	 * @param stream to read from
	 * @return read compound
	 * @throws IOException
	 */
	public static CommonTagCompound readFrom(InputStream stream) throws IOException {
		return (CommonTagCompound) create(NBTUtil.readCompound(stream));
	}
}
