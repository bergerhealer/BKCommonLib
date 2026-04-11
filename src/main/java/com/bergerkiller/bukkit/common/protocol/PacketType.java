package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.SynchedEntityDataHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.reflection.net.minecraft.server.NMSPacketClasses.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PacketType extends ClassTemplate<Object> {
    static {
        CommonBootstrap.initServer();
    }

    private static Map<Class<?>, PacketTypeOptions> typesByPacketClassVisible = Collections.emptyMap();
    private static final ClassMap<PacketTypeOptions> typesByPacketClass = new ClassMap<PacketTypeOptions>();
    private static final PacketTypeOptions NO_TYPE_OPTIONS = new PacketTypeOptions() {
        @Override
        public PacketType firstRegistered() { return null; }
        @Override
        public PacketType find(Object packetHandle) { return null; }
        @Override
        public PacketTypeOptions add(PacketType newType) { return new PacketTypeOptionsSingleton(newType); }
    };

    /*
     * ========================
     * === Outgoing packets ===
     * ========================
     */
    /* Misc. packets */
    public static final NMSPacket DEFAULT = new NMSPacket();
    public static final NMSClientboundPlayerAbilitiesPacket OUT_ABILITIES = new NMSClientboundPlayerAbilitiesPacket();
    public static final NMSClientboundUpdateAdvancementsPacket OUT_ADVANCEMENTS = new NMSClientboundUpdateAdvancementsPacket();
    public static final NMSClientboundBlockEventPacket OUT_BLOCK_ACTION = new NMSClientboundBlockEventPacket();
    //public static final NMSPacketPlayOutChat OUT_CHAT = new NMSPacketPlayOutChat();
    public static final NMSClientboundTakeItemEntityPacket OUT_COLLECT = new NMSClientboundTakeItemEntityPacket();
    public static final NMSClientboundCustomPayloadPacket OUT_CUSTOM_PAYLOAD = new NMSClientboundCustomPayloadPacket();
    public static final NMSClientboundSetExperiencePacket OUT_EXPERIENCE = new NMSClientboundSetExperiencePacket();
    public static final NMSClientboundExplodePacket OUT_EXPLOSION = new NMSClientboundExplodePacket();
    public static final NMSClientboundGameEventPacket OUT_GAME_STATE_CHANGE = new NMSClientboundGameEventPacket();
    public static final NMSClientboundSetHeldSlotPacket OUT_HELD_ITEM_SLOT = new NMSClientboundSetHeldSlotPacket();
    public static final NMSClientboundKeepAlivePacket OUT_KEEP_ALIVE = new NMSClientboundKeepAlivePacket();
    public static final NMSClientboundDisconnectPacket OUT_KICK_DISCONNECT = new NMSClientboundDisconnectPacket();
    public static final NMSClientboundLoginPacket OUT_LOGIN = new NMSClientboundLoginPacket();
    public static final NMSClientboundMapItemDataPacket OUT_MAP = new NMSClientboundMapItemDataPacket();
    public static final NMSClientboundLevelChunkWithLightPacket OUT_MAP_CHUNK = new NMSClientboundLevelChunkWithLightPacket();
    public static final NMSClientboundTabListPacket OUT_PLAYER_LIST_HEADER_FOOTER = new NMSClientboundTabListPacket();
    //public static final NMSPacketPlayOutMultiBlockChange OUT_MULTI_BLOCK_CHANGE = new NMSPacketPlayOutMultiBlockChange();
    public static final NMSClientboundSoundPacket OUT_NAMED_SOUND_EFFECT = new NMSClientboundSoundPacket();
    public static final NMSClientboundOpenSignEditorPacket OUT_OPEN_SIGN_EDITOR = new NMSClientboundOpenSignEditorPacket();
    public static final NMSClientboundPlayerInfoUpdatePacket OUT_PLAYER_INFO_UPDATE = new NMSClientboundPlayerInfoUpdatePacket();
    public static final NMSClientboundPlayerInfoRemovePacket OUT_PLAYER_INFO_REMOVE = new NMSClientboundPlayerInfoRemovePacket();
    public static final NMSClientboundPlayerPositionPacket OUT_POSITION = new NMSClientboundPlayerPositionPacket();
    public static final NMSClientboundPlayerRotationPacket OUT_ROTATION = new NMSClientboundPlayerRotationPacket();
    public static final NMSClientboundRespawnPacket OUT_RESPAWN = new NMSClientboundRespawnPacket();
    public static final NMSClientboundSetDefaultSpawnPositionPacket OUT_SPAWN_POSITION = new NMSClientboundSetDefaultSpawnPositionPacket();
    public static final NMSClientboundAwardStatsPacket OUT_STATISTIC = new NMSClientboundAwardStatsPacket();
    public static final NMSClientboundCommandSuggestionsPacket OUT_TAB_COMPLETE = new NMSClientboundCommandSuggestionsPacket();
    public static final NMSClientboundBlockEntityDataPacket OUT_TILE_ENTITY_DATA = new NMSClientboundBlockEntityDataPacket();
    //public static final NMSPacketPlayOutTitle OUT_TITLE = new NMSPacketPlayOutTitle();
    public static final NMSClientboundSetHealthPacket OUT_UPDATE_HEALTH = new NMSClientboundSetHealthPacket();
    public static final NMSClientboundSetTimePacket OUT_UPDATE_TIME = new NMSClientboundSetTimePacket();
    public static final NMSClientboundLevelEventPacket OUT_WORLD_EVENT = new NMSClientboundLevelEventPacket();
    public static final NMSClientboundLevelParticlesPacket OUT_WORLD_PARTICLES = new NMSClientboundLevelParticlesPacket();
    public static final NMSClientboundBlockDestructionPacket OUT_BLOCK_BREAK_ANIMATION = new NMSClientboundBlockDestructionPacket();
    public static final NMSClientboundBlockUpdatePacket OUT_BLOCK_CHANGE = new NMSClientboundBlockUpdatePacket();
    public static final NMSClientboundBossEventPacket OUT_BOSS = new NMSClientboundBossEventPacket();
    public static final NMSClientboundSetCameraPacket OUT_CAMERA = new NMSClientboundSetCameraPacket();
    public static final NMSClientboundCustomSoundPacket OUT_CUSTOM_SOUND_EFFECT = new NMSClientboundCustomSoundPacket();
    public static final NMSClientboundResourcePackPushPacket OUT_RESOURCE_PACK_PUSH = new NMSClientboundResourcePackPushPacket();
    public static final NMSClientboundResourcePackPopPacket OUT_RESOURCE_PACK_POP = new NMSClientboundResourcePackPopPacket();
    public static final NMSClientboundChangeDifficultyPacket OUT_SERVER_DIFFICULTY = new NMSClientboundChangeDifficultyPacket();
    public static final NMSClientboundCooldownPacket OUT_SET_COOLDOWN = new NMSClientboundCooldownPacket();
    public static final NMSClientboundForgetLevelChunkPacket OUT_UNLOAD_CHUNK = new NMSClientboundForgetLevelChunkPacket();
    /* Scoreboard-related packets */
    public static final NMSClientboundSetDisplayObjectivePacket OUT_SCOREBOARD_DISPLAY_OBJECTIVE = new NMSClientboundSetDisplayObjectivePacket();
    public static final NMSClientboundSetObjectivePacket OUT_SCOREBOARD_OBJECTIVE = new NMSClientboundSetObjectivePacket();
    public static final NMSClientboundSetScorePacket OUT_SCOREBOARD_SCORE = new NMSClientboundSetScorePacket();
    public static final NMSClientboundResetScorePacket OUT_SCOREBOARD_SCORE_RESET = new NMSClientboundResetScorePacket();
    public static final NMSClientboundSetPlayerTeamPacket OUT_SCOREBOARD_TEAM = new NMSClientboundSetPlayerTeamPacket();
    /* Window-related packets */
    public static final NMSClientboundContainerClosePacket OUT_WINDOW_CLOSE = new NMSClientboundContainerClosePacket();
    public static final NMSClientboundContainerSetDataPacket OUT_WINDOW_DATA = new NMSClientboundContainerSetDataPacket();
    public static final NMSClientboundOpenScreenPacket OUT_WINDOW_OPEN = new NMSClientboundOpenScreenPacket();
    public static final NMSClientboundContainerSetSlotPacket OUT_WINDOW_SET_SLOT = new NMSClientboundContainerSetSlotPacket();
    public static final NMSClientboundContainerSetContentPacket OUT_WINDOW_ITEMS = new NMSClientboundContainerSetContentPacket();
    /* Entity-related packets */
    public static final NMSClientboundAddEntityPacket OUT_ENTITY_SPAWN = new NMSClientboundAddEntityPacket();
    public static final NMSClientboundAddPlayerPacket OUT_ENTITY_SPAWN_NAMED = new NMSClientboundAddPlayerPacket();

    /**
     * Used on Minecraft 1.8 - 1.21.4 to spawn exp orbs.
     * Use {@link #OUT_ENTITY_SPAWN} on 1.21.5 instead, and set the
     * experience value in the entity datawatcher metadata
     */
    public static final NMSClientboundAddExperienceOrbPacket OUT_ENTITY_SPAWN_EXPORB = new NMSClientboundAddExperienceOrbPacket();

    public static final NMSClientboundAddMobPacket OUT_ENTITY_SPAWN_LIVING = new NMSClientboundAddMobPacket();
    public static final NMSClientboundAddPaintingPacket OUT_ENTITY_SPAWN_PAINTING = new NMSClientboundAddPaintingPacket();
    public static final NMSPacketPlayOutSpawnEntityWeather OUT_ENTITY_SPAWN_WITHER = new NMSPacketPlayOutSpawnEntityWeather();
    public static final NMSClientboundRemoveEntitiesPacket OUT_ENTITY_DESTROY = new NMSClientboundRemoveEntitiesPacket();
    public static final NMSClientboundSetEntityLinkPacket OUT_ENTITY_ATTACH = new NMSClientboundSetEntityLinkPacket();
    public static final NMSClientboundUpdateMobEffectPacket OUT_ENTITY_EFFECT_ADD = new NMSClientboundUpdateMobEffectPacket();
    public static final NMSClientboundRemoveMobEffectPacket OUT_ENTITY_EFFECT_REMOVE = new NMSClientboundRemoveMobEffectPacket();
    public static final NMSClientboundSetEquipmentPacket OUT_ENTITY_EQUIPMENT = new NMSClientboundSetEquipmentPacket();
    public static final NMSClientboundRotateHeadPacket OUT_ENTITY_HEAD_ROTATION = new NMSClientboundRotateHeadPacket();
    public static final NMSClientboundMoveEntityPacketRot OUT_ENTITY_LOOK = new NMSClientboundMoveEntityPacketRot();
    public static final NMSClientboundAnimatePacket OUT_ENTITY_ANIMATION = new NMSClientboundAnimatePacket();
    public static final NMSClientboundSetEntityDataPacket OUT_ENTITY_METADATA = new NMSClientboundSetEntityDataPacket();
    public static final NMSClientboundEntityEventPacket OUT_ENTITY_STATUS = new NMSClientboundEntityEventPacket();
    public static final NMSClientboundEntityPositionSyncPacket OUT_ENTITY_TELEPORT = new NMSClientboundEntityPositionSyncPacket();
    public static final NMSClientboundSetEntityMotionPacket OUT_ENTITY_VELOCITY = new NMSClientboundSetEntityMotionPacket();
    public static final NMSClientboundMoveEntityPacketPos OUT_ENTITY_MOVE = new NMSClientboundMoveEntityPacketPos();
    public static final NMSClientboundMoveEntityPacketPosRot OUT_ENTITY_MOVE_LOOK = new NMSClientboundMoveEntityPacketPosRot();
    public static final NMSClientboundUpdateAttributesPacket OUT_ENTITY_UPDATE_ATTRIBUTES = new NMSClientboundUpdateAttributesPacket();
    public static final NMSClientboundSetPassengersPacket OUT_MOUNT = new NMSClientboundSetPassengersPacket();
    public static final NMSClientboundMoveVehiclePacket OUT_VEHICLE_MOVE = new NMSClientboundMoveVehiclePacket();

    // MC 1.8.8 only
    public static final NMSPacketPlayOutUpdateSign OUT_UPDATE_SIGN = new NMSPacketPlayOutUpdateSign();

    // MC 1.19.4 only
    public static final NMSClientboundBundlePacket OUT_BUNDLE = new NMSClientboundBundlePacket();

    /*
     * ========================
     * === Incoming packets ===
     * ========================
     */
    public static final NMSServerboundPlayerAbilitiesPacket IN_ABILITIES = new NMSServerboundPlayerAbilitiesPacket();
    public static final NMSServerboundSwingPacket IN_ENTITY_ANIMATION = new NMSServerboundSwingPacket();
    public static final NMSServerboundPlayerActionPacket IN_BLOCK_DIG = new NMSServerboundPlayerActionPacket();
    public static final NMSServerboundUseItemPacket IN_BLOCK_PLACE = new NMSServerboundUseItemPacket();
    public static final NMSServerboundPaddleBoatPacket IN_BOAT_MOVE = new NMSServerboundPaddleBoatPacket();
    public static final NMSServerboundChatPacket IN_CHAT = new NMSServerboundChatPacket();
    public static final NMSServerboundClientCommandPacket IN_CLIENT_COMMAND = new NMSServerboundClientCommandPacket();
    public static final NMSServerboundCustomPayloadPacket IN_CUSTOM_PAYLOAD = new NMSServerboundCustomPayloadPacket();
    public static final NMSServerboundPlayerCommandPacket IN_ENTITY_ACTION = new NMSServerboundPlayerCommandPacket();
    public static final NMSServerboundMovePlayerPacketRot IN_LOOK = new NMSServerboundMovePlayerPacketRot();
    public static final NMSServerboundMovePlayerPacketPos IN_POSITION = new NMSServerboundMovePlayerPacketPos();
    public static final NMSServerboundMovePlayerPacketPosRot IN_POSITION_LOOK = new NMSServerboundMovePlayerPacketPosRot();
    public static final NMSServerboundSetCarriedItemPacket IN_HELD_ITEM_SLOT = new NMSServerboundSetCarriedItemPacket();
    public static final NMSServerboundKeepAlivePacket IN_KEEP_ALIVE = new NMSServerboundKeepAlivePacket();
    public static final NMSServerboundTeleportToEntityPacket IN_SPECTATE = new NMSServerboundTeleportToEntityPacket();
    public static final NMSServerboundSetCreativeModeSlotPacket IN_SET_CREATIVE_SLOT = new NMSServerboundSetCreativeModeSlotPacket();
    public static final NMSServerboundClientInformationPacket IN_SETTINGS = new NMSServerboundClientInformationPacket();
    public static final NMSServerboundPlayerInputPacket IN_STEER_VEHICLE = new NMSServerboundPlayerInputPacket();
    public static final NMSServerboundCommandSuggestionPacket IN_TAB_COMPLETE = new NMSServerboundCommandSuggestionPacket();
    public static final NMSServerboundAcceptTeleportationPacket IN_TELEPORT_ACCEPT = new NMSServerboundAcceptTeleportationPacket();
    public static final NMSServerboundSignUpdatePacket IN_UPDATE_SIGN = new NMSServerboundSignUpdatePacket();
    public static final NMSServerboundInteractPacket IN_INTERACT_ENTITY = new NMSServerboundInteractPacket();
    public static final NMSServerboundAttackPacket IN_ATTACK_ENTITY = new NMSServerboundAttackPacket();
    public static final NMSServerboundUseItemOnPacket IN_USE_ITEM = new NMSServerboundUseItemOnPacket();
    public static final NMSServerboundMoveVehiclePacket IN_VEHICLE_MOVE = new NMSServerboundMoveVehiclePacket();
    public static final NMSServerboundClientTickEndPacket IN_CLIENT_TICK_END = new NMSServerboundClientTickEndPacket();

    /* Window-related packets */
    public static final NMSServerboundContainerClosePacket IN_WINDOW_CLOSE = new NMSServerboundContainerClosePacket();
    public static final NMSServerboundContainerButtonClickPacket IN_WINDOW_ENCHANT_ITEM = new NMSServerboundContainerButtonClickPacket();
    public static final NMSServerboundResourcePackPacket IN_WINDOW_RESOURCEPACK_STATUS = new NMSServerboundResourcePackPacket();
    public static final NMSServerboundContainerClickPacket IN_WINDOW_CLICK = new NMSServerboundContainerClickPacket();

    private final String name;
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
            if (this.name.equals("Packet")) {
                packetClass = CommonUtil.getClass("net.minecraft.network.protocol." + this.name);
            } else {
                packetClass = CommonUtil.getClass("net.minecraft.network.protocol.game." + this.name);
                if (packetClass == null) {
                    packetClass = CommonUtil.getClass("net.minecraft.network.protocol.common." + this.name);
                }
            }
        }

        if (packetClass == null) {
            //Logging.LOGGER_REFLECTION.warning("Failed to find NMS Packet class type for " + getClass().getSimpleName());
            this.outgoing = false;
            this.dataWatcherField = null;
            return;
        }

        // Store in mapping (ignore raw Packet type!)
        if (!packetClass.equals(PacketHandle.T.getType())) {
            // Register in the ClassMap
            synchronized (PacketType.class) {
                typesByPacketClass.put(packetClass, typesByPacketClass.getOrDefault(packetClass, NO_TYPE_OPTIONS).add(this));
                typesByPacketClassVisible = new HashMap<>(typesByPacketClass.getData()); // Regenerate, excludes mappings of extended classes
            }
        }

        // Apply the packet class
        this.setClass((Class<Object>) packetClass);
        this.addImport("net.minecraft.network.protocol.game.*");

        // Determine whether this Packet Class is outgoing, or not
        this.outgoing = isPacketOutgoing(getType());

        // Obtain the datawatcher Field
        FieldAccessor<DataWatcher> dataWatcherField = null;
        for (SafeField<?> field : this.getFields()) {
            if (SynchedEntityDataHandle.T.isType(field.getType())) {
                dataWatcherField = field.translate(DuplexConversion.dataWatcher);
                break;
            }
        }
        this.dataWatcherField = dataWatcherField;
    }

    public boolean isOutGoing() {
        return this.outgoing;
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
    public static PacketType getType(final Object packetHandle) {
        // Get class, protect against null
        final Class<?> packetHandleType;
        try {
            packetHandleType = packetHandle.getClass();
        } catch (NullPointerException ex) {
            if (packetHandle == null) {
                throw new IllegalArgumentException("Input packet is null");
            } else {
                throw ex; // never happens?
            }
        }

        return LogicUtil.synchronizeCopyOnWrite(PacketType.class,
                () -> typesByPacketClassVisible.getOrDefault(packetHandleType, NO_TYPE_OPTIONS).find(packetHandle),
                () -> {
                    PacketTypeOptions options = typesByPacketClass.getOrDefault(packetHandleType, NO_TYPE_OPTIONS);
                    PacketType type = options.find(packetHandle);
                    if (type == null) {
                        // Register an entirely new PacketType. This also registers it in both maps.
                        return new PacketType(packetHandleType);
                    }

                    // Update visible map cache - eliminates use of isAssignable logic in ClassMap
                    Map<Class<?>, PacketTypeOptions> newMap = new HashMap<Class<?>, PacketTypeOptions>(typesByPacketClassVisible);
                    newMap.put(packetHandleType, options);
                    typesByPacketClassVisible = newMap;

                    return type;
                });
    }

    /**
     * Determines whether a particular Packet Class is outgoing (packet is sent from server to client).
     * Includes complicated reflection hacks to properly support esoteric forge hacks (*cough* mohist)
     *
     * @param packetClass
     * @return True if packet class is outgoing
     */
    @SuppressWarnings("unchecked")
    private static boolean isPacketOutgoing(Class<?> packetClass) {
        if (packetClass == null) {
            return false;
        }

        try {
            if (CommonBootstrap.evaluateMCVersion(">=", "1.20.5")) {
                return isPacketOutgoing_1_20_5(packetClass);
            } else {
                return isPacketOutgoing_1_8_to_1_20_4(packetClass);
            }
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to determine outgoing for packet " + packetClass, t);
            return false;
        }
    }

    private static boolean isPacketOutgoing_1_20_5(Class<?> packetClass) throws Throwable {
        // Im getting too tired of this shit. On 1.20.5 this cannot be assessed without building
        // an entire fricken connection listener and whatnot. Even then it would be extremely hard to
        // figure out what CODEC instance belongs to what packet...
        // For now, just do it by name. Ugh.
        String name = MPLType.getName(packetClass);
        if (name.contains("Clientbound") || name.contains("PacketPlayOut")) {
            return true;
        } else if (name.contains("Serverbound") || name.contains("PacketPlayIn")) {
            return false;
        } else if (packetClass.equals(PacketHandle.T.getType())) {
            return false; // Invalid packet, technically...
        } else {
            throw new IllegalStateException("Unknown packet class name format: " + name);
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean isPacketOutgoing_1_8_to_1_20_4(Class<?> packetClass) throws Throwable {
        Class<?> enumProtocolType = CommonUtil.getClass("net.minecraft.network.EnumProtocol");
        Class<?> enumProtocolDirectionType = CommonUtil.getClass("net.minecraft.network.protocol.EnumProtocolDirection");
        Class<?> bimapClass = CommonUtil.getClass("com.google.common.collect.BiMap");

        // Get CLIENTBOUND EnumProtocolDirection constant
        Object clientBoundDirection;
        {
            Field f = Resolver.resolveAndGetDeclaredField(enumProtocolDirectionType, "CLIENTBOUND");
            clientBoundDirection = f.get(null);
        }

        // Gets the 'flows' field in EnumProtocol. This is a map by protocol direction.
        Field flowsField = null;
        if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            flowsField = Resolver.resolveAndGetDeclaredField(enumProtocolType, "flows");
        } else {
            // WindSpigot (and maybe others) renamed this field to "packetMap", look for that first
            flowsField = LogicUtil.tryMake(() -> {
                Field f = enumProtocolType.getDeclaredField("packetMap");
                return Map.class.isAssignableFrom(f.getType()) ? f : null;
            }, null);

            if (flowsField == null) {
                // Past vanilla field names
                if (CommonBootstrap.evaluateMCVersion(">=", "1.10.2")) {
                    flowsField = Resolver.resolveAndGetDeclaredField(enumProtocolType, "h");
                } else if (CommonBootstrap.evaluateMCVersion(">=", "1.8.3")) {
                    flowsField = Resolver.resolveAndGetDeclaredField(enumProtocolType, "j");
                } else {
                    flowsField = Resolver.resolveAndGetDeclaredField(enumProtocolType, "h");
                }
            }
        }
        flowsField.setAccessible(true);

        // Find the packet class in the registry
        Object[] protocols = enumProtocolType.getEnumConstants();
        for (Object protocol : protocols) {
            Object directionFlows = ((Map<?, Object>) flowsField.get(protocol)).get(clientBoundDirection);
            if (directionFlows == null) {
                continue;
            }

            if (bimapClass.isAssignableFrom(directionFlows.getClass())) {
                // Use BiMap containsValue to check it exists or not. Used before MC 1.15.
                Method containsValueMethod = bimapClass.getMethod("containsValue", Object.class);
                Boolean containsValue = (Boolean) containsValueMethod.invoke(directionFlows, packetClass);
                if (containsValue.booleanValue()) {
                    return true;
                }
            } else if (directionFlows instanceof Map) {
                // Used on WindSpigot and maybe other forks on MC 1.8.8 where it uses a netty IntObjectMap
                if (((Map<?, ?>) directionFlows).containsValue(packetClass)) {
                    return true;
                }
            } else {
                // After MC 1.20.2 the flows object stores an additional object which has the actual method
                // Go by all fields to find it
                PacketSearchResult packetContained = tryCheckPacketClassContained(directionFlows, packetClass);
                if (packetContained == PacketSearchResult.FAILED) {
                    // Try fields contained in directionFlows
                    for (Field f : directionFlows.getClass().getDeclaredFields()) {
                        if (Modifier.isStatic(f.getModifiers())) {
                            continue;
                        }
                        if (f.getType().getDeclaringClass() != enumProtocolType) {
                            continue;
                        }

                        f.setAccessible(true);
                        Object directionFlowsSub = f.get(directionFlows);
                        packetContained = tryCheckPacketClassContained(directionFlowsSub, packetClass);
                        if (packetContained != PacketSearchResult.FAILED) {
                            break;
                        }
                    }
                }

                if (packetContained == PacketSearchResult.FAILED) {
                    throw new IllegalStateException("Unable to identify packet flow direction");
                } else if (packetContained == PacketSearchResult.FOUND) {
                    return true;
                }
            }
        }

        return false;
    }

    private static PacketSearchResult tryCheckPacketClassContained(Object flows, Class<?> packetClass) throws Throwable {
        Method getPacketIdMethod = null;
        if (flows != null) {
            Class<?> flowType = flows.getClass();
            while (flowType != null && flowType != Object.class) {
                for (Method m : flowType.getDeclaredMethods()) {
                    if (m.getParameterCount() == 1 &&
                            m.getParameterTypes()[0].equals(Class.class) &&
                            (m.getReturnType() == Integer.class || m.getReturnType() == int.class)
                    ) {
                        getPacketIdMethod = m;
                        break;
                    }
                }
                flowType = flowType.getSuperclass();
            }
        }
        if (getPacketIdMethod == null) {
            return PacketSearchResult.FAILED;
        }

        // Invoke and check
        getPacketIdMethod.setAccessible(true);
        Integer packetId = (Integer) getPacketIdMethod.invoke(flows, packetClass);
        return (packetId != null && packetId.intValue() != -1)
                ? PacketSearchResult.FOUND : PacketSearchResult.NOT_FOUND;
    }

    private enum PacketSearchResult {
        FAILED,
        NOT_FOUND,
        FOUND
    }

    private static interface PacketTypeOptions {
        PacketType firstRegistered();
        PacketType find(Object packetHandle);
        PacketTypeOptions add(PacketType newType);
    }

    private static final class PacketTypeOptionsSingleton implements PacketTypeOptions {
        private final PacketType type;

        public PacketTypeOptionsSingleton(PacketType type) {
            this.type = type;
        }

        @Override
        public PacketType firstRegistered() {
            return type;
        }

        @Override
        public PacketType find(Object packetHandle) {
            return type;
        }

        @Override
        public PacketTypeOptions add(PacketType newType) {
            ArrayList<PacketType> types = new ArrayList<PacketType>(2);
            types.add(newType);
            types.add(type);
            return new PacketTypeOptionsMultiple(types);
        }
    }

    private static final class PacketTypeOptionsMultiple implements PacketTypeOptions {
        private final PacketType[] types;

        public PacketTypeOptionsMultiple(List<PacketType> types) {
            this.types = types.toArray(new PacketType[types.size()]);
        }

        @Override
        public PacketType firstRegistered() {
            return this.types[types.length - 1];
        }

        @Override
        public PacketType find(Object packetHandle) {
            for (PacketType type : types) {
                if (type.matchPacket(packetHandle)) {
                    return type;
                }
            }

            return null;
        }

        @Override
        public PacketTypeOptions add(PacketType newType) {
            ArrayList<PacketType> newTypes = new ArrayList<PacketType>(types.length + 1);
            newTypes.add(newType);
            newTypes.addAll(Arrays.asList(types));
            return new PacketTypeOptionsMultiple(newTypes);
        }
    }
}
