package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.level.chunk.NibbleArrayHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class NMSNibbleArray {
    public static final ClassTemplate<?> T = ClassTemplate.create(NibbleArrayHandle.T.getType());
    public static final FieldAccessor<byte[]> array = NibbleArrayHandle.T.dataField.toFieldAccessor();

    /**
     * Copies all data contained in the Nibble Array to the byte array specified
     *
     * @param nibbleArray to get the data from
     * @param destArray to copy to
     * @param offset in the array to copy at
     * @return The offset added to the size of the Nibble Array
     */
    public static int copyTo(Object nibbleArray, byte[] destArray, int offset) {
        byte[] data = array.get(nibbleArray);
        System.arraycopy(data, 0, destArray, offset, data.length);
        return data.length + offset;
    }

    /**
     * Obtains a new byte array of the contents of the nibble array
     *
     * @param nibbleArray to get te copied array from
     * @return copied, unreferenced array
     */
    public static byte[] getArrayCopy(Object nibbleArray) {
    	byte[] data = array.get(nibbleArray);
        byte[] rval = new byte[data.length];
        copyTo(nibbleArray, rval, 0);
        return rval;
    }

    /**
     * Obtains a reference to the byte[] contents of a Nibble Array. These
     * contents are unsafe for modification, do not change elements of it!
     *
     * @param nibbleArray to get the referenced byte array contents of
     * @return value array
     */
    public static byte[] getValueArray(Object nibbleArray) {
        return array.get(nibbleArray);
    }
}
