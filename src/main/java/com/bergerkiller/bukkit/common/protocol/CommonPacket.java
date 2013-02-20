package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

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
	
	/**
	 * Get the packet type
	 * 
	 * @return Packet type
	 */
	public PacketType getType() {
		return this.type;
	}
	
	/**
	 * Get the vanilla object of the packet
	 * 
	 * @return Vanilla object
	 */
	public Object getHandle() {
		return this.packet;
	}

	/**
	 * Write data on a cusotm field in the packet
	 * 
	 * @param fieldAccessor Custom field
	 * @param value Value
	 */
	public <T> void write(FieldAccessor<T> fieldAccessor, T value) {
		fieldAccessor.set(getHandle(), value);
	}

	/**
	 * Write data to a field by name
	 * 
	 * @param field Field name
	 * @param value Value
	 */
	public void write(String field, Object value) {
		try {
			SafeField.set(packet, field, value);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid field name: "+field);
		}
	}

	/**
	 * Write data to a field by index
	 * 
	 * @param index Index
	 * @param value Value
	 * @throws IllegalArgumentException Invalid field index
	 */
	public void write(int index, Object value) throws IllegalArgumentException {
		String field = type.getField(index);
		if(field != null) {
			this.write(field, value);
		} else {
			throw new IllegalArgumentException("Invalid field index: "+index);
		}
	}

	/**
	 * Read data from a custom field
	 * 
	 * @param fieldAccessor Cusotm field
	 * @return Data
	 */
	public <T> T read(FieldAccessor<T> fieldAccessor) {
		return fieldAccessor.get(this.getHandle());
	}

	/**
	 * Read data from a field by name
	 * 
	 * @param field Field name
	 * @return Data
	 */
	public Object read(String field) {
		try {
			return SafeField.get(packet, field);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid field name: "+field);
		}
	}

	/**
	 * Read data from a field by index
	 * 
	 * @param index Index
	 * @return Data
	 * @throws IllegalArgumentException Ivalid field index
	 */
	public Object read(int index) throws IllegalArgumentException {
		String field = type.getField(index);
		if(field != null) {
			return this.read(field);
		} else {
			throw new IllegalArgumentException("Invalid field index: "+index);
		}
	}

	/**
	 * Set the datawatcher from a packet
	 * 
	 * @param datawatcher DataWatcher
	 * @throws IllegalArgumentException no datawatcher field found
	 */
	public void setDatawatcher(DataWatcher datawatcher) throws IllegalArgumentException {
		write(type.getMetaDataField(), datawatcher.getHandle());
	}
	
	/**
	 * Get the data watcher from a packet
	 * 
	 * @throws IllegalArgumentException no datawatcher field found
	 * @return DataWatcher
	 */
	public Object getDatawatcher() throws IllegalArgumentException {
		return this.read(type.getMetaDataField());
	}
	
	/**
	 * Read an integer from a packet
	 * 
	 * @param index Field index
	 * @return Integer
	 * @throws IllegalArgumentException Field index not found
	 */
	public int readInt(int index) throws IllegalArgumentException {
		return (Integer) read(index);
	}
	
	/**
	 * Read a boolean from a packet
	 * 
	 * @param index Field index
	 * @return Boolean
	 * @throws IllegalArgumentException Field index not found
	 */
	public boolean readBoolean(int index) throws IllegalArgumentException {
		return (Boolean) read(index);
	}
	
	/**
	 * Read a byte from a packet
	 * 
	 * @param index Field index
	 * @return Byte
	 * @throws IllegalArgumentException Field index not found
	 */
	public byte readByte(int index) throws IllegalArgumentException {
		return (Byte) read(index);
	}
	
	/**
	 * Read a string from a packet
	 * 
	 * @param index Field index
	 * @return String
	 * @throws IllegalArgumentException Field index not found
	 */
	public String readString(int index) throws IllegalArgumentException {
		return (String) read(index);
	}

	/**
	 * Read a double from a packet
	 * 
	 * @param index Field index
	 * @return Double
	 * @throws IllegalArgumentException Field index not found
	 */
	public double readDouble(int index) throws IllegalArgumentException {
		return (Double) read(index);
	}
}