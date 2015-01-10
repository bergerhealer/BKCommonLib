package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeDirectMethod;

public class NibbleArrayRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("NibbleArray");
    private static final FieldAccessor<byte[]> array = TEMPLATE.getField("a");
    public static final FieldAccessor<Integer> bitCount = TEMPLATE.getField("b");
    private static final MethodAccessor<Integer> getByteLength;
    private static final MethodAccessor<Integer> copyToByteArray;
    private static final MethodAccessor<byte[]> getValueArray;

    static {
//		if (Common.IS_SPIGOT_SERVER) {
//			copyToByteArray = TEMPLATE.getMethod("copyToByteArray", byte[].class, int.class);
//			getValueArray = TEMPLATE.getMethod("getValueArray");
//			getByteLength = TEMPLATE.getMethod("getByteLength");
//		} else {
        copyToByteArray = new SafeDirectMethod<Integer>() {
            @Override
            public Integer invoke(Object instance, Object... args) {
                byte[] data = array.get(instance);
                byte[] rawBuffer = (byte[]) args[0];
                int rawLength = ((Integer) args[1]).intValue();
                System.arraycopy(data, 0, rawBuffer, rawLength, data.length);
                return Integer.valueOf(data.length + rawLength);
            }
        };
        getValueArray = new SafeDirectMethod<byte[]>() {
            @Override
            public byte[] invoke(Object instance, Object... args) {
                return array.get(instance);
            }
        };
        getByteLength = new SafeDirectMethod<Integer>() {
            @Override
            public Integer invoke(Object instance, Object... args) {
                return array.get(instance).length;
            }
        };
//		}
    }

    /**
     * Copies all data contained in the Nibble Array to the byte array specified
     *
     * @param nibbleArray to get the data from
     * @param array to copy to
     * @param offset in the array to copy at
     * @return The offset added to the size of the Nibble Array
     */
    public static int copyTo(Object nibbleArray, byte[] array, int offset) {
        return copyToByteArray.invoke(nibbleArray, array, offset).intValue();
    }

    /**
     * Obtains a new byte array of the contents of the nibble array
     *
     * @param nibbleArray to get te copied array from
     * @return copied, unreferenced array
     */
    public static byte[] getArrayCopy(Object nibbleArray) {
        byte[] rval = new byte[getByteLength.invoke(nibbleArray)];
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
        return getValueArray.invoke(nibbleArray);
    }
}
