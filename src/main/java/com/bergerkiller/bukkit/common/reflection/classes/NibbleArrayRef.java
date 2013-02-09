package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

import net.minecraft.server.v1_4_R1.NibbleArray;

public class NibbleArrayRef {
	public static int rawAppend(NibbleArray nibble, int rawLength, byte[] rawBuffer) {
		if(!Common.spigot) {
			FieldAccessor<byte[]> data = new SafeField<byte[]>(nibble, "a");
			rawLength = appendArray(data.get(nibble), rawLength, rawBuffer);
		} else
			rawLength = nibble.copyToByteArray(rawBuffer, rawLength);
		
		return rawLength;
	}
	
	private static int appendArray(byte[] data, int rawLength, byte[] rawBuffer) {
		System.arraycopy(data, 0, rawBuffer, rawLength, data.length);
		return rawLength + data.length;
	}
}
