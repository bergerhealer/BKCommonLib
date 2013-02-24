package com.bergerkiller.bukkit.common.wrappers;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.classes.BlockRef;

import net.minecraft.server.v1_4_R1.Block;

/**
 * Class implementation for Block Info that has a backing handle.
 * Override all methods here and perform block-specific logic instead.
 */
class BlockInfoImpl extends BlockInfo {
	private int id;
	
	public BlockInfoImpl(Object handle) {
		setHandle(handle);
	}

	@Override
	protected void setHandle(Object handle) {
		super.setHandle(handle);
		id = BlockRef.id.get(handle);
	}
	
	@Override
	public int getOpacity() {
		return Block.lightBlock[id];
	}

	@Override
	public int getLightEmission() {
		return Block.lightEmission[id];
	}

	@Override
	public boolean isSolid() {
		return BlockRef.isSolid.invoke(handle);
	}

	@Override
	public boolean isPowerSource() {
		return BlockRef.isPowerSource.invoke(handle);
	}

	@Override
	public void dropNaturally(World world, int x, int y, int z, int data, float yield, int chance) {
		BlockRef.dropNaturally.invoke(handle, Conversion.toWorldHandle.convert(world), x, y, z, data, yield, chance);
	}

	@Override
	public void ignite(World world, int x, int y, int z) {
		BlockRef.ignite.invoke(handle, Conversion.toWorldHandle.convert(world), x, y, z);
	}
}
