package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

public class CommonPacket {
	private Object packet;
	private PacketType type;

	public CommonPacket(int id, boolean outGoing) {
		this(PacketType.getType(id, outGoing));
	}

	public CommonPacket(PacketType packetType) {
		this.type = packetType;
		this.packet = this.type.createPacketHandle();
	}

	public CommonPacket(Object packetHandle) {
		this(packetHandle, PacketType.getType(packetHandle));
	}

	public CommonPacket(Object packetHandle, PacketType packetType) {
		this.type = packetType;
		this.packet = packetHandle;
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
	@SuppressWarnings("unchecked")
	public void write(int index, Object value) throws IllegalArgumentException {
		this.write((FieldAccessor<Object>) this.type.getFieldAt(index), value);
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
		return this.read(this.type.getFieldAt(index));
	}

	/**
	 * Set the datawatcher from a packet
	 * 
	 * @param datawatcher DataWatcher
	 * @throws IllegalArgumentException no datawatcher field found
	 */
	public void setDatawatcher(DataWatcher datawatcher) throws IllegalArgumentException {
		write(type.getMetaDataField(), datawatcher);
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

	@Override
	public String toString() {
		final Object handle = getHandle();
		if (handle == null) {
			return "null";
		}
		PacketType type = this.getType();
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(type).append(" {\n");
			// Get all field accessor constants defined in the Packet Type
			for (SafeField<?> field : ClassTemplate.create(type).getFields()) {
				if (field.isStatic()) {
					continue;
				}
				Object fieldValue = field.get(type);
				if (fieldValue instanceof FieldAccessor) {
					// Obtain the name and value for each field
					final String name = field.getName();
					final Object value = ((FieldAccessor<?>) fieldValue).get(handle);
					builder.append("  ").append(name).append(" = ").append(Conversion.toString.convert(value)).append('\n');
				}
			}
			builder.append("}");
			return builder.toString();
		} catch (Throwable t) {
		}
		// Print the fields (error/unknown packet...)
		StringBuilder builder = new StringBuilder();
		if (handle.getClass().getName().startsWith(Common.NMS_ROOT)) {
			builder.append(handle.getClass().getSimpleName());
		} else {
			builder.append(handle.getClass().getName());
		}
		builder.append(" {\n");
		for (SafeField<?> field : ClassTemplate.create(handle).getFields()) {
			if (field.isStatic()) {
				continue;
			}
			builder.append("  ").append(field.getName()).append(" = ").append(Conversion.toString.convert(field.get(handle))).append('\n');
		}
		builder.append("}");
		return builder.toString();
	}
}