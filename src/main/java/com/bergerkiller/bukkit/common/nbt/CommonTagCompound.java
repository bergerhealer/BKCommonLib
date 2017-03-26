package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.config.TempFileOutputStream;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.reflection.net.minecraft.server.NMSNBT;

import java.io.*;
import java.util.*;

/**
 * An NBTTagCompound wrapper which is used to map values to keys<br>
 * See also the {@link CommonTag} class description for more information
 */
@SuppressWarnings("unchecked")
public class CommonTagCompound extends CommonTag implements Map<String, CommonTag> {

    public CommonTagCompound() {
        this(new HashMap<String, CommonTag>());
    }

    public CommonTagCompound(Map<String, ?> data) {
        this((Object) data);
    }

    protected CommonTagCompound(Object data) {
        super(data);
    }

    @Override
    public Map<String, CommonTag> getData() {
        return (Map<String, CommonTag>) super.getData();
    }

    @Override
    protected Map<String, Object> getRawData() {
        return (Map<String, Object>) super.getRawData();
    }

    private Collection<?> getHandleValues() {
        return NMSNBT.Compound.getValues.invoke(handle);
    }

    @Override
    public CommonTagCompound clone() {
        return (CommonTagCompound) super.clone();
    }

    /**
     * Removes and returns the value contained at a given key. Possible returned
     * types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param key of the element to remove
     * @return the removed element value, or null if nothing was removed
     */
    public Object removeValue(String key) {
        if (key == null) {
            return null;
        }
        Object removedHandle = getRawData().remove(key);
        return removedHandle == null ? null : nbtToCommon(NMSNBT.getData(removedHandle), false);
    }

    /**
     * Removes and returns the tag contained at a given key.
     *
     * @param key of the element to remove
     * @return the removed CommonTag, or null if nothing was removed
     */
    @Override
    public CommonTag remove(Object key) {
        if (key == null) {
            return null;
        }
        return create(getRawData().remove(key.toString()));
    }

    /**
     * Sets the value of the element representing a given key. Use a data of
     * null to remove the data at the key. Supported data types:<br>
     * <u>CommonTag, NBTBase, List<CommonTag>, Map<String, CommonTag>, byte,
     * short, int, long, float, double, byte[], int[], String</u>
     *
     * @param key of the element to set
     * @param value to assign to this key
     */
    public void putValue(String key, Object value) {
        if (value == null) {
            NMSNBT.Compound.remove.invoke(handle, key);
        } else {
            Object elementHandle = commonToNbt(value);
            if (!NMSNBT.Base.T.isInstance(elementHandle)) {
                elementHandle = NMSNBT.createHandle(value);
            }
            NMSNBT.Compound.set.invoke(handle, key, elementHandle);
        }
    }

    /**
     * Puts a new Tag list at the key, filled with the values specified
     *
     * @param key to put at
     * @param values to set the elements of the list to
     */
    public <T> void putListValues(String key, T... values) {
        final CommonTagList list = new CommonTagList();
        list.setAllValues(values);
        this.put(key, list);
    }

    /**
     * Gets the value associated with a key. This is the value of the tag, not
     * the tag itself. Possible returned types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param key to get
     * @return value of the tag at the key
     */
    public Object getValue(String key) {
        if (key == null) {
            return null;
        }
        return nbtToCommon(NMSNBT.getData(NMSNBT.Compound.get.invoke(handle, key)), false);
    }

    /**
     * Gets the value associated with a key. This is the value of the tag, not
     * the tag itself. Returns the default value if no tag is contained.
     * Possible returned types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param key to get
     * @param def value to return when not found (can not be null)
     * @return value of the tag at the key
     */
    public <T> T getValue(String key, T def) {
        return Conversion.convert(getValue(key), def);
    }

    /**
     * Gets the value associated with a key. This is the value of the tag, not
     * the tag itself. Returns null if no tag is contained. Possible returned
     * types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param key to get
     * @param type to case the value to
     * @return value of the tag at the key
     */
    public <T> T getValue(String key, Class<T> type) {
        return Conversion.convert(getValue(key), type);
    }

    /**
     * Gets the value associated with a key. This is the value of the tag, not
     * the tag itself. Returns the default value if no tag is contained.
     * Possible returned types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param key to get
     * @param type to case the value to
     * @param def value to return when not found
     * @return value of the tag at the key
     */
    public <T> T getValue(String key, Class<T> type, T def) {
        return Conversion.convert(getValue(key), type, def);
    }

    /**
     * Gets all values contained in this CommonTagCompound and casts it to the
     * type specified<br>
     * Lists, Sets and arrays (also primitive) are supported for types
     *
     * @param type to cast to
     * @return all data contained
     */
    public <T> T getAllValues(Class<T> type) {
        T values = Conversion.convert(this.values(), type);
        if (values == null) {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        } else {
            return values;
        }
    }

    /**
     * Gets an UUID value from two previously-stored keys. If the key is
     * 'Entity' then two long values 'EntityUUIDMost' and 'EntityUUIDLeast' are
     * read. If either of the two values are missing, null is returned instead
     *
     * @param key to read
     * @return value at the key
     */
    public UUID getUUID(String key) {
        Object most = getValue(key + "UUIDMost");
        Object least = getValue(key + "UUIDLeast");
        if (most instanceof Long && least instanceof Long) {
            return new UUID((Long) most, (Long) least);
        } else {
            return null;
        }
    }

    /**
     * Puts an UUID value by storing it under two keys. If the key is 'Entity'
     * then two long values 'EntityUUIDMost' and 'EntityUUIDLeast' are stored.
     * If the data is null, the elements at the key are removed
     *
     * @param key to put at
     * @param data to put
     */
    public void putUUID(String key, UUID data) {
        final Long most, least;
        if (data == null) {
            most = least = null;
        } else {
            most = data.getMostSignificantBits();
            least = data.getLeastSignificantBits();
        }
        putValue(key + "UUIDMost", most);
        putValue(key + "UUIDLeast", least);
    }

    /**
     * Obtains a CommonTagCompound at the key, if it does not exist a new one is
     * created at the key.
     *
     * @param key to get or create at
     * @return CommonTagCompound
     */
    public CommonTagCompound createCompound(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Can not store elements under null keys");
        }
        Object elementHandle = getRawData().get(key);
        if (!NMSNBT.Compound.T.isInstance(elementHandle)) {
            elementHandle = NMSNBT.Compound.T.newInstance();
            getRawData().put(key.toString(), elementHandle);
        }
        return (CommonTagCompound) create(elementHandle);
    }

    /**
     * Obtains a CommonTagList at the key, if it does not exist a new one is
     * created at the key.
     *
     * @param key to get or create at
     * @return CommonTagList
     */
    public CommonTagList createList(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Can not store elements under null keys");
        }
        Object elementHandle = getRawData().get(key);
        if (!NMSNBT.List.T.isInstance(elementHandle)) {
            elementHandle = NMSNBT.List.T.newInstance();
            getRawData().put(key.toString(), elementHandle);
        }
        return (CommonTagList) create(elementHandle);
    }

    /**
     * Gets a certain type of tag from this Tag Compound
     *
     * @param key to get the tag at
     * @param type of tag to get
     * @return the tag, or null if this was not possible
     */
    public <T extends CommonTag> T get(Object key, Class<T> type) {
        return Conversion.convert(get(key), type);
    }

    @Override
    public CommonTag get(Object key) {
        if (key == null) {
            return null;
        }
        return create(NMSNBT.Compound.get.invoke(handle, key.toString()));
    }

    @Override
    public CommonTag put(String key, CommonTag value) {
        final CommonTag prev = get(key);
        putValue(key, value);
        return prev;
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
        return NMSNBT.Compound.isEmpty.invoke(handle);
    }

    @Override
    public boolean containsKey(Object key) {
        return NMSNBT.Compound.contains.invoke(handle, key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof CommonTag) {
            value = ((CommonTag) value).getHandle();
        }
        if (NMSNBT.Base.T.isInstance(value)) {
            // Compare NBT elements
            return getHandleValues().contains(value);
        } else {
            // Compare the data of the NBT elements
            for (Object base : getHandleValues()) {
                if (NMSNBT.getData(base).equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends CommonTag> m) {
        for (Entry<? extends String, ? extends CommonTag> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<String> keySet() {
        return getRawData().keySet();
    }

    @Override
    public Collection<CommonTag> values() {
        return Collections.unmodifiableCollection((Collection<CommonTag>) nbtToCommon(getHandleValues(), true));
    }

    @Override
    public Set<Entry<String, CommonTag>> entrySet() {
        return Collections.unmodifiableSet((Set<Entry<String, CommonTag>>) nbtToCommon(getRawData(), true));
    }

    @Override
    public void writeToStream(OutputStream out) throws IOException {
    	writeToStream(out, false);
    }

    /**
     * Writes this Tag Compound to the stream specified. If compressed, the
     * data is compressed in GZIP format.
     * 
     * @param out Stream to write to
     * @param compressed Whether to compress the data with GZIP
     * @throws IOException on failure
     */
    public void writeToStream(OutputStream out, boolean compressed) throws IOException {
    	if (compressed) {
    		NMSNBT.StreamTools.Compressed.writeTagCompound.invoke(null, getHandle(), out);
    	} else {
        	if (!(out instanceof DataOutput)) {
        		out = new DataOutputStream(out);
        	}
        	NMSNBT.StreamTools.Uncompressed.writeTagCompound.invoke(null, getHandle(), out);
    	}
    }

    /**
     * Writes this CommonTagCompound to the file specified. First writes to a
     * temporary file to avoid corrupted files. If compressed, the
     * data is compressed in GZIP format.
     *
     * @param file to write to
     * @param compressed Whether to compress the data with GZIP
     * @throws IOException on failure
     */
    public void writeToFile(File file, boolean compressed) throws IOException {
        TempFileOutputStream stream = new TempFileOutputStream(file);
        boolean successful = false;
        try {
        	writeToStream(stream, compressed);
            successful = true;
        } finally {
            stream.close(successful);
        }
    }

    /**
     * Deserializes and reads a compound tag from a stream. The input data should be uncompressed.
     * 
     * @param in Stream to read from
     * @return read compound tag
     * @throws IOException
     */
    public static CommonTagCompound readFromStream(InputStream in) throws IOException {
    	return readFromStream(in, false);
    }

    /**
     * Reads a CommonTagCompound from the InputStream specified. If compressed, the
     * data is expected to be compressed with the GZIP format.
     *
     * @param in Stream to read from
     * @param compressed Whether to the data needs to be uncompressed with GZIP
     * @return read compound
     * @throws IOException on failure
     */
    public static CommonTagCompound readFromStream(InputStream in, boolean compressed) throws IOException {
    	Object handle;
    	if (compressed) {
    		handle = NMSNBT.StreamTools.Compressed.readTagCompound.invoke(null, in);
    	} else {
    		if (!(in instanceof DataInput)) {
    			in = new DataInputStream(in);
    		}
    		handle = NMSNBT.StreamTools.Uncompressed.readTagCompound.invoke(null, in);
    	}
        return (CommonTagCompound) create(handle);
    }

    /**
     * Reads a CommonTagCompound from the file specified. If compressed, the
     * data is expected to be compressed with the GZIP format.
     *
     * @param file to read from
     * @param compressed Whether to the data needs to be uncompressed with GZIP
     * @return read compound
     * @throws IOException on failure
     */
    public static CommonTagCompound readFromFile(File file, boolean compressed) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        try {
            return readFromStream(stream, compressed);
        } finally {
            stream.close();
        }
    }
}
