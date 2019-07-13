package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.server.NBTBaseHandle;
import com.bergerkiller.reflection.net.minecraft.server.NMSNBT;

import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

/**
 * An NBT Tag wrapper implementation to safely operate on tags<br><br>
 * <p/>
 * <b>Data</b> represents actual data stored by the tag. This can be:<br>
 * <u>List<CommonTag>, Map<String, CommonTag>, byte, short, int, long, float,
 * double, byte[], int[], String</u>
 */
public class CommonTag extends BasicWrapper<NBTBaseHandle> implements Cloneable {
    protected final NMSNBT.Type info;

    public CommonTag(Object data) {
    	info = NMSNBT.Type.find(data);
        setHandle(NBTBaseHandle.createHandle(info.createHandle(commonToNbt(data))));
    }

    /**
     * Gets the NBT Type used in this Common Tag
     * 
     * @return NBT Type
     */
    public NMSNBT.Type getType() {
    	return info;
    }

    /**
     * Gets the raw data stored inside this tag
     * 
     * @return Raw data
     */
    protected Object getRawData() {
        return info.getData(getRawHandle());
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
        return Conversion.convert(getData(), type, null);
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
        info.setData(getRawHandle(), commonToNbt(data));
    }

    @Override
    public String toString() {
        return info.toString(getRawHandle(), 0);
    }

    @Override
    public CommonTag clone() {
        return create(NMSNBT.Base.clone.invoke(getRawHandle()));
    }

    /**
     * Serializes and writes this tag to a stream. The data is uncompressed.
     *
     * @param out Stream to write to
     * @throws IOException
     */
    public void writeToStream(OutputStream out) throws IOException {
    	if (!(out instanceof DataOutput)) {
    		out = new DataOutputStream(out);
    	}
        NMSNBT.StreamTools.Uncompressed.writeTag.invoke(null, getRawHandle(), out);
    }

    /**
     * Deserializes and reads a tag from a stream. The input data should be uncompressed.
     * 
     * @param in Stream to read from
     * @return read tag
     * @throws IOException
     */
    public static CommonTag readFromStream(InputStream in) throws IOException {
    	if (!(in instanceof DataInput)) {
    		in = new DataInputStream(in);
    	}
    	Object limiter = NMSNBT.StreamTools.Uncompressed.getNoReadLimiter();
    	Object handle = NMSNBT.StreamTools.Uncompressed.readTag.invoke(null, in, 0, limiter);
    	return create(handle);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static Object commonToNbt(Object data) {
        if (data instanceof CommonTag) {
            return ((CommonTag) data).getRawHandle();
        } else if (NMSNBT.Base.T.isInstance(data) || data == null) {
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
        } else if (NMSNBT.Type.canStore(data)) {
            return NMSNBT.createHandle(data);
        } else {
            String dataAsString = Conversion.toString.convert(data);
            if (dataAsString == null) {
                throw new IllegalArgumentException("Value of type " + data.getClass() +
                        " can not be serialized as a String");
            }
            return NMSNBT.createHandle(dataAsString);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static Object nbtToCommon(Object data, boolean wrapData) {
        if (NMSNBT.Base.T.isInstance(data)) {
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
     * @param data to store
     * @return a new CommonTag instance
     */
    public static CommonTag createForData(Object data) {
        return create(NMSNBT.createHandle(data));
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
        if (NMSNBT.Compound.T.isInstance(handle)) {
            tag = new CommonTagCompound(handle);
        } else if (NMSNBT.List.T.isInstance(handle)) {
            tag = new CommonTagList(handle);
        } else {
            tag = new CommonTag(handle);
        }
        return tag;
    }
}
