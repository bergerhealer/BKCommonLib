package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.resources.ResourceKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;

/**
 * All the information of a bed (re)spawn point,
 * configured for a player.
 */
public abstract class PlayerRespawnPoint {

    /**
     * Default constant for unavailable respawn points. The world is null
     * and the x/y/z coordinates are 0. Can be applied to players or
     * saved to NBT, which will work as intended to clear the respawn point.
     */
    public static final PlayerRespawnPoint NONE = new PlayerRespawnPoint() {
        @Override
        public boolean isNone() {
            return true;
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public float getAngle() {
            return 0;
        }

        @Override
        public void toNBT(CommonTagCompound nbt) {
            nbt.removeValue("respawn");
            nbt.removeValue("SpawnWorld");
            nbt.removeValue("SpawnDimension");
            nbt.removeValue("SpawnForced");
            nbt.removeValue("SpawnX");
            nbt.removeValue("SpawnY");
            nbt.removeValue("SpawnZ");
            nbt.removeValue("SpawnAngle");
        }

        @Override
        public void applyToPlayer(Player player) {
            EntityPlayerHandle.fromBukkit(player).setRespawnConfigSilent(null);
        }

        @Override
        public Location findSafeSpawn() {
            return null;
        }

        @Override
        public Location findSafeSpawn(boolean alsoWhenDestroyed, boolean isDeathRespawn) {
            return null;
        }

        @Override
        public String toString() {
            return "NONE";
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }
    };

    /**
     * Creates a new player respawn point at the block specified.
     * A default angle of 0.0 is used.
     * If block is null, {@link #NONE} is returned.
     * 
     * @param block Near Block
     * @return respawn point at this block
     */
    public static PlayerRespawnPoint create(Block block) {
        return create(block, 0.0f);
    }

    /**
     * Creates a new player respawn point near the block specified.
     * If block is null, {@link #NONE} is returned.
     * 
     * @param block Near Block
     * @param angle Horizontal Angle
     * @return respawn point near this block
     */
    public static PlayerRespawnPoint create(Block block, float angle) {
        return create(block, angle, false);
    }

    /**
     * Creates a new player respawn point near the block specified.
     * If block is null, {@link #NONE} is returned.
     *
     * @param block Near Block
     * @param angle Horizontal Angle
     * @param forced Whether the respawn point near this block succeeds even if no bed or respawn anchor
     *               is nearby
     * @return respawn point near this block
     */
    public static PlayerRespawnPoint create(Block block, float angle, boolean forced) {
        if (block == null) {
            return NONE;
        } else {
            return new PlayerRespawnPointNearBlock(block.getWorld(), block.getX(), block.getY(), block.getZ(), angle, forced);
        }
    }

    /**
     * Creates a new player respawn point for near the block on the world and coordinates
     * specified.
     * A default angle of 0.0 is used.
     * If world is null, {@link #NONE} is returned.
     *
     * @param world World
     * @param x Near-Block X
     * @param y Near-Block Y
     * @param z Near-Block Z
     * @return respawn point near this block
     */
    public static PlayerRespawnPoint create(World world, int x, int y, int z) {
        return create(world, x, y, z, 0.0f);
    }

    /**
     * Creates a new player respawn point for near the block on the world and coordinates
     * specified.
     * If world is null, {@link #NONE} is returned.
     *
     * @param world World
     * @param x Near-Block X
     * @param y Near-Block Y
     * @param z Near-Block Z
     * @return respawn point near this block
     */
    public static PlayerRespawnPoint create(World world, int x, int y, int z, float angle) {
        return create(world, x, y, z, angle, false);
    }

    /**
     * Creates a new player respawn point for near the block on the world and coordinates
     * specified.
     * If world is null, {@link #NONE} is returned.
     *
     * @param world World
     * @param x Near-Block X
     * @param y Near-Block Y
     * @param z Near-Block Z
     * @param forced Whether the respawn point near this block succeeds even if no bed or respawn anchor
     *               is nearby
     * @return respawn point near this block
     */
    public static PlayerRespawnPoint create(World world, int x, int y, int z, float angle, boolean forced) {
        if (world == null) {
            return NONE;
        } else {
            return new PlayerRespawnPointNearBlock(world, x, y, z, angle, forced);
        }
    }

    /**
     * Creates a new player respawn point for near the block on the world and coordinates
     * specified.
     * If world is null, {@link #NONE} is returned.
     *
     * @param dimensionKey Resource key identity of the World (offline reference)
     * @param x Near-Block X
     * @param y Near-Block Y
     * @param z Near-Block Z
     * @param forced Whether the respawn point near this block succeeds even if no bed or respawn anchor
     *               is nearby
     * @return respawn point near this block
     */
    public static PlayerRespawnPoint create(ResourceKey<World> dimensionKey, int x, int y, int z, float angle, boolean forced) {
        if (dimensionKey == null) {
            return NONE;
        } else {
            return new PlayerRespawnPointNearBlock(dimensionKey, x, y, z, angle, forced);
        }
    }

    /**
     * Gets whether this spawn point is {@link #NONE}
     * 
     * @return True if none
     */
    public boolean isNone() {
        return false;
    }

    /**
     * Gets the world in which the bed or respawn anchor is located
     * 
     * @return world
     */
    public abstract World getWorld();

    /**
     * Gets the horizontal rotation angle of the player.
     * Not used before Minecraft 1.16.2.
     * 
     * @return rotation angle
     */
    public abstract float getAngle();

    /**
     * Encodes this player respawn point and saves it to NBT, formatted in the
     * same way player data is stored. If this respawn point is {@link #NONE},
     * then all relevant fields are removed.
     * 
     * @param nbt NBT to write this respawn point configuration to
     */
    public abstract void toNBT(CommonTagCompound nbt);

    /**
     * Applies this respawn point configuration to a player. If {@link #NONE}, then
     * the respawn point is reset to nothing.
     * 
     * @param player The player to apply this respawn point to
     */
    public abstract void applyToPlayer(Player player);

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
    public abstract Location findSafeSpawn(boolean alsoWhenDestroyed, boolean isDeathRespawn);

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

        // Read the new packed format as of 1.21.5
        if (CommonCapabilities.PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY) {
            EntityPlayerHandle.RespawnConfigHandle respawnConfig = EntityPlayerHandle.RespawnConfigHandle.codecFromNBT(nbt);
            if (respawnConfig != null) {
                return new PlayerRespawnPointNearBlock(respawnConfig);
            }
        }

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
        return create(world, x, y, z, angle);
    }

    /**
     * Reads the respawn point set for a particular player. Returns {@link #NONE} if none is set.
     * 
     * @param player The player to read the respawn point from
     * @return respawn point set for the player
     */
    public static PlayerRespawnPoint forPlayer(Player player) {
        EntityPlayerHandle handle = EntityPlayerHandle.fromBukkit(player);
        EntityPlayerHandle.RespawnConfigHandle respawnConfig = handle.getRespawnConfig();
        if (respawnConfig != null) {
            return new PlayerRespawnPointNearBlock(respawnConfig);
        }

        return NONE;
    }
}
