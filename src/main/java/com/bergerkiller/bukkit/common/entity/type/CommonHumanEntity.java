package com.bergerkiller.bukkit.common.entity.type;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle;

/**
 * A Common Entity implementation for Human Entities
 */
public class CommonHumanEntity<T extends HumanEntity> extends CommonLivingEntity<T> {

    public CommonHumanEntity(T entity) {
        super(entity);
    }

    /**
     * Gets the block location of the respawn point for the human entity.
     * If none is available, null is returned instead.
     * 
     * @return spawn point coordinates, or null if none are available
     */
    public BlockLocation getSpawnPoint() {
        Object handle = getHandle();
        String world = EntityHumanHandle.T.spawnWorld.get(handle);
        IntVector3 coords = EntityHumanHandle.T.spawnCoord.get(handle);
        if (world != null && coords != null && !world.isEmpty()) {
            return new BlockLocation(world, coords);
        } else {
            return null;
        }
    }

    /**
     * Sets the block location of the respawn point for the human entity.
     * To clear the respawn point and set it to 'none', set it to null.
     * 
     * @param spawnPoint to set to
     */
    public void setSpawnPoint(BlockLocation spawnPoint) {
        Object handle = getHandle();
        IntVector3 coord = null;
        String world = "";
        if (spawnPoint != null) {
            world = spawnPoint.world;
            coord = spawnPoint.getCoordinates();
        }
        EntityHumanHandle.T.spawnWorld.set(handle, world);
        EntityHumanHandle.T.spawnCoord.set(handle, coord);
    }

}
