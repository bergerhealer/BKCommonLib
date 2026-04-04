package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.config.TempFileOutputStream;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.ListTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NbtIoHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;

import java.io.*;
import java.util.*;

/**
 * An NBTTagCompound wrapper which is used to map values to keys<br>
 * See also the {@link CommonTag} class description for more information
 */
@SuppressWarnings("unchecked")
public class CommonTagCompound extends CommonTag implements Map<String, CommonTag> {

    /**
     * A read-only EMPTY tag compound. This tag cannot be modified! Is returned
     * as a constant when reading the custom data of items that don't have any.
     */
    public static final CommonTagCompound EMPTY = makeReadOnly(new CommonTagCompound());

    public CommonTagCompound() {
        this(new HashMap<String, CommonTag>());
    }

    public CommonTagCompound(Map<String, ?> data) {
        this((Object) data);
    }

    protected CommonTagCompound(Object data) {
        super(data);
    }

    public CommonTagCompound(CompoundTagHandle handle) {
        super(handle);
    }

    @Override
    public CompoundTagHandle getBackingHandle() {
        return (CompoundTagHandle) handle;
    }

    @Override
    public CommonTagCompound clone() {
        return new CommonTagCompound(handle.clone());
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
        return getRawData().values();
    }

    /**
     * Removes and returns the value contained at a given key.<br>
     * <br>
     * Possible returned
     * types:<br>
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, boolean, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param key of the element to remove
     * @return the removed element value, or null if nothing was removed
     */
    public Object removeValue(String key) {
        if (key == null) {
            return null;
        }
        assertWritable();
        Object removedHandle = getRawData().remove(key);
        return removedHandle == null ? null : wrapGetDataForHandle(removedHandle, readOnly);
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
        assertWritable();
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
        } else if (type == IdentifierHandle.class) {
            // == IdentifierHandle ==
            String v = putGetRemove(op, key, String.class, (value == null) ? null : ((IdentifierHandle) value).toString());
            if (v != null) {
                return (T) IdentifierHandle.createNew(v);
            }
        } else if (type == ChatText.class) {
            // Encoded / decodes NBT data
            CommonTag result = putGetRemove(op, key, CommonTag.class, (value == null) ? null : ((ChatText) value).getNBT());
            if (result != null) {
                return (T) ChatText.fromNBT(result);
            }
        } else if (type == boolean.class || type == Boolean.class) {
            // == Booleans (serialized as Byte) ==
            Byte v = putGetRemove(op, key, Byte.class, (value == null) ? null : ((Boolean) value) ? (byte) 1 : (byte) 0);
            if (v != null) {
                return (T) ((v.byteValue() != (byte) 0) ? Boolean.TRUE : Boolean.FALSE);
            }
        } else if (op == PutGetRemoveOp.GET) {
            // Get other types of values
            rawNBTResult = CompoundTagHandle.T.get.raw.invoke(getRawHandle(), key);
        } else if (op == PutGetRemoveOp.REMOVE || (op == PutGetRemoveOp.PUT && value == null)) {
            // Remove other types of values
            assertWritable();
            rawNBTResult = getRawData().remove(key);
        } else if (op == PutGetRemoveOp.PUT) {
            // Put other types of values
            assertWritable();
            Object putValueNBT = TagHandle.createRawHandleForData(value);
            rawNBTResult = CompoundTagHandle.T.put.raw.invoke(getRawHandle(), key, putValueNBT);
        }

        // Failure fallback + convert raw NBT tags to their converted values
        if (rawNBTResult == null) {
            return null; // failure or no previous value
        } else if (type == null) {
            return (T) wrapGetDataForHandle(rawNBTResult, readOnly);
        } else if (TagHandle.class.isAssignableFrom(type)) {
            return Conversion.convert(TagHandle.createHandleForData(rawNBTResult), type, null);
        } else if (CommonTag.class.isAssignableFrom(type)) {
            CommonTag commonTag = TagHandle.createHandleForData(rawNBTResult).toCommonTag();
            commonTag.readOnly = this.readOnly;
            return Conversion.convert(commonTag, type, null);
        } else if (TagHandle.T.isAssignableFrom(type)) {
            return Conversion.convert(rawNBTResult, type, null);
        } else if (CompoundTagHandle.T.isAssignableFrom(rawNBTResult)) {
            return Conversion.convert(
                    wrapRawData(CompoundTagHandle.T.data.get(rawNBTResult), readOnly),
                    type, null);
        } else if (ListTagHandle.T.isAssignableFrom(rawNBTResult)) {
            return Conversion.convert(
                    wrapRawData(ListTagHandle.T.data.get(rawNBTResult), readOnly),
                    type, null);
        } else {
            return Conversion.convert(TagHandle.getDataForHandle(rawNBTResult), type, null);
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
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, boolean, short, int, long,
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
     * <u>CommonTag, NBTBase, List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, boolean,
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
     * <u>CommonTag, NBTBase, List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte,
     * boolean, short, int, long, float, double, byte[], int[],
     * String, UUID*, BlockLocation*, IntVector3*, other**</u><br>
     * <br>
     * <i>* these types do not support removing, use {@link #removeValue(String, Class)}
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
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, boolean, short, int, long,
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
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, boolean, short, int, long,
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
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, boolean, short, int, long,
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
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, boolean, short, int, long,
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
     * Gets the MinecraftKey Handle value stored at a key as a String, decoding it into
     * a MinecraftKey.
     * 
     * @param key to read
     * @return value at the key
     */
    public IdentifierHandle getMinecraftKey(String key) {
        return getValue(key, IdentifierHandle.class);
    }

    /**
     * Puts a MinecraftKey Handle value at a key, converting it to a String when stored.
     * 
     * @param key to put at
     * @param minecraftKey value to put
     */
    public void putMinecraftKey(String key, IdentifierHandle minecraftKey) {
        putValue(key, IdentifierHandle.class, minecraftKey);
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
     * Obtains a CommonTagCompound stored at the key. If it does not exist, returns
     * the read-only {@link #EMPTY} tag. Should only be used for reading NBT contents.
     *
     * @param key to get at
     * @return CommonTagCompound, or {@link #EMPTY} if not found
     */
    public CommonTagCompound getCompoundOrEmpty(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Can not store elements under null keys");
        }
        Object handle = getRawData().get(key);
        if (CompoundTagHandle.T.isAssignableFrom(handle)) {
            CommonTagCompound tag = CommonTagCompound.create(CompoundTagHandle.createHandle(handle));
            tag.readOnly = this.readOnly;
            return tag;
        } else {
            return EMPTY;
        }
    }

    /**
     * Obtains a CommonTagCompound stored at the key, if it does not exist a new one is
     * created at the key. If this tag is read-only, throws an exception if the tag
     * does not exist.
     *
     * @param key to get or create at
     * @return CommonTagCompound
     */
    public CommonTagCompound createCompound(Object key) {
        CommonTagCompound tag = getCompoundOrEmpty(key);
        if (tag == EMPTY) {
            assertWritable();
            tag = new CommonTagCompound();
            getRawData().put(key.toString(), tag.getRawHandle());
        }
        return tag;
    }

    /**
     * Obtains a CommonTagList stored at the key. If it does not exist, returns
     * the read-only {@link CommonTagList#EMPTY} tag. Should only be used for reading NBT contents.
     *
     * @param key to get at
     * @return CommonTagList, or {@link CommonTagList#EMPTY} if not found
     */
    public CommonTagList getListOrEmpty(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Can not store elements under null keys");
        }
        Object handle = getRawData().get(key);
        if (ListTagHandle.T.isAssignableFrom(handle)) {
            CommonTagList list = CommonTagList.create(ListTagHandle.createHandle(handle));
            list.readOnly = this.readOnly;
            return list;
        } else {
            return CommonTagList.EMPTY;
        }
    }

    /**
     * Obtains a CommonTagList at the key, if it does not exist a new one is
     * created at the key. If this tag is read-only, throws an exception if the tag
     * does not exist.
     *
     * @param key to get or create at
     * @return CommonTagList
     */
    public CommonTagList createList(Object key) {
        CommonTagList tag = getListOrEmpty(key);
        if (tag == CommonTagList.EMPTY) {
            assertWritable();
            tag = new CommonTagList();
            getRawData().put(key.toString(), tag.getRawHandle());
        }
        return tag;
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
        TagHandle handle = getBackingHandle().get(key.toString());
        return (handle == null) ? null : handle.toCommonTag();
    }

    @Override
    public CommonTag put(String key, CommonTag value) {
        return putGetRemove(PutGetRemoveOp.PUT, key, CommonTag.class, value);
    }

    @Override
    public void clear() {
        assertWritable();
        getRawData().clear();
    }

    @Override
    public int size() {
        return getBackingHandle().size();
    }

    @Override
    public boolean isEmpty() {
        return getBackingHandle().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return key != null && getBackingHandle().containsKey(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof TagHandle) {
            value = ((TagHandle) value).getRaw();
        } else if (value instanceof CommonTag) {
            value = ((CommonTag) value).getRawHandle();
        }
        if (TagHandle.T.isAssignableFrom(value)) {
            // Compare NBT elements
            return getHandleValues().contains(value);
        } else {
            // Compare the data of the NBT elements
            for (Object base : getHandleValues()) {
                if (TagHandle.getDataForHandle(base).equals(value)) {
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
        return getData().values();
    }

    @Override
    public Set<Entry<String, CommonTag>> entrySet() {
        return getData().entrySet();
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
    	    NbtIoHandle.compressed_writeTagCompound(getBackingHandle(), out);
    	} else if (out instanceof DataOutput) {
    	    NbtIoHandle.uncompressed_writeTagCompound(getBackingHandle(), (DataOutput) out);
    	} else {
            NbtIoHandle.uncompressed_writeTagCompound(getBackingHandle(), new DataOutputStream(out));
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
        CompoundTagHandle handle;
        if (compressed) {
            handle = NbtIoHandle.compressed_readTagCompound(in);
        } else if (in instanceof DataInput) {
            handle = NbtIoHandle.uncompressed_readTagCompound((DataInput) in);
        } else {
            handle = NbtIoHandle.uncompressed_readTagCompound(new DataInputStream(in));
        }
        return (handle == null) ? null : new CommonTagCompound(handle);
    }

    /**
     * Attempts to decode a legacy base64-encoded NBT compound from the "internal" string format.
     * This format was used in the serialized ItemStack yaml output.
     *
     * @param internal ItemStack "internal" string
     * @return Decoded compound, or null if the string was null or not in the expected format or could not be decoded
     */
    public static CommonTagCompound fromBase64String(String internal) {
        if (internal == null) {
            return null;
        }

        CommonTagCompound nbt;
        try {
            byte[] compressedBytes = Base64.getDecoder().decode(internal);
            try (ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedBytes)) {
                return CommonTagCompound.readFromStream(byteStream, true);
            }
        } catch (IllegalArgumentException ex) {
            // Not base64?
            return null;
        } catch (Exception ex) {
            // Corrupted internal data, just throw whatever comes from that...
            return null;
        }
    }

    /**
     * Encodes this NBT compound to the base64 "internal" string format.
     * This format was used in the serialized ItemStack yaml output.
     *
     * @return Serialized base64 internal String
     */
    public String toBase64String() {
        try {
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
                writeToStream(byteStream, true);
                byteStream.flush();
                return Base64.getEncoder().encodeToString(byteStream.toByteArray());
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write NBT data to byte stream", ex);
        }
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
        return LogicUtil.tryCast(CommonTag.create(handle), CommonTagCompound.class);
    }

    /**
     * Creates an unmodifiable CommonTagCompound from the handle specified<br>
     * If the handle is null or not a compound, null is returned
     *
     * @param handle to create a compound wrapper class for
     * @return Wrapper class suitable for the given handle
     */
    public static CommonTagCompound createReadOnly(Object handle) {
        return makeReadOnly(create(handle));
    }

    /**
     * Parses a String in Mojang's json format into an NBT tag compound
     * 
     * @param mojangson
     * @return parsed compound, null if no tag could be parsed
     * @deprecated Use {@link #fromSNBT(String)} instead
     */
    @Deprecated
    public static CommonTagCompound fromMojangson(String mojangson) {
        return fromSNBT(mojangson).getResult();
    }

    /**
     * Parses a String in Mojang's <a href="https://minecraft.wiki/w/NBT_format#SNBT_format">SNBT format</a>
     * into an NBT tag compound.
     *
     * @param snbtContent SNBT String
     * @return parsed compound, null if no full tag compound could be parsed
     */
    public static SNBTResult<CommonTagCompound> fromSNBT(String snbtContent) {
        try {
            CompoundTagHandle result = NbtIoHandle.parseTagCompoundFromSNBT(snbtContent);
            return SNBTResult.success(result.toCommonTag());
        } catch (Throwable t) {
            return SNBTResult.error(NbtIoHandle.handleSNBTParseError(snbtContent, t));
        }
    }
}
