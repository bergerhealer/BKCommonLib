package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.reflection.net.minecraft.server.NMSBlock;
import com.bergerkiller.server.CommonNMS;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.Explosion;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.entity.Entity;

/**
 * Class implementation for Block Info that has a backing handle. Override all
 * methods here and perform block-specific logic instead.
 */
class BlockInfoImpl extends BlockInfo {
    private Object defIBlockData;

    public BlockInfoImpl(Object handle) {
        setHandle(handle);
        this.defIBlockData = getHandle(Block.class).fromLegacyData(0);
    }

    @Override
    public Object getIBlockData() {
        return defIBlockData;
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
        NMSBlock.dropNaturally.invoke(handle, Conversion.toWorldHandle.convert(world), x, y, z, data, yield, chance);
    }

    @Override
    public void ignite(org.bukkit.World world, int x, int y, int z) {
        World worldhandle = CommonNMS.getNative(world);
        Explosion ex = new Explosion(worldhandle, null, x, y, z, (float) 4.0, true, true);
        NMSBlock.ignite.invoke(handle, worldhandle, x, y, z, ex);
    }
}
