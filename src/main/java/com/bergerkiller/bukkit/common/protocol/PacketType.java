package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumProtocolHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.reflection.net.minecraft.server.NMSPacketClasses.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class PacketType extends ClassTemplate<Object> {
    static {
        CommonBootstrap.initTemplates();
    }

    private static final ClassMap<List<PacketType>> typesByPacketClass = new ClassMap<List<PacketType>>();

    /*
     * ========================
     * === Outgoing packets ===
     * ========================
     */
    /* Misc. packets */
    public static final NMSPacket DEFAULT = new NMSPacket();
    public static final NMSPacketPlayOutAbilities OUT_ABILITIES = new NMSPacketPlayOutAbilities();
    public static final NMSPacketPlayOutBed OUT_BED = Common.evaluateMCVersion(">=", "1.14") ? null : new NMSPacketPlayOutBed();
    public static final NMSPacketPlayOutBlockAction OUT_BLOCK_ACTION = new NMSPacketPlayOutBlockAction();
    public static final NMSPacketPlayOutChat OUT_CHAT = new NMSPacketPlayOutChat();
    public static final NMSPacketPlayOutCollect OUT_COLLECT = new NMSPacketPlayOutCollect();
    public static final NMSPacketPlayOutCustomPayload OUT_CUSTOM_PAYLOAD = new NMSPacketPlayOutCustomPayload();
    public static final NMSPacketPlayOutExperience OUT_EXPERIENCE = new NMSPacketPlayOutExperience();
    public static final NMSPacketPlayOutExplosion OUT_EXPLOSION = new NMSPacketPlayOutExplosion();
    public static final NMSPacketPlayOutGameStateChange OUT_GAME_STATE_CHANGE = new NMSPacketPlayOutGameStateChange();
    public static final NMSPacketPlayOutHeldItemSlot OUT_HELD_ITEM_SLOT = new NMSPacketPlayOutHeldItemSlot();
    public static final NMSPacketPlayOutKeepAlive OUT_KEEP_ALIVE = new NMSPacketPlayOutKeepAlive();
    public static final NMSPacketPlayOutKickDisconnect OUT_KICK_DISCONNECT = new NMSPacketPlayOutKickDisconnect();
    public static final NMSPacketPlayOutLogin OUT_LOGIN = new NMSPacketPlayOutLogin();
    public static final NMSPacketPlayOutMap OUT_MAP = new NMSPacketPlayOutMap();
    public static final NMSPacketPlayOutMapChunk OUT_MAP_CHUNK = new NMSPacketPlayOutMapChunk();
    public static final NMSPacketPlayOutPlayerListHeaderFooter OUT_PLAYER_LIST_HEADER_FOOTER = new NMSPacketPlayOutPlayerListHeaderFooter();
    public static final NMSPacketPlayOutMultiBlockChange OUT_MULTI_BLOCK_CHANGE = new NMSPacketPlayOutMultiBlockChange();
    public static final NMSPacketPlayOutNamedSoundEffect OUT_NAMED_SOUND_EFFECT = new NMSPacketPlayOutNamedSoundEffect();
    public static final NMSPacketPlayOutOpenSignEditor OUT_OPEN_SIGN_EDITOR = new NMSPacketPlayOutOpenSignEditor();
    public static final NMSPacketPlayOutPlayerInfo OUT_PLAYER_INFO = new NMSPacketPlayOutPlayerInfo();
    public static final NMSPacketPlayOutPosition OUT_POSITION = new NMSPacketPlayOutPosition();
    public static final NMSPacketPlayOutRespawn OUT_RESPAWN = new NMSPacketPlayOutRespawn();
    public static final NMSPacketPlayOutSpawnPosition OUT_SPAWN_POSITION = new NMSPacketPlayOutSpawnPosition();
    public static final NMSPacketPlayOutStatistic OUT_STATISTIC = new NMSPacketPlayOutStatistic();
    public static final NMSPacketPlayOutTabComplete OUT_TAB_COMPLETE = new NMSPacketPlayOutTabComplete();
    public static final NMSPacketPlayOutTileEntityData OUT_TILE_ENTITY_DATA = new NMSPacketPlayOutTileEntityData();
    public static final NMSPacketPlayOutTitle OUT_TITLE = new NMSPacketPlayOutTitle();
    public static final NMSPacketPlayOutUpdateHealth OUT_UPDATE_HEALTH = new NMSPacketPlayOutUpdateHealth();
    public static final NMSPacketPlayOutUpdateTime OUT_UPDATE_TIME = new NMSPacketPlayOutUpdateTime();
    public static final NMSPacketPlayOutWorldBorder OUT_WORLD_BORDER = new NMSPacketPlayOutWorldBorder();
    public static final NMSPacketPlayOutWorldEvent OUT_WORLD_EVENT = new NMSPacketPlayOutWorldEvent();
    public static final NMSPacketPlayOutWorldParticles OUT_WORLD_PARTICLES = new NMSPacketPlayOutWorldParticles();
    public static final NMSPacketPlayOutBlockBreakAnimation OUT_BLOCK_BREAK_ANIMATION = new NMSPacketPlayOutBlockBreakAnimation();
    public static final NMSPacketPlayOutBlockChange OUT_BLOCK_CHANGE = new NMSPacketPlayOutBlockChange();
    public static final NMSPacketPlayOutBoss OUT_BOSS = new NMSPacketPlayOutBoss();
    public static final NMSPacketPlayOutCamera OUT_CAMERA = new NMSPacketPlayOutCamera();
    public static final NMSPacketPlayOutCombatEvent OUT_COMBAT_EVENT = new NMSPacketPlayOutCombatEvent();
    public static final NMSPacketPlayOutCustomSoundEffect OUT_CUSTOM_SOUND_EFFECT = new NMSPacketPlayOutCustomSoundEffect();
    public static final NMSPacketPlayOutResourcePackSend OUT_RESOURCE_PACK_SEND = new NMSPacketPlayOutResourcePackSend();
    public static final NMSPacketPlayOutServerDifficulty OUT_SERVER_DIFFICULTY = new NMSPacketPlayOutServerDifficulty();
    public static final NMSPacketPlayOutSetCooldown OUT_SET_COOLDOWN = new NMSPacketPlayOutSetCooldown();
    public static final NMSPacketPlayOutUnloadChunk OUT_UNLOAD_CHUNK = new NMSPacketPlayOutUnloadChunk();
    /* Scoreboard-related packets */
    public static final NMSPacketPlayOutScoreboardDisplayObjective OUT_SCOREBOARD_DISPLAY_OBJECTIVE = new NMSPacketPlayOutScoreboardDisplayObjective();
    public static final NMSPacketPlayOutScoreboardObjective OUT_SCOREBOARD_OBJECTIVE = new NMSPacketPlayOutScoreboardObjective();
    public static final NMSPacketPlayOutScoreboardScore OUT_SCOREBOARD_SCORE = new NMSPacketPlayOutScoreboardScore();
    public static final NMSPacketPlayOutScoreboardTeam OUT_SCOREBOARD_TEAM = new NMSPacketPlayOutScoreboardTeam();
    /* Window-related packets */
    public static final NMSPacketPlayOutCloseWindow OUT_WINDOW_CLOSE = new NMSPacketPlayOutCloseWindow();
    public static final NMSPacketPlayOutWindowData OUT_WINDOW_DATA = new NMSPacketPlayOutWindowData();
    public static final NMSPacketPlayOutOpenWindow OUT_WINDOW_OPEN = new NMSPacketPlayOutOpenWindow();
    public static final NMSPacketPlayOutSetSlot OUT_WINDOW_SET_SLOT = new NMSPacketPlayOutSetSlot();
    public static final NMSPacketPlayOutTransaction OUT_WINDOW_TRANSACTION = new NMSPacketPlayOutTransaction();
    public static final NMSPacketPlayOutWindowItems OUT_WINDOW_ITEMS = new NMSPacketPlayOutWindowItems();
    /* Entity-related packets */
    public static final NMSPacketPlayOutEntity OUT_ENTITY = new NMSPacketPlayOutEntity();
    public static final NMSPacketPlayOutSpawnEntity OUT_ENTITY_SPAWN = new NMSPacketPlayOutSpawnEntity();
    public static final NMSPacketPlayOutNamedEntitySpawn OUT_ENTITY_SPAWN_NAMED = new NMSPacketPlayOutNamedEntitySpawn();
    public static final NMSPacketPlayOutSpawnEntityExperienceOrb OUT_ENTITY_SPAWN_EXPORB = new NMSPacketPlayOutSpawnEntityExperienceOrb();
    public static final NMSPacketPlayOutSpawnEntityLiving OUT_ENTITY_SPAWN_LIVING = new NMSPacketPlayOutSpawnEntityLiving();
    public static final NMSPacketPlayOutSpawnEntityPainting OUT_ENTITY_SPAWN_PAINTING = new NMSPacketPlayOutSpawnEntityPainting();
    public static final NMSPacketPlayOutSpawnEntityWeather OUT_ENTITY_SPAWN_WITHER = new NMSPacketPlayOutSpawnEntityWeather();
    public static final NMSPacketPlayOutEntityDestroy OUT_ENTITY_DESTROY = new NMSPacketPlayOutEntityDestroy();
    public static final NMSPacketPlayOutAttachEntity OUT_ENTITY_ATTACH = new NMSPacketPlayOutAttachEntity();
    public static final NMSPacketPlayOutEntityEffect OUT_ENTITY_EFFECT_ADD = new NMSPacketPlayOutEntityEffect();
    public static final NMSPacketPlayOutRemoveEntityEffect OUT_ENTITY_EFFECT_REMOVE = new NMSPacketPlayOutRemoveEntityEffect();
    public static final NMSPacketPlayOutEntityEquipment OUT_ENTITY_EQUIPMENT = new NMSPacketPlayOutEntityEquipment();
    public static final NMSPacketPlayOutEntityHeadRotation OUT_ENTITY_HEAD_ROTATION = new NMSPacketPlayOutEntityHeadRotation();
    public static final NMSPacketPlayOutEntityLook OUT_ENTITY_LOOK = new NMSPacketPlayOutEntityLook();
    public static final NMSPacketPlayOutAnimation OUT_ENTITY_ANIMATION = new NMSPacketPlayOutAnimation();
    public static final NMSPacketPlayOutEntityMetadata OUT_ENTITY_METADATA = new NMSPacketPlayOutEntityMetadata();
    public static final NMSPacketPlayOutEntityStatus OUT_ENTITY_STATUS = new NMSPacketPlayOutEntityStatus();
    public static final NMSPacketPlayOutEntityTeleport OUT_ENTITY_TELEPORT = new NMSPacketPlayOutEntityTeleport();
    public static final NMSPacketPlayOutEntityVelocity OUT_ENTITY_VELOCITY = new NMSPacketPlayOutEntityVelocity();
    public static final NMSPacketPlayOutRelEntityMove OUT_ENTITY_MOVE = new NMSPacketPlayOutRelEntityMove();
    public static final NMSPacketPlayOutRelEntityMoveLook OUT_ENTITY_MOVE_LOOK = new NMSPacketPlayOutRelEntityMoveLook();
    public static final NMSPacketPlayOutUpdateAttributes OUT_ENTITY_UPDATE_ATTRIBUTES = new NMSPacketPlayOutUpdateAttributes();
    public static final NMSPacketPlayOutMount OUT_MOUNT = new NMSPacketPlayOutMount();
    public static final NMSPacketPlayOutVehicleMove OUT_VEHICLE_MOVE = new NMSPacketPlayOutVehicleMove();

    // MC 1.8.8 only
    public static final NMSPacketPlayOutUpdateSign OUT_UPDATE_SIGN = new NMSPacketPlayOutUpdateSign();

    /*
     * ========================
     * === Incoming packets ===
     * ========================
     */
    public static final NMSPacketPlayInAbilities IN_ABILITIES = new NMSPacketPlayInAbilities();
    public static final NMSPacketPlayInArmAnimation IN_ENTITY_ANIMATION = new NMSPacketPlayInArmAnimation();
    public static final NMSPacketPlayInBlockDig IN_BLOCK_DIG = new NMSPacketPlayInBlockDig();
    public static final NMSPacketPlayInBlockPlace IN_BLOCK_PLACE = new NMSPacketPlayInBlockPlace();
    public static final NMSPacketPlayInBoatMove IN_BOAT_MOVE = new NMSPacketPlayInBoatMove();
    public static final NMSPacketPlayInChat IN_CHAT = new NMSPacketPlayInChat();
    public static final NMSPacketPlayInClientCommand IN_CLIENT_COMMAND = new NMSPacketPlayInClientCommand();
    public static final NMSPacketPlayInCustomPayload IN_CUSTOM_PAYLOAD = new NMSPacketPlayInCustomPayload();
    public static final NMSPacketPlayInEntityAction IN_ENTITY_ACTION = new NMSPacketPlayInEntityAction();
    public static final NMSPacketPlayInLook IN_LOOK = new NMSPacketPlayInLook();
    public static final NMSPacketPlayInPosition IN_POSITION = new NMSPacketPlayInPosition();
    public static final NMSPacketPlayInPositionLook IN_POSITION_LOOK = new NMSPacketPlayInPositionLook();
    public static final NMSPacketPlayInHeldItemSlot IN_HELD_ITEM_SLOT = new NMSPacketPlayInHeldItemSlot();
    public static final NMSPacketPlayInKeepAlive IN_KEEP_ALIVE = new NMSPacketPlayInKeepAlive();
    public static final NMSPacketPlayInSpectate IN_SPECTATE = new NMSPacketPlayInSpectate();
    public static final NMSPacketPlayInSetCreativeSlot IN_SET_CREATIVE_SLOT = new NMSPacketPlayInSetCreativeSlot();
    public static final NMSPacketPlayInSettings IN_SETTINGS = new NMSPacketPlayInSettings();
    public static final NMSPacketPlayInSteerVehicle IN_STEER_VEHICLE = new NMSPacketPlayInSteerVehicle();
    public static final NMSPacketPlayInTabComplete IN_TAB_COMPLETE = new NMSPacketPlayInTabComplete();
    public static final NMSPacketPlayInTeleportAccept IN_TELEPORT_ACCEPT = new NMSPacketPlayInTeleportAccept();
    public static final NMSPacketPlayInUpdateSign IN_UPDATE_SIGN = new NMSPacketPlayInUpdateSign();
    public static final NMSPacketPlayInUseEntity IN_USE_ENTITY = new NMSPacketPlayInUseEntity();
    public static final NMSPacketPlayInUseItem IN_USE_ITEM = new NMSPacketPlayInUseItem();
    public static final NMSPacketPlayInVehicleMove IN_VEHICLE_MOVE = new NMSPacketPlayInVehicleMove();

    /* Window-related packets */
    public static final NMSPacketPlayInCloseWindow IN_WINDOW_CLOSE = new NMSPacketPlayInCloseWindow();
    public static final NMSPacketPlayInEnchantItem IN_WINDOW_ENCHANT_ITEM = new NMSPacketPlayInEnchantItem();
    public static final NMSPacketPlayInResourcePackStatus IN_WINDOW_RESOURCEPACK_STATUS = new NMSPacketPlayInResourcePackStatus();
    public static final NMSPacketPlayInTransaction IN_WINDOW_TRANSACTION = new NMSPacketPlayInTransaction();
    public static final NMSPacketPlayInWindowClick IN_WINDOW_CLICK = new NMSPacketPlayInWindowClick();

    private final String name;
    private final int id;
    private final boolean outgoing;
    private final FieldAccessor<DataWatcher> dataWatcherField;

    static {
        //TODO: Check that all NMS packets are actually registered here
    }

    /**
     * Constructor used in PacketTypeClasses to construct by name
     */
    public PacketType() {
        this((String) null);
    }

    public void init() {

    }

    protected PacketType(String packetClassName) {
        this(packetClassName, null);
    }

    protected PacketType(Class<?> packetClass) {
        this(packetClass.getSimpleName(), packetClass);
    }

    @SuppressWarnings("unchecked")
    protected PacketType(String packetClassName, Class<?> packetClass) {
        // If not specified, resort to using the PacketType class name to obtain the Class
        this.name = (packetClassName != null) ? packetClassName : getClass().getSimpleName().substring(3);

        // If not specified, find NMS Packet Class Type
        if (packetClass == null) {
            packetClass = CommonUtil.getNMSClass(this.name);
        }

        if (packetClass == null) {
            //Logging.LOGGER_REFLECTION.warning("Failed to find NMS Packet class type for " + getClass().getSimpleName());
            this.outgoing = false;
            this.id = -1;
            this.dataWatcherField = null;
            return;
        }

        // Store in mapping (ignore raw Packet type!)
        if (!packetClass.equals(PacketHandle.T.getType())) {
            List<PacketType> types_old = typesByPacketClass.get(packetClass);
            if (types_old == null || types_old.isEmpty()) {
                typesByPacketClass.put(packetClass, Collections.singletonList(this));
            } else {
                ArrayList<PacketType> new_list = new ArrayList<PacketType>(types_old);
                new_list.add(0, this);
                new_list.trimToSize();
                typesByPacketClass.put(packetClass, new_list);
            }
        }

        // Apply the packet class
        this.setClass((Class<Object>) packetClass);

        // Obtain ID and determine in/outgoing
        int tmpId;
        if ((tmpId = EnumProtocolHandle.PLAY.getPacketIdIn(getType())) != -1) {
            this.outgoing = false;
            this.id = tmpId;
        } else if ((tmpId = EnumProtocolHandle.PLAY.getPacketIdOut(getType())) != -1) {
            this.outgoing = true;
            this.id = tmpId;
        } else {
            this.outgoing = false;
            this.id = -1;
            if (packetClass == null || !packetClass.equals(CommonUtil.getNMSClass("Packet"))) {
                Logging.LOGGER_NETWORK.log(Level.WARNING, "Packet '" + getClass().getSimpleName() + " is not registered!");
            }
        }

        // Obtain the datawatcher Field
        FieldAccessor<DataWatcher> dataWatcherField = null;
        for (SafeField<?> field : this.getFields()) {
            if (DataWatcherHandle.T.isType(field.getType())) {
                dataWatcherField = field.translate(DuplexConversion.dataWatcher);
                break;
            }
        }
        this.dataWatcherField = dataWatcherField;
    }

    public boolean isOutGoing() {
        return this.outgoing;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * When multiple packet types match the same packet class, this method allows these
     * different types to be differentiated. For example, on MC 1.8 to 1.8.8 PacketPlayInBlockPlace
     * and PacketPlayInUseItem share the same packet class, using a field in the packet to select
     * between them.<br>
     * <br>
     * By default this method returns always true.
     * 
     * @param packetHandle
     * @return True if matching
     */
    protected boolean matchPacket(Object packetHandle) {
        return true;
    }

    /**
     * Called before sending or receiving a packet downstream. Allows a Packet Type to make modifications
     * before the packet is actually handled by the server or client.
     * 
     * @param packetHandle
     */
    public void preprocess(Object packetHandle) {
    }

    public FieldAccessor<DataWatcher> getMetaDataField() {
        if (dataWatcherField == null) {
            throw new IllegalArgumentException("MetaData field does not exist");
        }
        return dataWatcherField;
    }

    /**
     * Figures out the PacketType of a NMS Packet instance
     * 
     * @param packetHandle
     * @return Packet Type
     */
    public static PacketType getType(Object packetHandle) {
        if (packetHandle == null) {
            throw new IllegalArgumentException("Null packets can not be used");
        }
        List<PacketType> types = typesByPacketClass.get(packetHandle);
        if (types == null || types.isEmpty()) {
            // Packet class has no known type
            return new PacketType(packetHandle.getClass());
        } else if (types.size() == 1) {
            // Packet class has only one possible type
            return types.get(0);
        } else {
            // Multiple packet classes are possible, use method to filter
            for (PacketType type : types) {
                if (type.matchPacket(packetHandle)) {
                    return type;
                }
            }
            // Weird?
            return new PacketType(packetHandle.getClass());
        }
    }

    @Deprecated
    public static PacketType getType(int packetId, boolean outGoing) {
        final Class<?> packetHandleClass;
        if (outGoing) {
            packetHandleClass = EnumProtocolHandle.PLAY.getPacketClassOut(packetId);
        } else {
            packetHandleClass = EnumProtocolHandle.PLAY.getPacketClassIn(packetId);
        }
        if (packetHandleClass == null) {
            return null;
        } else {
            List<PacketType> types = typesByPacketClass.get(packetHandleClass);
            if (types == null || types.isEmpty()) {
                types = Collections.singletonList(new PacketType(packetHandleClass));
            }
            return types.get(0);
        }
    }
}
