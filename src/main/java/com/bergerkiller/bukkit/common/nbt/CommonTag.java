package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NBTCompressedStreamToolsHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NBTTagCompoundHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
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
    // Guards against modifications when a read-only NBT object is requested
    boolean readOnly = false;

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

    void assertWritable() {
        if (readOnly) {
            throw new UnsupportedOperationException("This NBT Tag is read-only");
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
        return wrapRawData(getRawData(), readOnly);
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

    protected static Object wrapGetDataForHandle(Object nmsNBTHandle, boolean readOnly) {
        return wrapRawData(NBTBaseHandle.getDataForHandle(nmsNBTHandle), readOnly);
    }

    /**
     * Wraps Map and List data to expose CommonTag objects rather than NBTBase.
     * Other types are returned as-is.
     * 
     * @param value Value to wrap
     * @param readOnly Whether any CommonTags should be wrapped as read-only
     * @return wrapped value
     */
    protected static Object wrapRawData(Object value, boolean readOnly) {
        if (value instanceof Map) {
            // Convert Map<String, NBTBase> to Map<String, CommonTag>
            return new ConvertingMap<String, CommonTag>((Map<?, ?>) value, DuplexConversion.string_string, nbtBaseToCommonTag(readOnly));
        } else if (value instanceof List) {
            // Convert List<NBTBase> to List<CommonTag>
            return new ConvertingList<CommonTag>((List<?>) value, nbtBaseToCommonTag(readOnly));
        } else {
            // Tag data value type does not require further conversion
            return value;
        }
    }

    static DuplexConverter<Object, CommonTag> nbtBaseToCommonTag(boolean readOnly) {
        return readOnly ? DuplexConversion.nbtBase_commonTag_readOnly : DuplexConversion.nbtBase_commonTag;
    }

    /**
     * Makes a tag read-only. Remove and put operations will no longer work.
     *
     * @param tag NBT Tag
     * @return Same input tag, but with read-only mode enabled
     */
    public static <T extends CommonTag> T makeReadOnly(T tag) {
        if (tag != null) {
            tag.readOnly = true;
        }
        return tag;
    }

    /**
     * Parses a String in Mojang's <a href="https://minecraft.wiki/w/NBT_format#SNBT_format">SNBT format</a>
     * into an NBT tag. This supports any type of tag, including numbers, lists and more.
     *
     * @param snbtContent SNBT String
     * @return parsed tag, null if no full tag could be parsed
     */
    public static SNBTResult<? extends CommonTag> fromSNBT(String snbtContent) {
        try {
            NBTBaseHandle result = NBTCompressedStreamToolsHandle.parseTagFromSNBT(snbtContent);

            // Sadly, it wraps it as NBTBaseHandle, not the derived type more suitable for what is returned
            // This is fixed by creating the proper handle using createHandleForData()
            return SNBTResult.success(NBTBaseHandle.createHandleForData(result.getRaw()).toCommonTag());
        } catch (Throwable t) {
            t.printStackTrace();
            return SNBTResult.error(NBTCompressedStreamToolsHandle.handleSNBTParseError(snbtContent, t));
        }
    }

    /**
     * The result of parsing Mojang's <a href="https://minecraft.wiki/w/NBT_format#SNBT_format">SNBT format</a>
     * into a NBT Tag. If parsing fails, the parsing error is included in the result.
     *
     * @param <T> CommonTag type (if known)
     */
    public static class SNBTResult<T extends CommonTag> {
        private final T result;
        private final String errorMessage;

        public static <T extends CommonTag> SNBTResult<T> error(String errorMessage) {
            return new SNBTResult<>(null, errorMessage);
        }

        public static <T extends CommonTag> SNBTResult<T> success(T tag) {
            return new SNBTResult<>(tag, null);
        }

        private SNBTResult(T result, String errorMessage) {
            this.result = result;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return result != null;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * Gets the parsed tag, or null if not successful
         *
         * @return Result
         */
        public T getResult() {
            return result;
        }

        @Override
        public String toString() {
            return isSuccess() ? "Success<" + getResult() + ">" : "Failure<" + getErrorMessage() + ">";
        }
    }
}
