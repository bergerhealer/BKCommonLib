package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.BlockRef;
import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.Explosion;
import net.minecraft.server.v1_9_R1.World;
import org.bukkit.entity.Entity;

/**
 * Class implementation for Block Info that has a backing handle. Override all
 * methods here and perform block-specific logic instead.
 */
class BlockInfoImpl extends BlockInfo {

    public BlockInfoImpl(Object handle) {
        setHandle(handle);
    }

    @Override
    public int getOpacity() {
        return getHandle(Block.class).o(getHandle(Block.class).getBlockData());
    }

    @Override
    public int getLightEmission() {
        return getHandle(Block.class).m(getHandle(Block.class).getBlockData());
    }

    @Override
    public boolean isSolid() {
        return getHandle(Block.class).d();
    }

    @Override
    public boolean isPowerSource() {
        return getHandle(Block.class).isPowerSource(getHandle(Block.class).getBlockData());
    }

    @Override
    public boolean isSuffocating() {
        return getHandle(Block.class).isOccluding(getHandle(Block.class).getBlockData());
    }

    @Override
    public float getDamageResilience(Entity source) {
        return getHandle(Block.class).a(CommonNMS.getNative(source));
    }

    @Override
    public void dropNaturally(org.bukkit.World world, int x, int y, int z, int data, float yield, int chance) {
        BlockRef.dropNaturally.invoke(handle, Conversion.toWorldHandle.convert(world), x, y, z, data, yield, chance);
    }

    @Override
    public void ignite(org.bukkit.World world, int x, int y, int z) {
        World worldhandle = CommonNMS.getNative(world);
        Explosion ex = new Explosion(worldhandle, null, x, y, z, (float) 4.0, true, true);
        BlockRef.ignite.invoke(handle, worldhandle, x, y, z, ex);
    }
}
