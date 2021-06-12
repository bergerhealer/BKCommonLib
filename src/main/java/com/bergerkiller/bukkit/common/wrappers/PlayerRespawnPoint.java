package com.bergerkiller.bukkit.common.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;

/**
 * All the information of a bed (re)spawn point,
 * configured for a player.
 */
public class PlayerRespawnPoint {
    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private final float angle;

    /**
     * Default constant for unavailable respawn points. The world is null
     * and the x/y/z coordinates are 0. Can be applied to players or
     * saved to NBT, which will work as intended to clear the respawn point.
     */
    public static final PlayerRespawnPoint NONE = new PlayerRespawnPoint(null, 0, 0, 0, 0.0f);

    /**
     * Creates a new player respawn point at the block specified.
     * A default angle of 0.0 is used.
     * If block is null, {@link #NONE} is returned.
     * 
     * @param block
     * @return respawn point at this block
     */
    public static PlayerRespawnPoint create(Block block) {
        return create(block, 0.0f);
    }

    /**
     * Creates a new player respawn point at the block specified.
     * If block is null, {@link #NONE} is returned.
     * 
     * @param block
     * @param angle
     * @return respawn point at this block
     */
    public static PlayerRespawnPoint create(Block block, float angle) {
        if (block == null) {
            return NONE;
        } else {
            return new PlayerRespawnPoint(block.getWorld(), block.getX(), block.getY(), block.getZ(), angle);
        }
    }

    /**
     * Creates a new player respawn point for the block on the world and coordinates
     * specified.
     * A default angle of 0.0 is used.
     * If world is null, {@link #NONE} is returned.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return respawn point at this block
     */
    public static PlayerRespawnPoint create(World world, int x, int y, int z) {
        return create(world, x, y, z, 0.0f);
    }

    /**
     * Creates a new player respawn point for the block on the world and coordinates
     * specified.
     * If world is null, {@link #NONE} is returned.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return respawn point at this block
     */
    public static PlayerRespawnPoint create(World world, int x, int y, int z, float angle) {
        if (world == null) {
            return NONE;
        } else {
            return new PlayerRespawnPoint(world, x, y, z, angle);
        }
    }

    protected PlayerRespawnPoint(World world, int x, int y, int z, float angle) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = Float.isNaN(angle) ? 0.0f : angle;
    }

    /**
     * Gets whether this spawn point is {@link #NONE}
     * 
     * @return True if none
     */
    public boolean isNone() {
        return this.world == null;
    }

    /**
     * Gets the block of this respawn point, which will be the block
     * of the bed or respawn anchor. If {@link #NONE} then null is returned.
     * 
     * @return block
     */
    public Block getBlock() {
        return (this.world == null) ? null : this.world.getBlockAt(this.x, this.y, this.z);
    }

    /**
     * Gets the world in which the bed or respawn anchor is located
     * 
     * @return world
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Gets the x-coordinate of the bed or respawn anchor block
     * 
     * @return x-coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of the bed or respawn anchor block
     * 
     * @return y-coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Gets the z-coordinate of the bed or respawn anchor block
     * 
     * @return z-coordinate
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Gets the horizontal rotation angle of the player.
     * Not used before Minecraft 1.16.2.
     * 
     * @return rotation angle
     */
    public float getAngle() {
        return this.angle;
    }

    /**
     * Encodes this player respawn point and saves it to NBT, formatted in the
     * same way player data is stored. If this respawn point is {@link #NONE},
     * then all relevant fields are removed.
     * 
     * @param nbt
     */
    public void toNBT(CommonTagCompound nbt) {
        if (this.isNone()) {
            nbt.removeValue("SpawnWorld");
            nbt.removeValue("SpawnDimension");
            nbt.removeValue("SpawnForced");
            nbt.removeValue("SpawnX");
            nbt.removeValue("SpawnY");
            nbt.removeValue("SpawnZ");
            nbt.removeValue("SpawnAngle");
        } else {
            if (CommonCapabilities.PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY) {
                nbt.putMinecraftKey("SpawnDimension", WorldUtil.getDimensionKey(getWorld()).getName());
            } else {
                nbt.putValue("SpawnWorld", getWorld().getName());
            }
            nbt.putValue("SpawnX", getX());
            nbt.putValue("SpawnY", getY());
            nbt.putValue("SpawnZ", getZ());
            if (CommonCapabilities.PLAYER_SPAWN_HAS_ANGLE) {
                nbt.putValue("SpawnAngle", getAngle());
            }
        }
    }

    /**
     * Applies this respawn point configuration to a player. If {@link #NONE}, then
     * the respawn point is reset to nothing.
     * 
     * @param player The player to apply this respawn point to
     */
    public void applyToPlayer(Player player) {
        EntityPlayerHandle handle = EntityPlayerHandle.fromBukkit(player);
        if (getWorld() == null) {
            handle.setSpawnWorld(null);
            handle.setSpawnCoord(null);
        } else {
            handle.setSpawnWorld(getWorld());
            handle.setSpawnCoord(new IntVector3(getX(), getY(), getZ()));
            handle.setSpawnAngle(getAngle());
        }
    }

    /**
     * Checks the world for a suitable place to spawn a player that uses this respawn point.
     * Returns null if no suitable location could be found.<br>
     * <br>
     * This is presumed to be a passive operation, that is, isDeathRespawn is false and
     * no value is returned when the bed or respawn anchor is destroyed.
     * 
     * @return safe spawn point, or null if none is available
     */
    public Location findSafeSpawn() {
        return findSafeSpawn(false, false);
    }

    /**
     * Checks the world for a suitable place to spawn a player that uses this respawn point.
     * Returns null if no suitable location could be found.
     * 
     * @param alsoWhenDestroyed Returns the location on top of the block if
     *        the bed or respawn anchor is destroyed
     * @param isDeathRespawn Whether this is a respawn that was caused by death. When this
     *        is true, and a respawn anchor was used, one tick is removed from it.
     * @return safe spawn point, or null if none is available
     */
    public Location findSafeSpawn(boolean alsoWhenDestroyed, boolean isDeathRespawn) {
        if (this.isNone()) {
            return null;
        } else {
            return WorldServerHandle.fromBukkit(getWorld()).findSafeSpawn(this, alsoWhenDestroyed, isDeathRespawn);
        }
    }

    /**
     * Decodes the player respawn point from NBT, formatted in the
     * same way player data is stored. If no player respawn point
     * is configured in the NBT, then the {@link #NONE} constant is returned.
     * 
     * @param nbt NBT Tag Compound to decode from
     * @return Player Respawn Point, {@link #NONE} if none or incomplete data is available
     */
    public static PlayerRespawnPoint fromNBT(CommonTagCompound nbt) {
        World world = null;

        // Read the SpawnDimension in the new format
        if (CommonCapabilities.PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY) {
            MinecraftKeyHandle spawnDimensionName = nbt.getMinecraftKey("SpawnDimension");
            if (spawnDimensionName != null) {
                world = WorldUtil.getWorldByDimensionKey(ResourceCategory.dimension.createKey(spawnDimensionName));
            }
        }

        // Migrate "SpawnWorld" name to "SpawnDimension" if needed when dimension key is used
        if (world == null && nbt.containsKey("SpawnWorld")) {
            world = Bukkit.getWorld(nbt.getValue("SpawnWorld", String.class));
        }

        // If world or other essential fields are missing, return NONE
        if (world == null ||
            !nbt.containsKey("SpawnX") ||
            !nbt.containsKey("SpawnY") ||
            !nbt.containsKey("SpawnZ"))
        {
            return NONE;
        }

        // Decode further
        int x = nbt.getValue("SpawnX", 0);
        int y = nbt.getValue("SpawnY", 0);
        int z = nbt.getValue("SpawnZ", 0);
        float angle = nbt.getValue("SpawnAngle", 0.0f);
        return new PlayerRespawnPoint(world, x, y, z, angle);
    }

    /**
     * Reads the respawn point set for a particular player. Returns {@link #NONE} if none is set.
     * 
     * @param player The player to read the respawn point from
     * @return respawn point set for the player
     */
    public static PlayerRespawnPoint forPlayer(Player player) {
        EntityPlayerHandle handle = EntityPlayerHandle.fromBukkit(player);
        World world = handle.getSpawnWorld();
        if (world == null) {
            return NONE;
        }
        IntVector3 coord = handle.getSpawnCoord();
        if (coord == null) {
            return NONE;
        }
        float angle = handle.getSpawnAngle();
        return new PlayerRespawnPoint(world, coord.x, coord.y, coord.z, angle);
    }
}
