package com.bergerkiller.bukkit.common.protocol;

import java.util.HashMap;

import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.classes.PacketFieldRef;
import com.bergerkiller.bukkit.common.reflection.classes.PacketRef;
import com.bergerkiller.bukkit.common.utils.StringUtil;

import net.minecraft.server.v1_4_R1.*;

public class CommonPacket {
	private Packet packet;
	private Packets type;
	
	public CommonPacket(int id) {
		this.type = Packets.getFromInt(id);
		this.packet = type.getPacket();
	}
	
	public CommonPacket(Packets packet) {
		this.type = packet;
		this.packet = type.getPacket();
	}
	
	public CommonPacket(Packet packet) {
		String name = packet.getClass().getSimpleName();
		this.type = Packets.getFromInt(StringUtil.countNrs(name));
		this.packet = packet;
	}
	
	public CommonPacket(Packet packet, int id) {
		this.type = Packets.getFromInt(id);
		this.packet = packet;
	}
	
	public Packets getType() {
		return this.type;
	}
	
	public Packet getHandle() {
		return this.packet;
	}
	
	public void write(String field, Object value) {
		SafeField.set(packet, field, value);
	}
	
	public void write(int index, Object value) throws IllegalArgumentException {
		String field = this.getField(index);
		if(field != null)
			this.write(field, value);
		else
			throw new IllegalArgumentException("Invalid field index: "+index);
	}
	
	public Object read(String field) {
		return SafeField.get(packet, field);
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
	
	public static enum Packets {
		KEEP_ALIVE(0),
		OPEN_WINDOW(100),
		CLODE_WINDOW(101),
		WINDOW_CLICK(102),
		SET_SLOW(103),
		WINDOW_ITEMS(104),
		PROGRESS_BAR(105),
		TRANSACTION(106),
		SET_CREATIVE_SLOT(107),
		BUTTON_CLICK(108),
		FLYING(10),
		PLAYER_POSITION(11),
		PLAYER_LOOK(12),
		UPDATE_SIGN(130),
		ITEM_DATA(131),
		TILE_ENTITY_DATA(132),
		PLAYER_LOOK_MOVE(13),
		BLOCK_DIG(14),
		PLACE(15),
		BLOCK_ITEM_SWITCH(16),
		ENTITY_LOCATION_ACTION(17),
		ANIMATION(18),
		ENTITY_ACTION(19),
		LOGIN(1),
		STATISTIC(200),
		PLAYER_INFO(201),
		ABILITIES(202),
		TAB_COMPLETE(203),
		CLIENT_INFO(204),
		CLIENT_COMMAND(205),
		NAMED_ENTITY_SPAWN(20),
		COLLECT(22),
		VEHICLE_SPAWN(23),
		MOB_SPAWN(24),
		CUSTOM_PAYLOAD(250),
		KEY_RESPONSE(252),
		KEY_REQUEST(253),
		GET_INFO(254),
		DISCONNECT(255),
		ENTITY_PAINTING(25),
		ADD_EXP_ORB(26),
		ENTITY_VELOCITY(28),
		DESTROY_ENTITY(29),
		HANDSHAKE(2),
		ENTITY(30),
		ENTITY_MOVE(31),
		ENTITY_LOOK(32),
		ETITY_MOVE_LOOK(33),
		ENTITY_TELEPORT(34),
		ENTITY_HEAD_ROTATION(35),
		ENTITY_STATUS(38),
		ATTACH_ENTITY(39),
		CHAT(3),
		ENTITY_METADATA(40),
		MOB_EFFECT(41),
		REMOVE_MOB_EFFECT(42),
		SET_EXP(43),
		UPDATE_TIME(4),
		MAP_CHUNK(51),
		MULTI_BLOCK_CHANGE(52),
		BLOCK_CHANGE(53),
		PLAY_NOTEBLOCK(54),
		BLOCK_BREAK_ANIMATION(55),
		MAP_CHUNK_BULK(56),
		ENTITY_EQUIPMENT(5),
		EXPLOSION(60),
		WOLRD_EVENT(61),
		NAMED_SOUND_EFFECT(62),
		SPAWN_POSITION(6),
		BED(70),
		WEATHER(71),
		USE_ENTITY(7),
		UPDATE_HEALTH(8),
		RESPAWN(9);
		
		
		private int id;
		
		Packets(int id) {
			this.id = id;
		}
		
		public Packet getPacket() {
			return PacketRef.getPacketById.invoke(null, this.id);
		}
		
		public int getId() {
			return this.id;
		}
		
		public static Packets getFromInt(int from) {
			for(Packets p : values()) {
				if(p.id == from)
					return p;
			}
			return null;
		}
	}
}