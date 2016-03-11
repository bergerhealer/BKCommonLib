package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.proxies.BlockStateProxy;
import org.bukkit.block.Block;
<<<<<<< HEAD
import org.bukkit.craftbukkit.v1_9_R1.block.CraftBlockState;
=======
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
>>>>>>> 6c6809c31fa3f2895f50a974cd9b182317b26eb3

/**
 * A base class for a Block State implementation. Does not extend
 * CraftBlockState, this Class is meant as a copied replacement. (Why is there
 * no base class in the Bukkit API? How annoying.)
 */
public class BlockStateBase extends BlockStateProxy {

    public BlockStateBase(Block block) {
        super(new CraftBlockState(block));
    }
}
