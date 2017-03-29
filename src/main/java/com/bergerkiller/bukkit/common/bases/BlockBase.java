package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.internal.CommonMethods;
import com.bergerkiller.bukkit.common.proxies.BlockProxy;

import org.bukkit.Chunk;

public class BlockBase extends BlockProxy {

    public BlockBase(Chunk chunk, int x, int y, int z) {
        super(CommonMethods.CraftBlock_new(chunk, x, y, z));
    }
}
