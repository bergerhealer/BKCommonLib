package com.bergerkiller.bukkit.common.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.classes.NBTRef;
import com.bergerkiller.bukkit.common.utils.NBTUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;

import net.minecraft.server.v1_8_R1.*;

/**
 * An NBT Tag wrapper implementation to safely operate on tags<br><br>
 *
 * <b>Data</b> represents actual data stored by the tag. This can be:<br>
 * <u>List<CommonTag>, Map<String, CommonTag>, byte, short, int, long, float,
 * double, byte[], int[], String</u>
 */
public class CommonTag extends BasicWrapper {

    protected final NBTTagInfo info;

    public CommonTag(Object data) {
        info = NBTTagInfo.findInfo(data);
        setHandle(info.createHandle(commonToNbt(data)));
    }

    protected Object getRawData() {
        return info.getData(handle);
    }

    /**
     * Gets the data stored by this tag
     *
     * @return Tag data
     */
    public Object getData() {
        return nbtToCommon(getRawData(), false);
    }

    /**
     * Gets the data stored by this tag
     *
     * @param def value to return when no data is available (can not be null)
     * @return Tag data
     */
    public <T> T getData(T def) {
        return Conversion.convert(getData(), def);
    }

    /**
     * Gets the data stored by this tag
     *
     * @param type to convert the data to
     * @return Tag data
     */
    public <T> T getData(Class<T> type) {
        return Conversion.convert(getData(), type);
    }

    /**
     * Gets the data stored by this tag
     *
     * @param def value to return when no data is available (can not be null)
     * @return Tag data
     */
    public <T> T getData(Class<T> type, T def) {
        return Conversion.convert(getData(), type, def);
    }

    /**
     * Sets the data stored by the tag
     *
     * @param data to set to
     */
    public void setData(Object data) {
        info.setData(handle, commonToNbt(data));
    }

    @Override
    public String toString() {
        return info.toString(handle, 0);
    }

    @Override
    public CommonTag clone() {
        return create(NBTRef.clone.invoke(handle));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static Object commonToNbt(Object data) {
        if (data instanceof CommonTag) {
            return ((CommonTag) data).getHandle();
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
                Object value = commonToNbt(entry.getValue());
                tags.put(entry.getKey(), value);
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
            return NBTUtil.createHandle(data);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static Object nbtToCommon(Object data, boolean wrapData) {
        if (data instanceof NBTBase) {
            return create(data);
        } else if (data instanceof CommonTag || data == null) {
            return data;
        } else if (data instanceof Entry) {
            Entry old = (Entry) data;
            // Replace
            Object newKey = nbtToCommon(old.getKey(), wrapData);
            Object newValue = nbtToCommon(old.getValue(), wrapData);
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
                tags.add(nbtToCommon(value, wrapData));
            }
            return tags;
        } else if (data instanceof Map) {
            Map<String, Object> elems = (Map<String, Object>) data;
            HashMap<String, Object> tags = new HashMap<String, Object>(elems.size());
            // Replace
            for (Entry<String, Object> entry : elems.entrySet()) {
                tags.put(entry.getKey(), nbtToCommon(entry.getValue(), wrapData));
            }
            return tags;
        } else if (data instanceof Collection) {
            Collection<Object> elems = (Collection<Object>) data;
            ArrayList<Object> tags = new ArrayList<Object>(elems.size());
            // Replace
            for (Object value : elems) {
                tags.add(nbtToCommon(value, wrapData));
            }
            return tags;
        } else if (wrapData) {
            return createForData(data);
        } else {
            return data;
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
    public static CommonTag createForData(Object data) {
        return create(NBTUtil.createHandle(data));
    }

    /**
     * Creates a CommonTag from the handle specified<br>
     * If the handle is null, null is returned
     *
     * @param handle to create a wrapper class for
     * @return Wrapper class suitable for the given handle
     */
    public static CommonTag create(Object handle) {
        if (handle == null) {
            return null;
        }
        CommonTag tag;
        if (NBTRef.NBTTagCompound.isInstance(handle)) {
            tag = new CommonTagCompound(handle);
        } else if (NBTRef.NBTTagList.isInstance(handle)) {
            tag = new CommonTagList(handle);
        } else {
            tag = new CommonTag(handle);
        }
        return tag;
    }
}
