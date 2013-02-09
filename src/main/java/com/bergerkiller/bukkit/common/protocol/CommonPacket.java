package com.bergerkiller.bukkit.common.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.classes.PacketFieldRef;
import com.bergerkiller.bukkit.common.reflection.classes.PacketRef;

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
		this.type = Packets.getFromInt(countNrs(name));
		this.packet = packet;
	}
	
	public Packets getType() {
		return this.type;
	}
	
	public Packet getHandle() {
		return this.packet;
	}
	
	public void write(String field, Object value) {
		FieldAccessor<Object> data = new SafeField<Object>(packet, field);
		data.set(packet, value);
	}
	
	public void write(int index, Object value) {
		this.write(this.getField(index), value);
	}
	
	public Object read(String field) {
		FieldAccessor<Object> data = new SafeField<Object>(packet, field);
		return data.get(field);
	}
	
	public Object read(int index) {
		return this.read(this.getField(index));
	}
	
	public void setDatawatcher(Object metaData) {
		if(PacketFieldRef.metaData.containsKey(type)) {
			String field = PacketFieldRef.metaData.get(type);
			FieldAccessor<Object> data = new SafeField<Object>(packet, field);
			data.set(packet, metaData);
		}
	}
	
	public int readInt(int index) {
		return (Integer) read(index);
	}
	
	public boolean readBoolean(int index) {
		return (Boolean) read(index);
	}
	
	public byte readByte(int index) {
		return (Byte) read(index);
	}
	
	public String readString(int index) {
		return (String) read(index);
	}
	
	private String getField(int index) {
		return PacketFieldRef.fields.get(type).get(index);
	}
	
	private int countNrs(String str) {
		String result = "";
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(str);
		
		while(matcher.find()) {
			result += matcher.group();
		}
		
		return result != "" ? Integer.valueOf(result) : 0;
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
		
		public static Packets getFromInt(int from) {
			for(Packets p : values()) {
				if(p.id == from)
					return p;
			}
			return null;
		}
	}
}