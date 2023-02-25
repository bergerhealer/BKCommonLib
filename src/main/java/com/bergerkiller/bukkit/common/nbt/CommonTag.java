package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NBTCompressedStreamToolsHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.conversion.util.ConvertingMap;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * An NBT Tag wrapper implementation to safely operate on tags<br><br>
 * <p>
 * <b>Data</b> represents actual data stored by the tag. This can be:<br>
 * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, short, int, long, float,
 * double, byte[], int[], String</u>
 */
public class CommonTag extends BasicWrapper<NBTBaseHandle> implements Cloneable {

    public CommonTag(NBTBaseHandle handle) {
        setHandle(handle);
    }

    public CommonTag(Object data) {
        if (data instanceof NBTBaseHandle) {
            setHandle((NBTBaseHandle) data);
        } else {
            setHandle(NBTBaseHandle.createHandleForData(data));
        }
    }

    /**
     * Gets the raw data stored inside this tag
     * 
     * @return Raw data
     */
    protected Object getRawData() {
        return NBTBaseHandle.getDataForHandle(getRawHandle());
    }

    /**
     * Gets the data stored by this tag
     *
     * @return Tag data
     */
    public Object getData() {
        return wrapRawData(getRawData());
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

    @Override
    public String toString() {
        return handle.toPrettyString();
    }

    @Override
    public CommonTag clone() {
        return new CommonTag(handle.clone());
    }

    /**
     * Serializes and writes this tag to a stream. The data is uncompressed.
     *
     * @param out Stream to write to
     * @throws IOException
     */
    public void writeToStream(OutputStream out) throws IOException {
    	if (out instanceof DataOutput) {
    	    NBTCompressedStreamToolsHandle.uncompressed_writeTag(handle, (DataOutput) out);
    	} else {
    	    NBTCompressedStreamToolsHandle.uncompressed_writeTag(handle, new DataOutputStream(out));
    	}
    }

    /**
     * Deserializes and reads a tag from a stream. The input data should be uncompressed.
     * 
     * @param in Stream to read from
     * @return read tag
     * @throws IOException
     */
    public static CommonTag readFromStream(InputStream in) throws IOException {
        if (in instanceof DataInput) {
            return create(NBTCompressedStreamToolsHandle.uncompressed_readTag((DataInput) in));
        } else {
            return create(NBTCompressedStreamToolsHandle.uncompressed_readTag(new DataInputStream(in)));
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
        return NBTBaseHandle.createHandleForData(data).toCommonTag();
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
        } else {
            return NBTBaseHandle.createHandleForData(handle).toCommonTag();
        }
    }

    protected static Object wrapGetDataForHandle(Object nmsNBTHandle) {
        return wrapRawData(NBTBaseHandle.getDataForHandle(nmsNBTHandle));
    }

    /**
     * Wraps Map and List data to expose CommonTag objects rather than NBTBase.
     * Other types are returned as-is.
     * 
     * @param value
     * @return wrapped value
     */
    protected static Object wrapRawData(Object value) {
        if (value instanceof Map) {
            // Convert Map<String, NBTBase> to Map<String, CommonTag>
            return new ConvertingMap<String, CommonTag>((Map<?, ?>) value, DuplexConversion.string_string, DuplexConversion.nbtBase_commonTag);
        } else if (value instanceof List) {
            // Convert List<NBTBase> to List<CommonTag>
            return new ConvertingList<CommonTag>((List<?>) value, DuplexConversion.nbtBase_commonTag);
        } else {
            // Tag data value type does not require further conversion
            return value;
        }
    }
}
