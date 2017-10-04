package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.WeatherState;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.IDataManagerHandle;
import com.bergerkiller.generated.net.minecraft.server.MovingObjectPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.PlayerChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.PlayerChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldNBTStorageHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftTravelAgentHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.reflection.net.minecraft.server.NMSVector;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WorldUtil extends ChunkUtil {

    private static final Object findSpawnDummyEntity = EntityPlayerHandle.T.newInstanceNull();

    /**
     * Gets BlockData for a particular Block
     * 
     * @param block to query
     * @return BlockData
     */
    public static BlockData getBlockData(org.bukkit.block.Block block) {
        return ChunkUtil.getBlockData(block.getChunk(), block.getX(), block.getY(), block.getZ());
    }

    /**
     * Gets BlockData for a particular Block
     * 
     * @param world of the block
     * @param coordinates of the block
     * @return BlockData
     */
    public static BlockData getBlockData(org.bukkit.World world, IntVector3 coordinates) {
        return getBlockData(world, coordinates.x, coordinates.y, coordinates.z);
    }

    /**
     * Gets BlockData for a particular Block
     * 
     * @param world of the block
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @return BlockData
     */
    public static BlockData getBlockData(org.bukkit.World world, int x, int y, int z) {
        return WorldHandle.T.getBlockDataAtCoord.invoke(HandleConversion.toWorldHandle(world), x, y, z);

        /*
        Object worldHandleRaw = HandleConversion.toWorldHandle(world);
        Object blockPos = BlockPositionHandle.T.constr_x_y_z.raw.newInstance(x, y, z);
        Object iBlockData = WorldHandle.T.getBlockData.raw.invoke(worldHandleRaw, blockPos); 
        return BlockData.fromBlockData(iBlockData);
        */
    }

    /**
     * Gets Block Material Type for a particular Block.
     * 
     * @param block to query
     * @return Block Material Type
     */
    public static org.bukkit.Material getBlockType(org.bukkit.block.Block block) {
        return getBlockData(block.getWorld(), block.getX(), block.getY(), block.getZ()).getType();
    }

    /**
     * Gets Block Material Type for a particular Block.
     * 
     * @param world of the block
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @return Block Material Type
     */
    public static org.bukkit.Material getBlockType(org.bukkit.World world, int x, int y, int z) {
        return getBlockData(world, x, y, z).getType();
    }

    /**
     * Sets Block Data for a particular Block and performs physics updates
     * 
     * @param block to set
     * @param data to set to
     */
    public static void setBlockData(org.bukkit.block.Block block, BlockData data) {
        setBlockData(block.getWorld(), block.getX(), block.getY(), block.getZ(), data);
    }

    /**
     * Sets Block Data for a particular Block and performs physics updates
     * 
     * @param world of the block
     * @param position of the block
     * @param data to set to
     */
    public static void setBlockData(org.bukkit.World world, IntVector3 position, BlockData data) {
        setBlockData(world, position.x, position.y, position.z, data);
    }

    /**
     * Sets Block Data for a particular Block and performs physics updates
     * 
     * @param world of the block
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @param data to set to
     */
    public static void setBlockData(org.bukkit.World world, int x, int y, int z, BlockData data) {
        NMSWorld.updateBlock(HandleConversion.toWorldHandle(world), x, y, z, data, NMSWorld.UPDATE_DEFAULT);
    }

    /**
     * Sets Block Material Type for a particular Block and performs physics updates
     * 
     * @param block to set
     * @param type to set to
     */
    public static void setBlockType(org.bukkit.block.Block block, org.bukkit.Material type) {
        setBlockData(block, BlockData.fromMaterial(type));
    }

    /**
     * Sets Block Material Type for a particular Block and performs physics updates
     * 
     * @param world of the block
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @param type to set to
     */
    public static void setBlockType(org.bukkit.World world, int x, int y, int z, org.bukkit.Material type) {
        setBlockData(world, x, y, z, BlockData.fromMaterial(type));
    }

    /**
     * Sets Block Data for a particular Block without performing physics updates
     * 
     * @param block to set
     * @param data to set to
     */
    public static void setBlockDataFast(org.bukkit.block.Block block, BlockData data) {
        ChunkUtil.setBlockFast(block.getChunk(), block.getX(), block.getY(), block.getZ(), data);

        //setBlockDataFast(block.getWorld(), block.getX(), block.getY(), block.getZ(), data);
    }

    /**
     * Sets Block Data for a particular Block without performing physics updates
     * 
     * @param world of the block
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @param data to set to
     */
    public static void setBlockDataFast(org.bukkit.World world, int x, int y, int z, BlockData data) {
        ChunkUtil.setBlockFast(world.getChunkAt(x >> 4, z >> 4), x, y, z, data);

        // We can not use this, because it causes doPhysics to be called when setting the Block in Chunk
        // For some blocks, such as powered rails, it causes a forced uncancellable physics update - unwanted!
        //NMSWorld.updateBlock(Conversion.toWorldHandle.convert(world), x, y, z, data, NMSWorld.UPDATE_NOTIFY);
    }

    /**
     * Sets Block Material Type for a particular Block without performing physics updates
     * 
     * @param block to set
     * @param type to set to
     */
    public static void setBlockTypeFast(org.bukkit.block.Block block, org.bukkit.Material type) {
        setBlockDataFast(block, BlockData.fromMaterial(type));
    }

    /**
     * Sets Block Material Type for a particular Block without performing physics updates
     * 
     * @param world of the block
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @param type to set to
     */
    public static void setBlockTypeFast(org.bukkit.World world, int x, int y, int z, org.bukkit.Material type) {
        setBlockDataFast(world, x, y, z, BlockData.fromMaterial(type));
    }

    /**
     * Gets the shared Random of a world
     *
     * @param world to get the Random of
     * @return Random generator of a world
     */
    public static Random getRandom(org.bukkit.World world) {
        return CommonNMS.getHandle(world).getRandom();
    }

    /**
     * Sets if the spawn chunk area should be kept in memory
     *
     * @param world World to apply value on
     * @param value Keep in memory or not?
     */
    public static void setKeepSpawnInMemory(org.bukkit.World world, boolean value) {
        CommonNMS.getHandle(world).setKeepSpawnInMemory(value);
    }

    /**
     * Removes a single entity from the world
     *
     * @param entity to remove
     */
    public static void removeEntity(org.bukkit.entity.Entity entity) {
        EntityHandle e = CommonNMS.getHandle(entity);
        e.getWorld().removeEntity(e);
        e.getWorldServer().getEntityTracker().stopTracking(entity);
    }

    /**
     * Removes a world from all global locations where worlds are mapped
     *
     * @param world to remove
     */
    public static void removeWorld(org.bukkit.World world) {
        // Remove the world from the Bukkit worlds mapping
        Iterator<org.bukkit.World> iter = getWorlds().iterator();
        while (iter.hasNext()) {
            if (iter.next() == world) {
                iter.remove();
            }
        }
        // Remove the world from the MinecraftServer worlds mapping
        CommonNMS.getMCServer().getWorlds().remove(WorldServerHandle.fromBukkit(world));
    }

    /**
     * Obtains the internally stored collection of worlds<br>
     * Gets the values from the CraftServer.worlds map
     *
     * @return A collection of World instances
     */
    public static Collection<org.bukkit.World> getWorlds() {
        return CBCraftServer.worlds.get(Bukkit.getServer()).values();
    }

    /**
     * Gets a live collection (allows modification in the world) of entities on
     * a given world
     *
     * @param world the entities are on
     * @return collection of entities on the world
     */
    public static Collection<org.bukkit.entity.Entity> getEntities(org.bukkit.World world) {
        return DuplexConversion.entityList.convert(WorldHandle.T.entityList.raw.get(HandleConversion.toWorldHandle(world)));
    }

    /**
     * Gets a live collection (allows modification in the world) of players on a
     * given world
     *
     * @param world the players are on
     * @return collection of players on the world
     */
    public static Collection<Player> getPlayers(org.bukkit.World world) {
        return DuplexConversion.playerList.convert(WorldHandle.T.players.raw.get(HandleConversion.toWorldHandle(world)));
    }

    /**
     * Gets the folder where world data of a certain world is saved in
     *
     * @param world (can not be null)
     * @return world folder
     */
    public static File getWorldFolder(org.bukkit.World world) {
        return getWorldFolder(world.getName());
    }

    /**
     * Checks whether a given world name can be loaded
     *
     * @param worldName to check
     * @return True if the world at this world name is loadable, False if not
     */
    public static boolean isLoadableWorld(String worldName) {
        return Common.SERVER.isLoadableWorld(worldName);
    }

    /**
     * Gets the folder where world data for a certain world name is saved in
     *
     * @param worldName (can not be null)
     * @return world folder
     */
    public static File getWorldFolder(String worldName) {
        return Common.SERVER.getWorldFolder(worldName);
    }

    /**
     * Gets the File Location where the regions of a world are contained
     *
     * @param worldName to get the regions folder for
     * @return Region folder
     */
    public static File getWorldRegionFolder(String worldName) {
        return Common.SERVER.getWorldRegionFolder(worldName);
    }

    /**
     * Obtains a Collection of world names that can be loaded without creating
     * it
     *
     * @return Loadable worlds
     */
    public static Collection<String> getLoadableWorlds() {
        return Common.SERVER.getLoadableWorlds();
    }

    /**
     * Attempts to find a suitable spawn location, searching from the
     * startLocation specified. Note that portals are created if no position can
     * be found.
     *
     * @param startLocation to find a spawn from
     * @return suitable spawn location, or the input startLocation if this
     * failed
     */
    public static Location findSpawnLocation(Location startLocation) {
        return findSpawnLocation(startLocation, true);
    }

    /**
     * Attempts to find a suitable spawn location, searching from the
     * startLocation specified. If specified, portals will be created if none
     * are found.
     *
     * @param startLocation to find a spawn from
     * @param createPortals - True to create a portal if not found, False not to
     * @return suitable spawn location, or the input startLocation if this
     * failed
     */
    public static Location findSpawnLocation(Location startLocation, boolean createPortals) {
        // Use a new travel agent to designate a proper position
        CraftTravelAgentHandle travelAgent = CraftTravelAgentHandle.createNew(startLocation.getWorld());
        travelAgent.setCanCreatePortal(createPortals);
        Location exit = travelAgent.findOrCreate(startLocation);
        // Adjust the exit to make it suitable for players
        // Note: this will raise an NPE while trying to fire the PortalExit event
        // This is expected behavior
        try {
            travelAgent.adjustExit(WrapperConversion.toEntity(findSpawnDummyEntity), exit, new Vector(0, 0, 0));
        } catch (NullPointerException ex) {
        }
        // Done!
        return exit;
    }

    /**
     * Gets the folder where player data of a certain world is saved in
     *
     * @param world (can not be null)
     * @return players folder
     */
    public static File getPlayersFolder(org.bukkit.World world) {
        IDataManagerHandle man = WorldHandle.fromBukkit(world).getDataManager();
        if (man.isInstanceOf(WorldNBTStorageHandle.T)) {
            return man.cast(WorldNBTStorageHandle.T).getPlayerDir();
        }
        return new File(getWorldFolder(world), "playerdata");
    }

    /**
     * Gets the dimension Id of a world
     *
     * @param world to get from
     * @return world dimension Id
     */
    public static int getDimension(org.bukkit.World world) {
        return WorldHandle.fromBukkit(world).getWorldData().getType().getDimension();
    }

    /**
     * Gets the server a world object is running on
     *
     * @param world to get the server of
     * @return server
     */
    public static Server getServer(org.bukkit.World world) {
        return WorldHandle.T.getServer.invoke(HandleConversion.toWorldHandle(world));
    }

    /**
     * Gets the Entity Tracker for the world specified
     *
     * @param world to get the tracker for
     * @return world Entity Tracker
     */
    public static EntityTracker getTracker(org.bukkit.World world) {
        return WorldServerHandle.T.entityTracker.get(HandleConversion.toWorldHandle(world));
    }

    /**
     * Gets the tracker entry of the entity specified
     *
     * @param entity to get it for
     * @return entity tracker entry, or null if none is set
     */
    public static EntityTrackerEntryHandle getTrackerEntry(org.bukkit.entity.Entity entity) {
        return getTracker(entity.getWorld()).getEntry(entity);
    }

    /**
     * Sets a new entity tracker entry for the entity specified
     *
     * @param entity to set it for
     * @param entityTrackerEntry to set to (can be null to remove only)
     * @return the previous tracker entry for the entity, or null if there was
     * none
     */
    public static EntityTrackerEntryHandle setTrackerEntry(org.bukkit.entity.Entity entity, EntityTrackerEntryHandle entityTrackerEntry) {
        return getTracker(entity.getWorld()).setEntry(entity, entityTrackerEntry);
    }

    /**
     * Gets all the entities in the given cuboid area
     *
     * @param world to get the entities in
     * @param ignore entity to ignore (do not return)
     * @param xmin of the cuboid to check
     * @param ymin of the cuboid to check
     * @param zmin of the cuboid to check
     * @param xmax of the cuboid to check
     * @param ymax of the cuboid to check
     * @param zmax of the cuboid to check
     * @return A (referenced) list of entities in the cuboid
     */
    public static List<org.bukkit.entity.Entity> getEntities(org.bukkit.World world, org.bukkit.entity.Entity ignore,
            double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {

        Object worldHandle = Conversion.toWorldHandle.convert(world);
        Object ignoreHandle = Conversion.toEntityHandle.convert(ignore);
        Object axisAlignedBB = NMSVector.newAxisAlignedBB(xmin, ymin, zmin, xmax, ymax, zmax);
        List<?> entityHandles = NMSWorld.getEntities.invoke(worldHandle, ignoreHandle, axisAlignedBB);
        return new ConvertingList<org.bukkit.entity.Entity>(entityHandles, DuplexConversion.entity);
    }

    /**
     * Gets all the entities nearby an entity
     *
     * @param entity to get the nearby entities of
     * @param radX to look for entities
     * @param radY to look for entities
     * @param radZ to look for entities
     * @return A (referenced) list of entities nearby
     */
    public static List<org.bukkit.entity.Entity> getNearbyEntities(org.bukkit.entity.Entity entity, double radX, double radY, double radZ) {
        Object worldHandle = Conversion.toWorldHandle.convert(entity.getWorld());
        Object entityHandle = Conversion.toEntityHandle.convert(entity);
        Object entityBounds = EntityHandle.T.getBoundingBox.raw.invoke(entityHandle);
        Object axisAlignedBB = NMSVector.growAxisAlignedBB(entityBounds, radX, radY, radZ);
        List<?> entityHandles = NMSWorld.getEntities.invoke(worldHandle, entityHandle, axisAlignedBB);
        return new ConvertingList<org.bukkit.entity.Entity>(entityHandles, DuplexConversion.entity);
    }

    /**
     * Gets all the entities nearby a Location
     *
     * @param location to get the nearby entities of
     * @param radX to look for entities
     * @param radY to look for entities
     * @param radZ to look for entities
     * @return A (referenced) list of entities nearby
     */
    public static List<org.bukkit.entity.Entity> getNearbyEntities(Location location, double radX, double radY, double radZ) {
        final double xmin = location.getX() - radX;
        final double ymin = location.getY() - radY;
        final double zmin = location.getZ() - radZ;
        final double xmax = location.getX() + radX;
        final double ymax = location.getY() + radY;
        final double zmax = location.getZ() + radZ;
        return getEntities(location.getWorld(), null, xmin, ymin, zmin, xmax, ymax, zmax);
    }

    /**
     * Calculates the damage factor for an entity exposed to an explosion
     *
     * @param explosionPosition of the explosion
     * @param entity that was damaged
     * @return damage factor
     */
    public static float getExplosionDamageFactor(Location explosionPosition, org.bukkit.entity.Entity entity) {
        final WorldHandle world = CommonNMS.getHandle(explosionPosition.getWorld());
        return world.getExplosionFactor(explosionPosition.toVector(), CommonNMS.getHandle(entity).getBoundingBox());
    }

    /**
     * Saves a world to disk, waiting until saving has completed before
     * returning. This may take significantly long. This method is cross-thread
     * supported.
     *
     * @param world to be saved
     */
    public static synchronized void saveToDisk(org.bukkit.World world) {
        CommonNMS.getHandle(world).saveLevel();
    }

    public static void loadChunks(Location location, final int radius) {
        loadChunks(location.getWorld(), location.getX(), location.getZ(), radius);
    }

    public static void loadChunks(org.bukkit.World world, double xmid, double zmid, final int radius) {
        loadChunks(world, MathUtil.toChunk(xmid), MathUtil.toChunk(zmid), radius);
    }

    public static void loadChunks(org.bukkit.World world, final int xmid, final int zmid, final int radius) {
        for (int cx = xmid - radius; cx <= xmid + radius; cx++) {
            for (int cz = zmid - radius; cz <= zmid + radius; cz++) {
                world.getChunkAt(cx, cz);
            }
        }
    }

    public static boolean isLoaded(Location location) {
        return isLoaded(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static boolean isLoaded(Block block) {
        return isLoaded(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public static boolean isLoaded(org.bukkit.World world, double x, double y, double z) {
        return isLoaded(world, MathUtil.toChunk(x), MathUtil.toChunk(z));
    }

    public static boolean isLoaded(org.bukkit.World world, int x, int y, int z) {
        return isLoaded(world, x >> 4, z >> 4);
    }

    public static boolean isLoaded(org.bukkit.World world, int chunkX, int chunkZ) {
        if (world == null) {
            return false;
        }
        return world.isChunkLoaded(chunkX, chunkZ);
    }

    public static boolean areChunksLoaded(org.bukkit.World world, int chunkCenterX, int chunkCenterZ, int chunkDistance) {
        return areBlocksLoaded(world, chunkCenterX << 4, chunkCenterZ << 4, chunkDistance << 4);
    }

    public static boolean areBlocksLoaded(org.bukkit.World world, int blockCenterX, int blockCenterZ, int distance) {
        return CommonNMS.getHandle(world).areChunksLoaded(new IntVector3(blockCenterX, 0, blockCenterZ), distance);
    }

    /**
     * Queue a chunk for resending to all players that are in range of it.
     * This will resend the chunk block data, but not any changes to tile entities in them.
     * Use this method to update chunk data after doing changes to its raw structure.
     *
     * @param chunk to resent
     * @return True if players were nearby, False if not
     */
    public static boolean queueChunkSend(org.bukkit.Chunk chunk) {
        return queueChunkSend(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Queue a chunk for resending to all players that are in range of it.
     * Use this method to update chunk data after doing changes to its raw structure.
     *
     * @param player to send chunk data for
     * @param chunkX - coordinate of the chunk
     * @param chunkZ - coordinate of the chunk
     * @return True if players were nearby, False if not
     */
    public static boolean queueChunkSend(org.bukkit.World world, int chunkX, int chunkZ) {
        PlayerChunkMapHandle playerChunkMap = CommonNMS.getHandle(world).getPlayerChunkMap();
        PlayerChunkHandle playerChunk = playerChunkMap.getChunk(chunkX, chunkZ);
        if (playerChunk != null && !playerChunk.getPlayers().isEmpty()) {
            // Simply remove and re-add the players to the chunk. Does an instant chunk resend, though.
            // This doesn't work because block updates disable in the chunk permanently
            /*
            List<Player> old_players = new ArrayList<Player>(NMSPlayerChunk.players.get(chunk));
            for (Player player : old_players) {
                NMSPlayerChunk.removePlayer.invoke(chunk, Conversion.toEntityHandle.convert(player));
            }
            for (Player player : old_players) {
                NMSPlayerChunk.addPlayer.invoke(chunk, Conversion.toEntityHandle.convert(player));
            }
            */

            // This method sends 64 block changes to trigger a chunk resend
            // It doesn't really work because entities disappear
            // NMSPlayerChunk.dirtySectionMask.set(chunk, 65535); // all chunk sections
            // NMSPlayerChunk.dirtyCount.set(chunk, 64); // 64 triggers a full chunk re-send
            // NMSPlayerChunkMap.markForUpdate.invoke(playerChunkMap, chunk); // tell main chunk map to update

            // Manual resend because none of the above work without bugs
            // We use 0x1FFFF instead of 0xFFFF to avoid sending biome data, as that despawns the entities
            // The 0x10000 is an ignored mask as it is outside of the range of chunk slices.
            Chunk chunk = playerChunk.getChunk(world);
            if (chunk != null) {
                List<Player> old_players = new ArrayList<Player>(playerChunk.getPlayers());
                CommonPacket packet = PacketType.OUT_MAP_CHUNK.newInstance(chunk, 0x1FFFF);
                for (Player player : old_players) {
                    PacketUtil.sendPacket(player, packet);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Queues a block for sending to all players in view
     * 
     * @param block
     */
    public static void queueBlockSend(org.bukkit.block.Block block) {
        queueBlockSend(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    /**
     * Queues a block for sending to all players in view
     *
     * @param world the block is in
     * @param blockX of the block
     * @param blockY of the block
     * @param blockZ of the block
     */
    public static void queueBlockSend(org.bukkit.World world, int blockX, int blockY, int blockZ) {
        Object worldHandle = HandleConversion.toWorldHandle(world);
        PlayerChunkMapHandle playerChunkMap = WorldServerHandle.T.playerChunkMapField.get(worldHandle);
        playerChunkMap.flagPosDirty(blockX, blockY, blockZ);
    }

    /**
     * Performs a ray tracing operation from one point to the other, and obtains
     * the (first) block hit
     *
     * @param world to ray trace in
     * @param startX to start ray tracing from
     * @param startY to start ray tracing from
     * @param startZ to start ray tracing from
     * @param endX to stop ray tracing (outer limit)
     * @param endY to stop ray tracing (outer limit)
     * @param endZ to stop ray tracing (outer limit)
     * @return the hit Block, or null if none was found (AIR)
     */
    public static Block rayTraceBlock(org.bukkit.World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
        MovingObjectPositionHandle mop = WorldHandle.fromBukkit(world).rayTrace(new Vector(startX, startY, startZ), new Vector(endX, endY, endZ), false);
        if (mop == null) {
            return null;
        } else {
            Vector pos = mop.getPos();
            return world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        }
    }

    /**
     * Performs a ray tracing operation from one point to the other, and obtains
     * the (first) block hit
     *
     * @param startLocation to start ray tracing from
     * @param direction to which to ray trace
     * @param maxLength limit of ray tracing
     * @return the hit Block, or null if none was found (AIR)
     */
    public static Block rayTraceBlock(Location startLocation, Vector direction, double maxLength) {
        final double startX = startLocation.getX();
        final double startY = startLocation.getY();
        final double startZ = startLocation.getZ();
        final double endX = startX + direction.getX() * maxLength;
        final double endY = startY + direction.getY() * maxLength;
        final double endZ = startZ + direction.getZ() * maxLength;
        return rayTraceBlock(startLocation.getWorld(), startX, startY, startZ, endX, endY, endZ);
    }

    /**
     * Performs a ray tracing operation from one point to the other, and obtains
     * the (first) block hit
     *
     * @param startLocation to start ray tracing from, direction from Location
     * is used
     * @param maxLength limit of ray tracing
     * @return the hit Block, or null if none was found (AIR)
     */
    public static Block rayTraceBlock(Location startLocation, double maxLength) {
        return rayTraceBlock(startLocation, startLocation.getDirection(), maxLength);
    }

    /**
     * Obtains all the Block State tile entities available in a World
     *
     * @param world to get the Block States for
     * @return collection of Block States
     */
    public static Collection<BlockState> getBlockStates(org.bukkit.World world) {
        return new ConvertingList<BlockState>(NMSWorld.getTileList(world), DuplexConversion.blockState);
    }

    /**
     * Gets the current weather state set for a world
     * 
     * @param world to get the weather state for
     * @return weather state it is set to
     */
    public static WeatherState getWeatherState(org.bukkit.World world) {
        if (world.hasStorm()) {
            if (world.isThundering()) {
                return WeatherState.STORM;
            } else {
                return WeatherState.RAIN;
            }
        } else {
            return WeatherState.CLEAR;
        }
    }

    /**
     * Gets the duration of the current weather state, until the weather state
     * switches to the future state obtained from {@link #getFutureWeatherState(world)}
     * 
     * @param world to get the current weather duration for
     * @return duration in ticks
     */
    public static int getWeatherDuration(org.bukkit.World world) {
        if (world.hasStorm()) {
            return Math.min(world.getWeatherDuration(), world.getThunderDuration());
        } else {
            return world.getWeatherDuration();
        }
    }

    /**
     * Instantly switches the weather of a world to a new state.
     * This function resets the weather timers to random values, causing an unpredictable
     * future forecast. To set a predefined duration of this weather state, use
     * {@link #setWeatherDuration(world, durationInTicks)}
     * 
     * @param world to set the weather state for
     * @param state to set to
     */
    public static void setWeatherState(org.bukkit.World world, WeatherState state) {
        // reset 'clear' timer, otherwise we can not change weather!
        CommonNMS.getHandle(world).getWorldData().setClearTimer(0);

        if (state == WeatherState.CLEAR) {
            if (world.hasStorm()) {
                world.setStorm(false);
                world.setThundering(new Random().nextBoolean());
            }
        } else if (state == WeatherState.RAIN) {
            if (!world.hasStorm() || world.isThundering()) {
                world.setStorm(true);
                world.setThundering(false);
            }
        } else if (state == WeatherState.STORM) {
            if (!world.hasStorm() || !world.isThundering()) {
                world.setStorm(true);
                world.setThundering(true);
            }
        }
    }

    /**
     * Sets the weather duration of the current weather state, until it switches to a future state.
     * To make the current weather state last forever, specify a duration of Integer.MAX_VALUE.<br><br>
     * 
     * The future weather state will always go from clear <> rain/storm.
     * 
     * @param world to set the weather duration for
     * @param durationInTicks to set the duration to
     */
    public static void setWeatherDuration(org.bukkit.World world, int durationInTicks) {
        world.setWeatherDuration(durationInTicks);
        world.setThunderDuration(durationInTicks);
    }

    /**
     * Gets the very next weather state the world will switch to after the
     * time for the current weather state expires.
     * 
     * @param world to get the next weather state for
     * @return future weather state
     */
    public static WeatherState getFutureWeatherState(org.bukkit.World world) {
        int rainDuration = world.getWeatherDuration();
        int stormDuration = world.getThunderDuration();
        WeatherState currentState = getWeatherState(world);
        if (currentState == WeatherState.CLEAR) {
            // Rain or storm?
            boolean storm = world.isThundering();
            if (stormDuration < rainDuration) {
                storm = !storm;
            }
            return storm ? WeatherState.STORM : WeatherState.RAIN;
        } else if (currentState == WeatherState.RAIN) {
            // Clear weather, or will it turn into a storm?
            if (stormDuration < rainDuration) {
                return WeatherState.STORM;
            } else {
                return WeatherState.CLEAR;
            }
        } else if (currentState == WeatherState.STORM) {
            // Clear weather, or will the storm die down and turn into rainfall?
            if (stormDuration < rainDuration) {
                return WeatherState.RAIN;
            } else {
                return WeatherState.CLEAR;
            }
        }
        return WeatherState.CLEAR; // never reached
    }

    /**
     * Sets the very next weather state the world will switch to after the current
     * weather state expires. Attempts to set a future state the same as the current
     * one, or to a future state that it is already set to, are ignored.
     * 
     * @param world to set the future weather state for
     * @param state to set to
     */
    public static void setFutureWeatherState(org.bukkit.World world, WeatherState state) {
        WeatherState currentState = state;
        if (currentState == state || getFutureWeatherState(world) == state)
            return;

        int rainDuration = world.getWeatherDuration();
        int stormDuration = world.getThunderDuration();
        if (currentState == WeatherState.CLEAR) {
            // Got to toggle the 'thundering' state to make sure it is correct
            world.setThundering(!world.isThundering());
            world.setThunderDuration(stormDuration);
        } else if (currentState == WeatherState.RAIN) {
            if (state == WeatherState.CLEAR) {
                // Got to make sure it does not start thundering before the rain ends
                world.setThunderDuration(rainDuration + 1);
            } else if (state == WeatherState.STORM) {
                // Make sure that thunder happens after the rain finishes
                // Use a random thundering time based on MC values
                world.setThunderDuration(rainDuration);
                world.setWeatherDuration(rainDuration + new Random().nextInt(12000) + 3600);
            }
        } else if (currentState == WeatherState.STORM) {
            if (state == WeatherState.CLEAR) {
                // Got to make sure it does not stop thundering before the storm ends
                world.setThunderDuration(rainDuration + 1);
            } else if (state == WeatherState.RAIN) {
                // We know for certain the next state will not be rain, but clear weather
                // Make sure thundering is stopped at the time the rain is set to stop
                int durationOfRainAfterStorm = new Random().nextInt(12000) + 12000;
                world.setThunderDuration(rainDuration);
                world.setWeatherDuration(rainDuration + durationOfRainAfterStorm);
            }
        }
    }

}
