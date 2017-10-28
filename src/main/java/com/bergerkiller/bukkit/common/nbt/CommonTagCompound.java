package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.config.TempFileOutputStream;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
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
        return NMSNBT.Compound.getValues.invoke(getRawHandle());
    }

    @Override
    public CommonTagCompound clone() {
        return (CommonTagCompound) super.clone();
    }

    /**
     * Removes and returns the value contained at a given key.<br>
     * <br>
     * Possible returned
     * types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, boolean, short, int, long,
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
        return CommonTag.create(getRawData().remove(key.toString()));
    }

    // implements get/put/remove with a simplified single implementation function
    private final <T> T putGetRemove(PutGetRemoveOp op, String key, Class<T> type, T value) {
        Object rawNBTResult = null;
        if (type == UUID.class) {
            // == UUID ==
            UUID uuid = (UUID) value;
            Long uuidMost = putGetRemove(op, key + "UUIDMost", Long.class, (uuid == null) ? null : uuid.getMostSignificantBits());
            Long uuidLeast = putGetRemove(op, key + "UUIDLeast", Long.class, (uuid == null) ? null : uuid.getLeastSignificantBits());
            if (uuidMost != null && uuidLeast != null) {
                return (T) new UUID(uuidMost.longValue(), uuidLeast.longValue());
            }
        } else if (type == BlockLocation.class) {
            // == BlockLocation ==
            BlockLocation pos = (BlockLocation) value;
            String world = putGetRemove(op, key + "World", String.class, (pos == null) ? null : pos.world);
            Integer x = putGetRemove(op, key + "X", Integer.class, (pos == null) ? null : pos.x);
            Integer y = putGetRemove(op, key + "Y", Integer.class, (pos == null) ? null : pos.y);
            Integer z = putGetRemove(op, key + "Z", Integer.class,  (pos == null) ? null : pos.z);
            if (world != null && !world.isEmpty() && x != null && y != null && z != null) {
                return (T) new BlockLocation(world, x.intValue(), y.intValue(), z.intValue());
            }
        } else if (type == IntVector3.class) {
            // == IntVector3 ==
            IntVector3 pos = (IntVector3) value;
            Integer x = putGetRemove(op, key + "X", Integer.class, (pos == null) ? null : pos.x);
            Integer y = putGetRemove(op, key + "Y", Integer.class, (pos == null) ? null : pos.y);
            Integer z = putGetRemove(op, key + "Z", Integer.class, (pos == null) ? null : pos.z);
            if (x != null && y != null && z != null) {
                return (T) new IntVector3(x.intValue(), y.intValue(), z.intValue());
            }
        } else if (type == boolean.class || type == Boolean.class) {
            // == Booleans (serialized as Byte) ==
            Byte v = putGetRemove(op, key, Byte.class, (value == null) ? null : ((Boolean) value) ? (byte) 1 : (byte) 0);
            if (v != null) {
                return (T) ((v.byteValue() != (byte) 0) ? Boolean.TRUE : Boolean.FALSE);
            }
        } else if (op == PutGetRemoveOp.GET) {
            // Get other types of values
            rawNBTResult = NMSNBT.Compound.get.invoke(getRawHandle(), key);
        } else if (op == PutGetRemoveOp.REMOVE || (op == PutGetRemoveOp.PUT && value == null)) {
            // Remove other types of values
            rawNBTResult = getRawData().remove(key);
        } else if (op == PutGetRemoveOp.PUT) {
            // Put other types of values
            Object elementHandle = commonToNbt(value);
            if (!NMSNBT.Base.T.isInstance(elementHandle)) {
                elementHandle = NMSNBT.createHandle(value);
            }
            rawNBTResult = getRawData().put(key, elementHandle);
        }

        // Failure fallback + convert raw NBT tags to their converted values
        if (rawNBTResult == null) {
            return null; // failure
        } else {
            Object nbtValue = nbtToCommon(NMSNBT.getData(rawNBTResult), false);
            return (type == null) ? (T) nbtValue : Conversion.convert(nbtValue, type, null);
        }
    }

    private static enum PutGetRemoveOp {
        PUT, GET, REMOVE
    }

    /**
     * Removes the value associated with a key. This returns the value of the tag, not
     * the tag itself. Returns null if no tag was contained.<br>
     * <br>
     * Possible returned
     * types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, boolean, short, int, long,
     * float, double, byte[], int[],
     * String, UUID, BlockLocation, IntVector3, other**</u><br>
     * <br>
     * <i>** these types are serialized from/to a stored String type.</i>
     *
     * @param key to remove
     * @param type to cast the value to (type of value to remove)
     * @return value of the tag at the key, null if not contained
     */
    public <T> T removeValue(String key, Class<T> type) {
        return putGetRemove(PutGetRemoveOp.REMOVE, key, type, null);
    }

    /**
     * Sets the value of the element representing a given key. Use a data of
     * null to remove the data at the key.<br>
     * <br>
     * Supported data types:<br>
     * <u>CommonTag, NBTBase, List<CommonTag>, Map<String, CommonTag>, byte, boolean,
     * short, int, long, float, double, byte[], int[],
     * String, UUID, BlockLocation, IntVector3, other**</u><br>
     * <br>
     * <i>** these types are serialized from/to a stored String type.</i>
     *
     * @param key of the element to set
     * @param type of value to set
     * @param value to assign to this key, null to remove it
     */
    public <T> T putValue(String key, Class<T> type, T value) {
        return putGetRemove(PutGetRemoveOp.PUT, key, type, value);
    }

    /**
     * Sets the value of the element representing a given key. Use a data of
     * null to remove the data at the key.<br>
     * <br>
     * Supported data types:<br>
     * <u>CommonTag, NBTBase, List<CommonTag>, Map<String, CommonTag>, byte,
     * boolean, short, int, long, float, double, byte[], int[],
     * String, UUID*, BlockLocation*, IntVector3*, other**</u><br>
     * <br>
     * <i>* these types do not support removing, use {@link #removeValue(key, type)}
     * or {@link #putValue(String, Class, Object)} instead</i><br>
     * <i>** these types are serialized from/to a stored String type.</i>
     *
     * @param key of the element to set
     * @param value to assign to this key
     */
    public void putValue(String key, Object value) {
        putGetRemove(PutGetRemoveOp.PUT, key, (value == null) ? null : (Class<Object>) value.getClass(), value);
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
     * the tag itself. Returns null if no tag is contained.<br>
     * <br>
     * Possible returned
     * types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, boolean, short, int, long,
     * float, double, byte[], int[],
     * String, UUID, BlockLocation, IntVector3, other**</u><br>
     * <br>
     * <i>** these types are serialized from/to a stored String type.</i>
     *
     * @param key to get
     * @param type to cast the value to (and type of value to get)
     * @return value of the tag at the key
     */
    public <T> T getValue(String key, Class<T> type) {
        return putGetRemove(PutGetRemoveOp.GET, key, type, null);
    }

    /**
     * Gets the value associated with a key. This is the value of the tag, not
     * the tag itself.<br>
     * <br>
     * Possible returned types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, boolean, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param key to get
     * @return value of the tag at the key
     */
    public Object getValue(String key) {
        return putGetRemove(PutGetRemoveOp.GET, key, null, null);
    }

    /**
     * Gets the value associated with a key. This is the value of the tag, not
     * the tag itself. Returns the default value if no tag is contained.<br>
     * <br>
     * Possible returned types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, boolean, short, int, long,
     * float, double, byte[], int[], String, UUID, BlockLocation, IntVector3, other**</u><br>
     * <br>
     * <i>** these types are serialized from/to a stored String type.</i>
     *
     * @param key to get
     * @param type to case the value to
     * @param def value to return when not found
     * @return value of the tag at the key
     */
    public <T> T getValue(String key, Class<T> type, T def) {
        T result = putGetRemove(PutGetRemoveOp.GET, key, type, def);
        return (result == null) ? def : result;
    }

    /**
     * Gets the value associated with a key. This is the value of the tag, not
     * the tag itself. Returns the default value if no tag is contained.<br>
     * <br>
     * Possible returned types:<br>
     * <u>List<CommonTag>, Map<String, CommonTag>, byte, boolean, short, int, long,
     * float, double, byte[], int[], String, UUID, BlockLocation, IntVector3, other**</u><br>
     * <br>
     * <i>** these types are serialized from/to a stored String type.</i>
     *
     * @param key to get
     * @param def value to return when not found (can not be null)
     * @return value of the tag at the key
     */
    public <T> T getValue(String key, T def) {
        return getValue(key, (Class<T>) def.getClass(), def);
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
        T values = Conversion.convert(this.values(), type, null);
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
        return getValue(key, UUID.class);
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
        putValue(key, UUID.class, data);
    }

    /**
     * Gets a Block Location value from previously-stored keys. If the key is
     * 'Spawn' then the world 'SpawnWorld' and coordinates 'SpawnX', 'SpawnY' and
     * 'SpawnZ' are read. If some of the values are missing, null is returned instead
     *
     * @param key to read
     * @return value at the key
     */
    public BlockLocation getBlockLocation(String key) {
        return getValue(key, BlockLocation.class);
    }

    /**
     * Puts a Block Location value by storing it under a world and three coordinate keys.
     * If the key is 'Spawn', then 'SpawnWorld' and the coordinates 'SpawnX', 'SpawnY' and
     * 'SpawnZ' are stored. If the data is null, the elements at the key are removed.
     * 
     * @param key to put at
     * @param location to put
     */
    public void putBlockLocation(String key, BlockLocation location) {
        putValue(key, BlockLocation.class, location);
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
        return CommonTagCompound.create(elementHandle);
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
        return CommonTagList.create(elementHandle);
    }

    /**
     * Gets a certain type of tag from this Tag Compound
     *
     * @param key to get the tag at
     * @param type of tag to get
     * @return the tag, or null if this was not possible
     */
    public <T extends CommonTag> T get(Object key, Class<T> type) {
        return Conversion.convert(get(key), type, null);
    }

    @Override
    public CommonTag get(Object key) {
        if (key == null) {
            return null;
        }
        return CommonTag.create(NMSNBT.Compound.get.invoke(getRawHandle(), key.toString()));
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
        return NMSNBT.Compound.isEmpty.invoke(getRawHandle());
    }

    @Override
    public boolean containsKey(Object key) {
        return NMSNBT.Compound.contains.invoke(getRawHandle(), key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof CommonTag) {
            value = ((CommonTag) value).getRawHandle();
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
    		NMSNBT.StreamTools.Compressed.writeTagCompound.invoke(null, getRawHandle(), out);
    	} else {
        	if (!(out instanceof DataOutput)) {
        		out = new DataOutputStream(out);
        	}
        	NMSNBT.StreamTools.Uncompressed.writeTagCompound.invoke(null, getRawHandle(), out);
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
        return CommonTagCompound.create(handle);
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

    /**
     * Creates a CommonTagCompound from the handle specified<br>
     * If the handle is null or not a compound, null is returned
     *
     * @param handle to create a compound wrapper class for
     * @return Wrapper class suitable for the given handle
     */
    public static CommonTagCompound create(Object handle) {
        return CommonUtil.tryCast(CommonTag.create(handle), CommonTagCompound.class);
    }
}
