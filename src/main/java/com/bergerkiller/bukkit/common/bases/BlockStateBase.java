package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.proxies.BlockStateProxy;
import com.bergerkiller.server.Methods;

import org.bukkit.block.Block;

/**
 * A base class for a Block State implementation. Does not extend
 * CraftBlockState, this Class is meant as a copied replacement. (Why is there
 * no base class in the Bukkit API? How annoying.)
 */
public class BlockStateBase extends BlockStateProxy {

    public BlockStateBase(Block block) {
        super(Methods.CraftBlockState_new(block));
    }
}
