package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Used for respawn points that make a player respawn near to a bed or world anchor
 * 
 * @see PlayerRespawnPoint#create(Block) 
 */
public class PlayerRespawnPointNearBlock extends PlayerRespawnPoint {
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final boolean forced;

    protected PlayerRespawnPointNearBlock(World world, int blockX, int blockY, int blockZ, float angle, boolean forced) {
        super(world, angle);
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.forced = forced;
    }

    /**
     * Gets the block of this respawn point, which will be the block
     * of the bed or respawn anchor. If {@link #NONE} then null is returned.
     *
     * @return block
     */
    public Block getBlock() {
        return this.getWorld().getBlockAt(this.blockX, this.blockY, this.blockZ);
    }

    /**
     * Gets the x-coordinate of the bed or respawn anchor block
     *
     * @return x-coordinate
     */
    public int getBlockX() {
        return this.blockX;
    }

    /**
     * Gets the y-coordinate of the bed or respawn anchor block
     *
     * @return y-coordinate
     */
    public int getBlockY() {
        return this.blockY;
    }

    /**
     * Gets the z-coordinate of the bed or respawn anchor block
     *
     * @return z-coordinate
     */
    public int getBlockZ() {
        return this.blockZ;
    }

    /**
     * Gets whether the respawn near this block is forced. If so, no bed or respawn anchor
     * has to be at this block for the respawn to succeed. This is the case when the
     * /spawnpoint command is used by the Player.
     *
     * @return True if forced
     */
    public boolean isForced() {
        return forced;
    }

    @Override
    public void applyToPlayer(Player player) {
        EntityPlayerHandle handle = EntityPlayerHandle.fromBukkit(player);
        handle.setSpawnWorld(getWorld());
        handle.setSpawnCoord(new IntVector3(getBlockX(), getBlockY(), getBlockZ()));
        handle.setSpawnAngle(getAngle());
        handle.setSpawnForced(isForced());
    }

    @Override
    public void toNBT(CommonTagCompound nbt) {
        if (CommonCapabilities.PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY) {
            nbt.putMinecraftKey("SpawnDimension", WorldUtil.getDimensionKey(getWorld()).getName());
        } else {
            nbt.putValue("SpawnWorld", getWorld().getName());
        }
        nbt.putValue("SpawnX", getBlockX());
        nbt.putValue("SpawnY", getBlockY());
        nbt.putValue("SpawnZ", getBlockZ());
        if (CommonCapabilities.PLAYER_SPAWN_HAS_ANGLE) {
            nbt.putValue("SpawnAngle", getAngle());
        }
        nbt.putValue("SpawnForced", isForced());
    }

    @Override
    public Location findSafeSpawn(boolean alsoWhenDestroyed, boolean isDeathRespawn) {
        return WorldServerHandle.fromBukkit(getWorld()).findSafeSpawn(this, alsoWhenDestroyed || isForced(), isDeathRespawn);
    }

    @Override
    public String toString() {
        return "PlayerRespawnPointNearBlock{world=" + getWorld().getName() +
                ", x=" + blockX +", y=" + blockY + ", z=" + blockZ +
                ", angle=" + getAngle() + "}";
    }
}
