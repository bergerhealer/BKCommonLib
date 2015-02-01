package com.bergerkiller.bukkit.common.utils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R1.CraftTravelAgent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.CraftServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerChunkMapRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerChunkRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import net.minecraft.server.v1_8_R1.BlockPosition;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.IDataManager;
import net.minecraft.server.v1_8_R1.MovingObjectPosition;
import net.minecraft.server.v1_8_R1.Vec3D;
import net.minecraft.server.v1_8_R1.World;
import net.minecraft.server.v1_8_R1.WorldNBTStorage;
import net.minecraft.server.v1_8_R1.WorldServer;

public class WorldUtil extends ChunkUtil {

    private static final Object findSpawnDummyEntity = EntityPlayerRef.TEMPLATE.newInstanceNull();

    /**
     * Gets the block type Id
     *
     * @param world the block is in
     * @param blockPos of the block
     * @return block type Id
     */
    public static int getBlockTypeId(org.bukkit.World world, IntVector3 blockPos) {
        return getBlockTypeId(world, blockPos.x, blockPos.y, blockPos.z);
    }

    /**
     * Gets the block data
     *
     * @param world the block is in
     * @param blockPos of the block
     * @return block data
     */
    public static int getBlockData(org.bukkit.World world, IntVector3 blockPos) {
        return getBlockData(world, blockPos.x, blockPos.y, blockPos.z);
    }

    /**
     * Gets the block data
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return block data
     */
    public static int getBlockData(org.bukkit.World world, int x, int y, int z) {
        return ((Block) CommonNMS.getNative(world).getType(new BlockPosition(x, y, z))).getData();
    }

    /**
     * Gets the block type Id
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return block type Id
     * @deprecated returns -1
     */
    @Deprecated
    public static int getBlockTypeId(org.bukkit.World world, int x, int y, int z) {
        //return CommonNMS.getNative(world).getTypeId(x, y, z);
        return -1;
    }

    /**
     * Gets the block type
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return block type
     */
    public static org.bukkit.Material getBlockType(org.bukkit.World world, int x, int y, int z) {
        return MaterialUtil.getType(CommonNMS.getNative(world).getType(new BlockPosition(x, y, z)).getBlock().toString());
    }

    /**
     * Gets the shared Random of a world
     *
     * @param world to get the Random of
     * @return Random generator of a world
     */
    public static Random getRandom(org.bukkit.World world) {
        return CommonNMS.getNative(world).random;
    }

    /**
     * Sets if the spawn chunk area should be kept in memory
     *
     * @param world World to apply value on
     * @param value Keep in memory or not?
     */
    public static void setKeepSpawnInMemory(org.bukkit.World world, boolean value) {
        CommonNMS.getNative(world).keepSpawnInMemory = value;
    }

    /**
     * Removes a single entity from the world
     *
     * @param entity to remove
     */
    public static void removeEntity(org.bukkit.entity.Entity entity) {
        Entity e = CommonNMS.getNative(entity);
        e.world.removeEntity(e);
        WorldServerRef.entityTracker.get(e.world).stopTracking(entity);
    }

    /**
     * Removes a world from all global locations where worlds are mapped
     *
     * @param world to remove
     */
    public static void removeWorld(org.bukkit.World world) {
        // Remove the world from the Bukkit worlds mapping
        Iterator<org.bukkit.World> iter = CraftServerRef.worlds.values().iterator();
        while (iter.hasNext()) {
            if (iter.next() == world) {
                iter.remove();
            }
        }
        // Remove the world from the MinecraftServer worlds mapping
        CommonNMS.getWorlds().remove(CommonNMS.getNative(world));
    }

    /**
     * Obtains the internally stored collection of worlds<br>
     * Gets the values from the CraftServer.worlds map
     *
     * @return A collection of World instances
     */
    public static Collection<org.bukkit.World> getWorlds() {
        return CraftServerRef.worlds.values();
    }

    /**
     * Gets a live collection (allows modification in the world) of entities on
     * a given world
     *
     * @param world the entities are on
     * @return collection of entities on the world
     */
    public static Collection<org.bukkit.entity.Entity> getEntities(org.bukkit.World world) {
        return CommonNMS.getEntities(CommonNMS.getNative(world).entityList);
    }

    /**
     * Gets a live collection (allows modification in the world) of players on a
     * given world
     *
     * @param world the players are on
     * @return collection of players on the world
     */
    public static Collection<Player> getPlayers(org.bukkit.World world) {
        return CommonNMS.getPlayers(CommonNMS.getNative(world).players);
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
        WorldServer ws = CommonNMS.getNative(startLocation.getWorld());
        // Use a new travel agent to designate a proper position
        CraftTravelAgent travelAgent = new CraftTravelAgent(ws);
        travelAgent.setCanCreatePortal(createPortals);
        Location exit = travelAgent.findOrCreate(startLocation);
		// Adjust the exit to make it suitable for players
        // Note: this will raise an NPE while trying to fire the PortalExit event
        // This is expected behavior
        try {
            travelAgent.adjustExit((Entity) findSpawnDummyEntity, exit, new Vector(0, 0, 0));
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
        IDataManager man = CommonNMS.getNative(world).getDataManager();
        if (man instanceof WorldNBTStorage) {
            return ((WorldNBTStorage) man).getPlayerDir();
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
        return ((World) Conversion.toWorldHandle.convert(world)).worldProvider.getDimension();
    }

    /**
     * Gets the server a world object is running on
     *
     * @param world to get the server of
     * @return server
     */
    public static Server getServer(org.bukkit.World world) {
        return WorldServerRef.getServer(Conversion.toWorldHandle.convert(world));
    }

    /**
     * Gets the Entity Tracker for the world specified
     *
     * @param world to get the tracker for
     * @return world Entity Tracker
     */
    public static EntityTracker getTracker(org.bukkit.World world) {
        return WorldServerRef.entityTracker.get(Conversion.toWorldHandle.convert(world));
    }

    /**
     * Gets the tracker entry of the entity specified
     *
     * @param entity to get it for
     * @return entity tracker entry, or null if none is set
     */
    public static Object getTrackerEntry(org.bukkit.entity.Entity entity) {
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
    public static Object setTrackerEntry(org.bukkit.entity.Entity entity, Object entityTrackerEntry) {
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
        List<Entity> list = CommonNMS.getEntities(CommonNMS.getNative(world), CommonNMS.getNative(ignore), xmin, ymin, zmin, xmax, ymax, zmax);
        return new ConvertingList<org.bukkit.entity.Entity>(list, ConversionPairs.entity);
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
        return CommonNMS.getEntities(entity.getWorld(), entity, CommonNMS.getNative(entity).getBoundingBox().grow(radX, radY, radZ));
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
        final Vec3D vec = (Vec3D) Conversion.toVec3DHandle.convert(explosionPosition);
        return CommonNMS.getNative(explosionPosition.getWorld()).a(vec, CommonNMS.getNative(entity).getBoundingBox());
    }

    /**
     * Saves a world to disk, waiting until saving has completed before
     * returning. This may take significantly long. This method is cross-thread
     * supported.
     *
     * @param world to be saved
     */
    public static synchronized void saveToDisk(org.bukkit.World world) {
        CommonNMS.getNative(world).saveLevel();
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
        return CommonNMS.getNative(world).areChunksLoaded(new BlockPosition(blockCenterX, 0, blockCenterZ), distance);
    }

    public static void queueChunkSend(org.bukkit.Chunk chunk) {
        queueChunkSend(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Queues a chunk for sending to all players in view
     *
     * @param world the chunk is in
     * @param chunkX of the chunk
     * @param chunkZ of the chunk
     */
    public static void queueChunkSend(org.bukkit.World world, int chunkX, int chunkZ) {
        Object playerChunkMap = WorldServerRef.playerChunkMap.get(Conversion.toWorldHandle.convert(world));
        Object playerChunk = PlayerChunkMapRef.getPlayerChunk(playerChunkMap, chunkX, chunkZ);
        if (playerChunk == null) {
            return;
        }
        for (Player player : PlayerChunkRef.players.get(playerChunk)) {
            PlayerUtil.queueChunkSend(player, chunkX, chunkZ);
        }
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
        Object playerChunkMap = WorldServerRef.playerChunkMap.get(Conversion.toWorldHandle.convert(world));
        PlayerChunkMapRef.flagBlockDirty(playerChunkMap, blockX, blockY, blockZ);
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
        MovingObjectPosition mop = CommonNMS.getNative(world).rayTrace(CommonNMS.newVec3D(startX, startY, startZ),
                CommonNMS.newVec3D(endX, endY, endZ), false);
        return mop == null ? null : world.getBlockAt(mop.a().getX(), mop.a().getY(), mop.a().getZ());
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
}
