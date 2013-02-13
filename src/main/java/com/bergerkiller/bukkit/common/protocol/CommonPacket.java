package com.bergerkiller.bukkit.common.protocol;

import java.util.HashMap;

import com.bergerkiller.bukkit.common.reflection.classes.PacketFieldRef;
import com.bergerkiller.bukkit.common.reflection.classes.PacketRef;

public class CommonPacket {
	private Object packet;
	private PacketType type;
	
	public CommonPacket(int id) {
		this.type = PacketType.getFromInt(id);
		this.packet = type.getPacket();
	}
	
	public CommonPacket(PacketType packet) {
		this.type = packet;
		this.packet = type.getPacket();
	}
	
	public CommonPacket(Object packet) {
		int id = PacketRef.packetID.get(packet);
		this.type = PacketType.getFromInt(id);
		this.packet = packet;
	}
	
	public CommonPacket(Object packet, int id) {
		this.type = PacketType.getFromInt(id);
		this.packet = packet;
	}
	
	public PacketType getType() {
		return this.type;
	}
	
	public Object getHandle() {
		return this.packet;
	}
	
	public void write(String field, Object value) {
		PacketFieldRef.write(packet, field, value);
	}
	
	public void write(int index, Object value) throws IllegalArgumentException {
		String field = this.getField(index);
		if(field != null)
			this.write(field, value);
		else
			throw new IllegalArgumentException("Invalid field index: "+index);
	}
	
	public Object read(String field) {
		return PacketFieldRef.read(packet, field);
	}
	
	public Object read(int index) throws IllegalArgumentException {
		String field = this.getField(index);
		if(field != null)
			return this.read(field);
		else
			throw new IllegalArgumentException("Invalid field index: "+index);
	}
	
	public void setDatawatcher(Object metaData) throws IllegalArgumentException {
		String field = this.getMetaDataField();
		if(field != null)
			write(field, metaData);
	}
	
	public Object getDatawatcher() {
		String field = this.getMetaDataField();
		return this.read(field);
	}
	
	public int readInt(int index) throws IllegalArgumentException {
		return (Integer) read(index);
	}
	
	public boolean readBoolean(int index) throws IllegalArgumentException {
		return (Boolean) read(index);
	}
	
	public byte readByte(int index) throws IllegalArgumentException {
		return (Byte) read(index);
	}
	
	public String readString(int index) throws IllegalArgumentException {
		return (String) read(index);
	}
	
	public double readDouble(int index) throws IllegalArgumentException {
		return (Double) read(index);
	}
	
	private String getField(int index) {
		HashMap<Integer, String> fields = PacketFieldRef.fields.get(type);
		return fields.containsKey(index) ? fields.get(index) : null;
	}
	
	private String getMetaDataField() {
		if(PacketFieldRef.datawatchers.containsKey(type))
			return PacketFieldRef.datawatchers.get(type);
		else
			throw new IllegalArgumentException("MetaData field does not exist");
	}
}