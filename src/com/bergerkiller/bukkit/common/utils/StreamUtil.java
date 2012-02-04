package com.bergerkiller.bukkit.common.utils;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.server.ChunkCoordinates;

public class StreamUtil {
	
	public static UUID readUUID(DataInputStream stream) throws IOException {
		return new UUID(stream.readLong(), stream.readLong());
	}
	public static void writeUUID(DataOutputStream stream, UUID uuid) throws IOException {
		stream.writeLong(uuid.getMostSignificantBits());
		stream.writeLong(uuid.getLeastSignificantBits());
	}
	public static ChunkCoordinates readCoordinates(DataInputStream stream) throws IOException {
		return new ChunkCoordinates(stream.readInt(), stream.readInt(), stream.readInt());
	}
	public static void writeCoordinates(DataOutputStream stream, ChunkCoordinates coordinates) throws IOException {
		stream.writeInt(coordinates.x);
		stream.writeInt(coordinates.y);
		stream.writeInt(coordinates.z);
	}
	
	public static void writeIndent(BufferedWriter writer, int indent) throws IOException {
		for (; indent > 0; --indent) writer.write(' ');
	}
	
}
