package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

class CommonClasses {
    /*
     * In here we pre-load several classes to prevent unexpected ClassNotFound exceptions
     * This is required, as someone might be calling one of our classes from the main server thread
     * Then the main server thread class loader is used, which is unable to find (our) net.minecraft.server references
     */

    public static void init() {
        // Conversion
        CommonUtil.loadClass(Common.class);

        // Reflection classes
        /*
        loadRef("BlockState", "ChunkProviderServer", "CraftScheduler", "CraftServer", "CraftTask", "EntityMinecart", "EntityPlayer");
        loadRef("Entity", "EntityTrackerEntry", "EntityTracker", "EntityTypes", "LongHashMapEntry", "LongHashSet", "LongHashMap");
        loadRef("NetworkManager", "PlayerChunk", "PlayerChunkMap", "PluginDescriptionFile", "RegionFileCache", "ChunkRegionLoader");
        loadRef("Recipe", "NBT", "RegionFile", "TileEntity", "ChunkSection", "Block", "Chunk", "World", "WorldServer");
        loadRef("EnumGamemode", "EnumProtocol");
        */
        // Internal
        loadCommon("internal.CommonTabController");
        // Logic
        loadLogic("EntityAddRemoveHandler", "EntityMoveHandler", "EntityTypesClasses", "RegionHandler");
        // Utility classes (only those that interact with nms)
        loadUtil("Block", "Chunk", "Common", "EntityProperty", "Entity", "Item", "Material", "Native", "NBT", "Packet");
        loadUtil("Recipe", "Stream", "World");
        // Remaining classes
        loadCommon("entity.CommonEntityType", "collections.CollectionBasics");
        loadCommon("scoreboards.CommonScoreboard", "scoreboards.CommonTeam");
        loadCommon("protocol.PacketType");
    }

    private static void loadLogic(String... classNames) {
        for (int i = 0; i < classNames.length; i++) {
            classNames[i] = "internal.logic." + classNames[i];
        }
        loadCommon(classNames);
    }

    private static void loadUtil(String... classNames) {
        for (int i = 0; i < classNames.length; i++) {
            classNames[i] = "utils." + classNames[i] + "Util";
        }
        loadCommon(classNames);
    }

    private static void loadCommon(String... classNames) {
        for (int i = 0; i < classNames.length; i++) {
            classNames[i] = Common.COMMON_ROOT + "." + classNames[i];
        }
        Common.loadClasses(classNames);
    }
}
