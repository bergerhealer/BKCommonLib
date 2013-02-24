package com.bergerkiller.bukkit.common.bases;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftBlockState;

/**
 * A base class for a Block State implementation
 */
public class BlockStateBase extends CraftBlockState {

	public BlockStateBase(Block block) {
		super(block);
	}
}
