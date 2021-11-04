package com.bergerkiller.bukkit.common.internal.map;

import org.bukkit.World;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityItemFrameHandle;

/**
 * When clustering item frames (finding neighbours), this key is used
 * to store a mapping of what item frames exist on the server
 */
class ItemFrameClusterKey {
    public final World world;
    public final BlockFace facing;
    public final int coordinate;

    public ItemFrameClusterKey(EntityItemFrameHandle itemFrame) {
        this(itemFrame.getBukkitWorld(), itemFrame.getFacing(), itemFrame.getBlockPosition());
    }

    public ItemFrameClusterKey(World world, BlockFace facing, IntVector3 coordinates) {
        this.world = world;
        this.facing = facing;
        this.coordinate = facing.getModX()*coordinates.x +
                          facing.getModY()*coordinates.y +
                          facing.getModZ()*coordinates.z;
    }

    @Override
    public int hashCode() {
        return this.coordinate + (facing.ordinal()<<6);
    }

    @Override
    public boolean equals(Object o) {
        ItemFrameClusterKey other = (ItemFrameClusterKey) o;
        return other.coordinate == this.coordinate && (other.world == this.world || other.world.equals(this.world)) && other.facing == this.facing;
    }
}