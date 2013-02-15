package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeDirectMethod;

public class NibbleArrayRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("NibbleArray");
	public static final FieldAccessor<byte[]> array = TEMPLATE.getField("a");
	private static final MethodAccessor<Integer> copyToByteArray;
	static {
		if (Common.IS_SPIGOT_SERVER) {
			copyToByteArray = TEMPLATE.getMethod("copyToByteArray", byte[].class, int.class);
		} else {
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
		}
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
}
