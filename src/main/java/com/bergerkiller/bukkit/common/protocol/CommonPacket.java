package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CommonPacket {
	private Object packet;
	private PacketType type;

	public CommonPacket(int id) {
		this.type = PacketType.fromId(id);
		this.packet = type.getPacket();
	}

	public CommonPacket(PacketType packet) {
		this.type = packet;
		this.packet = type.getPacket();
	}
	
	public CommonPacket(Object packet) {
		int id = PacketFields.DEFAULT.packetID.get(packet);
		this.type = PacketType.fromId(id);
		this.packet = packet;
	}
	
	public CommonPacket(Object packet, int id) {
		this.type = PacketType.fromId(id);
		this.packet = packet;
	}
	
	public PacketType getType() {
		return this.type;
	}
	
	public Object getHandle() {
		return this.packet;
	}

	public <T> void write(FieldAccessor<T> fieldAccessor, T value) {
		fieldAccessor.set(getHandle(), value);
	}

	public void write(String field, Object value) {
		try {
			SafeField.set(packet, field, value);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid field name: "+field);
		}
	}

	public void write(int index, Object value) throws IllegalArgumentException {
		String field = type.getField(index);
		if(field != null) {
			this.write(field, value);
		} else {
			throw new IllegalArgumentException("Invalid field index: "+index);
		}
	}

	public <T> T read(FieldAccessor<T> fieldAccessor) {
		return fieldAccessor.get(this.getHandle());
	}

	public Object read(String field) {
		try {
			return SafeField.get(packet, field);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid field name: "+field);
		}
	}

	public Object read(int index) throws IllegalArgumentException {
		String field = type.getField(index);
		if(field != null) {
			return this.read(field);
		} else {
			throw new IllegalArgumentException("Invalid field index: "+index);
		}
	}

	public void setDatawatcher(Object metaData) throws IllegalArgumentException {
		write(type.getMetaDataField(), metaData);
	}
	
	public Object getDatawatcher() {
		return this.read(type.getMetaDataField());
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
}