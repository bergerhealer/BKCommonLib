package com.bergerkiller.bukkit.common.protocol;

import java.util.Arrays;
import java.util.List;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.classes.DataWatcherRef;

public enum PacketType {
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
	TILE_ENTITY_OPEN(133),
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
	SET_SCOREBOARD_OBJECTIVE(206),
	SET_SCOREBOARD_SCORE(207),
	SET_SCOREBOARD_DISPLAY_OBJECTIVE(208),
	SET_SCOREBOARD_TEAM(209),
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
	ENTITY_MOVE_LOOK(33),
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
	WORLD_PARTICLES(63),
	SPAWN_POSITION(6),
	BED(70),
	WEATHER(71),
	USE_ENTITY(7),
	UPDATE_HEALTH(8),
	RESPAWN(9),
	UNKNOWN(-1);

	private final int id;
	private final ClassTemplate<?> template;
	private final String[] fieldNames;
	private final String dataWatcherField;
	private static final PacketType[] byId = new PacketType[256];

	private PacketType(int id) {
		this.id = id;
		final Class<?> type = (Class<?>) PacketFields.DEFAULT.getStaticFieldValue("l", Conversion.toIntHashMap).get(id);
		if (type == null) {
			this.template = null;
			this.dataWatcherField = null;
			this.fieldNames = new String[0];
			return;
		}
		this.template = ClassTemplate.create(type);
		List<SafeField<?>> fields = this.template.getFields();
		this.fieldNames = new String[fields.size()];
		String dataWatcherField = null;
		for (int i = 0; i < fields.size(); i++) {
			SafeField<?> field = fields.get(i);
			if (DataWatcherRef.TEMPLATE.isType(field.getType())) {
				dataWatcherField = field.getName();
			}
			fieldNames[i] = field.getName();
		}
		this.dataWatcherField = dataWatcherField;

		// Needed?
		/*
		Field[] Allfields;
		if(CommonPlugin.getInstance().libaryInstalled)
			Allfields = ProtocolLib.getFields(getPacket());
		else
			Allfields = type.getDeclaredFields();
		*/
	}

	/**
	 * Constructs a new Packet instance from this Type
	 * 
	 * @return Packet
	 */
	public Object getPacket() {
		return template == null ? null : template.newInstance();
	}

	/**
	 * Gets the Packet Id of this Packet type
	 * 
	 * @return Packet type id
	 */
	public int getId() {
		return id;
	}

	public String getMetaDataField() {
		if (dataWatcherField == null) {
			throw new IllegalArgumentException("MetaData field does not exist");
		}
		return dataWatcherField;
	}

	public String getField(int index) {
		return (index >= 0 && index < fieldNames.length) ? fieldNames[index] : null;
	}

	/**
	 * Gets the Packet Type from a Packet Id
	 * 
	 * @param id of the Packet
	 * @return Packet Type, or UNKNOWN if unknown
	 */
	public static PacketType fromId(int id) {
		if (id >= 0 && id < 256) {
			return byId[id];
		} else {
			return UNKNOWN;
		}
	}

	static {
		Arrays.fill(byId, UNKNOWN);
		for (PacketType type : values()) {
			if (type != UNKNOWN) {
				byId[type.getId()] = type;
			}
		}
	}
}