package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

/**
 * Used for respawn points that make a player respawn near to a bed or world anchor
 * 
 * @see PlayerRespawnPoint#create(Block) 
 */
public class PlayerRespawnPointNearBlock extends PlayerRespawnPoint {
    private final EntityPlayerHandle.RespawnConfigHandle handle;
    private WeakReference<World> cachedWorld = LogicUtil.nullWeakReference();

    public PlayerRespawnPointNearBlock(ResourceKey<World> dimensionKey, int blockX, int blockY, int blockZ, float angle, boolean forced) {
        this(EntityPlayerHandle.RespawnConfigHandle.of(dimensionKey, new IntVector3(blockX, blockY, blockZ), angle, forced));
    }

    public PlayerRespawnPointNearBlock(World world, int blockX, int blockY, int blockZ, float angle, boolean forced) {
        this(EntityPlayerHandle.RespawnConfigHandle.of(world, new IntVector3(blockX, blockY, blockZ), angle, forced));
    }

    public PlayerRespawnPointNearBlock(EntityPlayerHandle.RespawnConfigHandle respawnConfig) {
        if (respawnConfig == null) {
            throw new IllegalArgumentException("RespawnConfig handle cannot be null");
        }
        this.handle = respawnConfig;
    }

    public EntityPlayerHandle.RespawnConfigHandle getHandle() {
        return handle;
    }

    /**
     * Gets the coordinates of the bed or respawn anchor block
     *
     * @return coordinates
     */
    public IntVector3 getBlockPosition() {
        return handle.position();
    }

    @Override
    public World getWorld() {
        World world = cachedWorld.get();
        if (world == null) {
            world = handle.world();
            if (world != null) {
                cachedWorld = new WeakReference<>(world);
            }
        }
        return world;
    }

    @Override
    public float getAngle() {
        return handle.angle();
    }

    /**
     * Gets the block of this respawn point, which will be the block
     * of the bed or respawn anchor. If {@link #NONE} or the
     * World is not loaded then null is returned.
     *
     * @return block
     */
    public Block getBlock() {
        World world = getWorld();
        return (world == null) ? null : getBlockPosition().toBlock(world);
    }

    /**
     * Gets the x-coordinate of the bed or respawn anchor block
     *
     * @return x-coordinate
     */
    public int getBlockX() {
        return getBlockPosition().x;
    }

    /**
     * Gets the y-coordinate of the bed or respawn anchor block
     *
     * @return y-coordinate
     */
    public int getBlockY() {
        return getBlockPosition().y;
    }

    /**
     * Gets the z-coordinate of the bed or respawn anchor block
     *
     * @return z-coordinate
     */
    public int getBlockZ() {
        return getBlockPosition().z;
    }

    /**
     * Gets whether the respawn near this block is forced. If so, no bed or respawn anchor
     * has to be at this block for the respawn to succeed. This is the case when the
     * /spawnpoint command is used by the Player.
     *
     * @return True if forced
     */
    public boolean isForced() {
        return handle.forced();
    }

    @Override
    public void applyToPlayer(Player player) {
        EntityPlayerHandle.fromBukkit(player).setRespawnConfigSilent(handle);
    }

    @Override
    public void toNBT(CommonTagCompound nbt) {
        if (CommonCapabilities.IS_RESPAWN_POINT_PACKED) {
            EntityPlayerHandle.RespawnConfigHandle.codecToNBT(handle, nbt);

            // Just in case...
            nbt.remove("SpawnWorld");
            nbt.remove("SpawnDimension");
            nbt.remove("SpawnX");
            nbt.remove("SpawnY");
            nbt.remove("SpawnZ");
            nbt.remove("SpawnAngle");
            nbt.remove("SpawnForced");
        } else {
            if (CommonCapabilities.PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY) {
                nbt.putMinecraftKey("SpawnDimension", WorldUtil.getDimensionKey(getWorld()).getName());
                nbt.remove("SpawnWorld");
            } else {
                nbt.putValue("SpawnWorld", getWorld().getName());
                nbt.remove("SpawnDimension");
            }
            IntVector3 blockPos = getBlockPosition();
            nbt.putValue("SpawnX", blockPos.x);
            nbt.putValue("SpawnY", blockPos.y);
            nbt.putValue("SpawnZ", blockPos.z);
            if (CommonCapabilities.PLAYER_SPAWN_HAS_ANGLE) {
                nbt.putValue("SpawnAngle", getAngle());
            }
            nbt.putValue("SpawnForced", isForced());
        }
    }

    @Override
    public Location findSafeSpawn(boolean alsoWhenDestroyed, boolean isDeathRespawn) {
        World world = getWorld();
        if (world == null) {
            return null;
        }
        return WorldServerHandle.fromBukkit(world).findSafeSpawn(this, alsoWhenDestroyed || isForced(), isDeathRespawn);
    }

    @Override
    public String toString() {
        IntVector3 blockPos = getBlockPosition();
        World world = getWorld();
        return "PlayerRespawnPointNearBlock{" +
                ((world == null) ? ("dimension=" + handle.dimension()) : ("world=" + world.getName())) +
                ", x=" + blockPos.x +", y=" + blockPos.y + ", z=" + blockPos.z +
                ", angle=" + getAngle() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PlayerRespawnPointNearBlock) {
            return ((PlayerRespawnPointNearBlock) o).handle.equals(handle);
        } else {
            return false;
        }
    }
}
