package com.bergerkiller.bukkit.common.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

class CommonClasses {
    /*
     * In here we pre-load several classes to prevent unexpected ClassNotFound exceptions
     * This is required, as someone might be calling one of our classes from the main server thread
     * Then the main server thread class loader is used, which is unable to find (our) net.minecraft.server references
     */

    public static void init() {
        // Make sure bootstrap is finished before loading
        // If not compatible, skip further initialization
        if (!Common.IS_COMPATIBLE) {
            return;
        }

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
        // Logic
        loadLogic("EntityAddRemoveHandler", "EntityMoveHandler", "EntityTypingHandler", "RegionHandler");
        // Utility classes (only those that interact with nms)
        loadUtil("Block", "Chunk", "Common", "EntityProperty", "Entity", "Item", "Material", "Native", "NBT", "Packet");
        loadUtil("Recipe", "Stream", "World");
        // Remaining classes
        loadCommon("entity.CommonEntityType", "collections.CollectionBasics");
        loadCommon("scoreboards.CommonScoreboard", "scoreboards.CommonTeam");
        loadCommon("protocol.PacketType");
        loadCommon("internal.CommonDisabledEntity");
        loadCommon("wrappers.DataWatcher");
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

    public static void initializeLogicClasses(Logger logger) {
        try {
            RegionHandler.INSTANCE.forceInitialization();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the RegionHandler", t);
        }
        try {
            PortalHandler.INSTANCE.forceInitialization();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the PortalHandler", t);
        }
        try {
            PlayerFileDataHandler.INSTANCE.forceInitialization();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the PlayerFileDataHandler", t);
        }
        try {
            LightingHandler.instance().getClass();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the LightingHandler", t);
        }
        try {
            EntityTypingHandler.INSTANCE.getClass();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the EntityTypingHandler", t);
        }
        try {
            EntityMoveHandler.assertInitialized();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the EntityMoveHandler", t);
        }
        try {
            EntityAddRemoveHandler.INSTANCE.forceInitialization();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the EntityAddRemoveHandler", t);
        }
        try {
            BlockDataSerializer.INSTANCE.forceInitialization();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the BlockDataSerializer", t);
        }
    }
}
