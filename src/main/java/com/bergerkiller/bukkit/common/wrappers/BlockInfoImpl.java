package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.reflection.net.minecraft.server.NMSBlock;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.Explosion;
import net.minecraft.server.v1_11_R1.IBlockData;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.entity.Entity;

/**
 * Class implementation for Block Info that has a backing handle. Override all
 * methods here and perform block-specific logic instead.
 */
class BlockInfoImpl extends BlockInfo {
    private final Block block;
    private final IBlockData data;

    public BlockInfoImpl(Object handle) {
        setHandle(handle);
        this.block = getHandle(Block.class);
        this.data = this.block.getBlockData();
    }

    @Override
    public Object getIBlockData() {
        return data;
    }

    @Override
    public int getOpacity() {
        return block.m(data);
    }

    @Override
    public int getLightEmission() {
        return block.o(data);
    }

    @Override
    public boolean isSolid() {
        return block.isOccluding(data);
    }

    @Override
    public boolean isPowerSource() {
        return block.isPowerSource(data);
    }

    @Override
    public boolean isSuffocating() {
        return block.isOccluding(data);
    }

    @Override
    public float getDamageResilience(Entity source) {
        return block.a(CommonNMS.getNative(source));
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
