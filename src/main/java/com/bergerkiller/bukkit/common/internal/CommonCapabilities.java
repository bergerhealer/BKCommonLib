package com.bergerkiller.bukkit.common.internal;

/**
 * A list of server capabilities relevant for BKCommonLib to know about.
 * Storing them in booleans instead of evaluating frequently helps performance a bit.
 */
public class CommonCapabilities {
    /**
     * Minecraft 1.8.9 and before there was a bug in the World Entity collision handler.
     * This caused passengers to collide with their vehicle at random. Fun times!
     */
    public static final boolean VEHICLES_COLLIDE_WITH_PASSENGERS = CommonBootstrap.evaluateMCVersion("<", "1.9");

    /**
     * Minecraft 1.8-1.8.9 had PacketPlayInUseItem and PacketPlayInBlockPlace merged as one
     */
    public static final boolean PLACE_PACKETS_MERGED = CommonBootstrap.evaluateMCVersion("<", "1.9");

    /**
     * Minecraft 1.9 and later specifies block/block as parent for block models,
     * which stores the transformations in different display modes. This is missing
     * on 1.8-1.8.9.
     */
    public static final boolean RESOURCE_PACK_MODEL_BASE_TRANSFORMS = CommonBootstrap.evaluateMCVersion(">=", "1.9");

    /**
     * Since Minecraft 1.9, effect names changed from simple names to longer keys with namespace
     * to improve mapping and sound system handling. As a result, random.fizz turned into block.fire.extinguish.
     */
    public static final boolean KEYED_EFFECTS = CommonBootstrap.evaluateMCVersion(">=", "1.9");

    /**
     * Since Minecraft 1.9 some changes to the chat component JSON structure
     */
    public static final boolean CHAT_TEXT_JSON_VER2 = CommonBootstrap.evaluateMCVersion(">=", "1.9");

    /**
     * Since Minecraft 1.9 int-based datawatcher keys were replaced with Datawatcher objects, significantly
     * simplifying registering these systems.
     */
    public static final boolean DATAWATCHER_OBJECTS = CommonBootstrap.evaluateMCVersion(">=", "1.9");

    /**
     * Since Minecraft 1.9.2 players can dual-wield, now introducing an off-hand
     */
    public static final boolean PLAYER_OFF_HAND = CommonBootstrap.evaluateMCVersion(">=", "1.9");

    /**
     * Since Minecraft 1.11.2 the Entity move function had some changes, introducing a
     * "EnumMoveType" enumeration parameter to handle piston motion. This changed the
     * function signature.
     */
    public static final boolean ENTITY_MOVE_VER2 = CommonBootstrap.evaluateMCVersion(">=", "1.11.2");

    /**
     * Since Minecraft 1.14 the Entity move function had some changes, replacing the
     * x/y/z delta coordinates with a Vec3D object. This changed the function signature.
     */
    public static final boolean ENTITY_MOVE_VER3 = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.11.2 the World Provider 'isDarkWorld' boolean property was inverted.
     */
    public static final boolean WORLD_LIGHT_DARK_INVERTED = CommonBootstrap.evaluateMCVersion(">=", "1.11.2");

    /**
     * Since Minecraft 1.11 ItemStacks could be 'empty' using the empty constant introduced in 1.8.9.
     * The player inventory 'dirty' state uses this new empty state, among others.
     */
    public static final boolean ITEMSTACK_EMPTY_STATE = CommonBootstrap.evaluateMCVersion(">=", "1.11");

    /**
     * Since Minecraft 1.11 all tile entities' serialized NBT data use Minecraft key names, still
     * standard to this day. Before that version it used fairly random names instead, which require
     * translation.
     */
    public static final boolean TILE_ENTITY_LEGACY_NAMES = CommonBootstrap.evaluateMCVersion("<=", "1.10.2");

    /**
     * Since Minecraft 1.9 more than one passenger per vehicle are permitted
     */
    public static final boolean MULTIPLE_PASSENGERS = CommonBootstrap.evaluateMCVersion(">=", "1.9");

    /**
     * Since Minecraft 1.8.3 EntitySlice class layout was changed
     */
    public static final boolean REVISED_CHUNK_ENTITY_SLICE = CommonBootstrap.evaluateMCVersion(">=", "1.8.3");

    /**
     * Since Minecraft 1.13 the Material enum was entirely overhauled
     */
    public static final boolean MATERIAL_ENUM_CHANGES = CommonBootstrap.evaluateMCVersion(">=", "1.13");

    /**
     * Since Minecraft 1.13 the Map Id is stored in the NBT tag, instead of as a durability value
     */
    public static final boolean MAP_ID_IN_NBT = CommonBootstrap.evaluateMCVersion(">=", "1.13");

    /**
     * Since Minecraft 1.13 particle effects have options data attached, such as DustOptions for Redstone
     */
    public static final boolean PARTICLE_OPTIONS = CommonBootstrap.evaluateMCVersion(">=", "1.13");

    /**
     * Since Minecraft 1.13 lists of AxisAlignedBB objects were replaced with VoxelShape
     */
    public static final boolean HAS_VOXELSHAPE_LOGIC = CommonBootstrap.evaluateMCVersion(">=", "1.13");

    /**
     * Since Minecraft 1.9 a prepare anvil event exists. Versions before that require a packet listener
     * to intercept and handle these packets.
     */
    public static final boolean HAS_PREPARE_ANVIL_EVENT = CommonBootstrap.evaluateMCVersion(">=", "1.9");

    /**
     * Since Minecraft 1.13 items can have an empty String as a display name, and it will show
     * an empty spot instead of the item's default name. This can be used with the Anvil GUI
     * to set the text box to an empty default.
     */
    public static final boolean EMPTY_ITEM_NAME = CommonBootstrap.evaluateMCVersion(">=", "1.13");

    /**
     * Since Minecraft 1.13.1 the dimension field of Entity is a dimension manager, instead of an int.
     * Since Minecraft 1.16 the dimension field is gone entirely
     */
    public static final boolean ENTITY_USES_DIMENSION_MANAGER = CommonBootstrap.evaluateMCVersion(">=", "1.13.1") &&
                                                                !CommonBootstrap.evaluateMCVersion(">=", "1.16");

    /**
     * Deprecated: use ENTITY_USES_DIMENSION_MANAGER instead
     */
    @Deprecated
    public static final boolean HAS_DIMENSION_MANAGER = ENTITY_USES_DIMENSION_MANAGER;

    /**
     * Since Minecraft 1.13 the slab ('step') Block has its own Block type.
     * Before that all slabs were a 'stone' slab Block type, with different variants.
     */
    public static final boolean BLOCK_SLAB_HAS_OWN_BLOCK = CommonBootstrap.evaluateMCVersion(">=", "1.13");

    /**
     * Whether the ForcedChunk provider can make use of a chunk (plugin) ticket API to keep chunks loaded
     */
    public static final boolean HAS_CHUNK_TICKET_API = CommonBootstrap.evaluateMCVersion(">=", "1.13.1");

    /**
     * Since Minecraft 1.14 the LongHashSet, LongObjectHashMap and IntHashMap collections are no longer
     * used. Instead, a library is used for these.
     */
    public static final boolean UTIL_COLLECTIONS_REMOVED = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 a new light engine is used, with a new LightUpdate Packet and LightEngine storage
     */
    public static final boolean NEW_LIGHT_ENGINE = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 the Lore attribute of items is a chat component (json) instead of a String
     */
    public static final boolean LORE_IS_CHAT_COMPONENT = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 the Entity Tracker was moved to the Player Chunk Map, and each entry is split
     * into an Entry and a State.
     */
    public static final boolean PLAYER_CHUNK_MAP_ENTITY_TRACKER = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 an asynchronous chunk loading system is used that tends to deadlock
     */
    public static final boolean ASYNCHRONOUS_CHUNK_LOADER = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 the window types are stored in a 'MENU' registry
     */
    public static final boolean HAS_WINDOW_TYPE_REGISTRY = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 multiple different material sign types exist for the wall sign and sign post
     */
    public static final boolean HAS_MATERIAL_SIGN_TYPES = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 items can store custom model data
     */
    public static final boolean HAS_CUSTOM_MODEL_DATA = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 the default constructor for entities includes an EntityTypes&lt;?&gt; instance as a first parameter
     */
    public static final boolean ENTITY_USES_ENTITYTYPES_IN_CONSTRUCTOR = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 it is no longer possible to cancel the chunk unload event
     */
    public static final boolean CAN_CANCEL_CHUNK_UNLOAD_EVENT = CommonBootstrap.evaluateMCVersion("<=", "1.13.2");

    /**
     * Since Minecraft 1.15 the NBT primitive container types return 'this' when cloning
     * and are immutable (no setters).
     */
    public static final boolean IMMUTABLE_NBT_PRIMITIVES = CommonBootstrap.evaluateMCVersion(">=", "1.15");

    /**
     * Since Minecraft 1.16 damage from lava and fire is no longer handled inside the entity move function
     */
    public static final boolean ENTITY_FIRE_DAMAGE_IN_MOVE_HANDLER = CommonBootstrap.evaluateMCVersion("<", "1.16");

    /**
     * Between Minecraft 1.16 and 1.16.5 it is not possible to cancel a vehicle exit event, resulting in the player
     * desynchronizing from the server if attempted.
     */
    public static final boolean VEHICLE_EXIT_CANCELLABLE = CommonBootstrap.evaluateMCVersion("<", "1.16") ||
                                                           CommonBootstrap.evaluateMCVersion(">=", "1.17");

    /**
     * Since Minecraft 1.16 the spawn world is saved as a dimension key in the NBT
     */
    public static final boolean PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY = CommonBootstrap.evaluateMCVersion(">=", "1.16");

    /**
     * Since Minecraft 1.16.2 the spawn point has an angle component
     */
    public static final boolean PLAYER_SPAWN_HAS_ANGLE = CommonBootstrap.evaluateMCVersion(">=", "1.16.2");

    /**
     * Since Minecraft 1.17 the destroy packet uses just a single entity id.
     * Earlier versions supported destroying multiple entities at once.
     */
    public static final boolean PACKET_DESTROY_MULTIPLE = CommonBootstrap.evaluateMCVersion("<", "1.17") || CommonBootstrap.evaluateMCVersion(">=", "1.17.1");

    /**
     * Since Minecraft 1.17 entity removal comes with a reason, and there is not just a die() method.
     */
    public static final boolean ENTITY_REMOVE_WITH_REASON = CommonBootstrap.evaluateMCVersion(">=", "1.17");

    /**
     * Since Minecraft 1.18 all methods names are de-obfuscated
     */
    public static final boolean MOJANGMAP_METHODS = CommonBootstrap.evaluateMCVersion(">=", "1.18");

    /**
     * Since Minecraft 1.19 the living/painting entity spawn packets were removed and replaced with
     * the generic spawn entity packet.
     */
    public static final boolean ENTITY_SPAWN_PACKETS_MERGED = CommonBootstrap.evaluateMCVersion(">=", "1.19");

    /**
     * Since Minecraft 1.19.3 the player info packet was split into two separate packets.
     * Before that removal was done using the player info update packet.
     */
    public static final boolean PLAYER_INFO_PACKET_SPLIT = CommonBootstrap.evaluateMCVersion(">=", "1.19.3");

    /**
     * Since Minecraft 1.19.4 BlockData can be serialized in entity metadata
     */
    public static final boolean HAS_BLOCKDATA_METADATA = CommonBootstrap.evaluateMCVersion(">=", "1.19.4");
}
