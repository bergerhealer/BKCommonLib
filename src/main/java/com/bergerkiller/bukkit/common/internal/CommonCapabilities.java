package com.bergerkiller.bukkit.common.internal;

/**
 * A list of server capabilities relevant for BKCommonLib to know about.
 * Storing them in booleans instead of evaluating frequently helps performance a bit.
 */
public class CommonCapabilities {
    /**
     * Minecraft 1.8.8 and before there was a bug in the World Entity collision handler.
     * This caused passengers to collide with their vehicle at random. Fun times!
     */
    public static final boolean VEHICLES_COLLIDE_WITH_PASSENGERS = CommonBootstrap.evaluateMCVersion("<=", "1.8.8");

    /**
     * Minecraft 1.8-1.8.8 had PacketPlayInUseItem and PacketPlayInBlockPlace merged as one
     */
    public static final boolean PLACE_PACKETS_MERGED = CommonBootstrap.evaluateMCVersion("<=", "1.8.8");

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
    public static final boolean PLAYER_OFF_HAND = CommonBootstrap.evaluateMCVersion(">=", "1.9.2");

    /**
     * Since Minecraft 1.11.2 the Entity move function had some changes, introducing a
     * "EnumMoveType" enumeration parameter to handle piston motion. This changed the
     * function signature.
     */
    public static final boolean ENTITY_MOVE_VER2 = CommonBootstrap.evaluateMCVersion(">=", "1.11.2");

    /**
     * Since Minecraft 1.11.2 the World Provider 'isDarkWorld' boolean property was inverted.
     */
    public static final boolean WORLD_LIGHT_DARK_INVERTED = CommonBootstrap.evaluateMCVersion(">=", "1.11.2");

    /**
     * Since Minecraft 1.11 ItemStacks could be 'empty' using the empty constant introduced in 1.8.8.
     * The player inventory 'dirty' state uses this new empty state, among others.
     */
    public static final boolean ITEMSTACK_EMPTY_STATE = CommonBootstrap.evaluateMCVersion(">=", "1.11");

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
     * Since Minecraft 1.13.1 the dimension field of Entity is a dimension manager, instead of an int
     */
    public static final boolean ENTITY_USES_DIMENSION_MANAGER = CommonBootstrap.evaluateMCVersion(">=", "1.13.1");

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
     * Since Minecraft 1.14 the LongHashSet, LongObjectHashMap and IntHashMap collections are no longer
     * used. Instead, a library is used for these.
     */
    public static final boolean UTIL_COLLECTIONS_REMOVED = CommonBootstrap.evaluateMCVersion(">=", "1.14");

    /**
     * Since Minecraft 1.14 a new light engine is used, with a new LightUpdate Packet and LightEngine storage
     */
    public static final boolean NEW_LIGHT_ENGINE = CommonBootstrap.evaluateMCVersion(">=", "1.14");
}
