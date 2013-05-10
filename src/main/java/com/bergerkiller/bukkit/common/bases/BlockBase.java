package com.bergerkiller.bukkit.common.bases;

import org.bukkit.Chunk;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.block.CraftBlock;

import com.bergerkiller.bukkit.common.proxies.BlockProxy;

public class BlockBase extends BlockProxy {

	public BlockBase(Chunk chunk, int x, int y, int z) {
		super(new CraftBlock((CraftChunk) chunk, x, y, z));
	}
}