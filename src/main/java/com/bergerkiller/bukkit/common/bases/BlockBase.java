package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.proxies.BlockProxy;
import com.bergerkiller.server.Methods;

import org.bukkit.Chunk;

public class BlockBase extends BlockProxy {

    public BlockBase(Chunk chunk, int x, int y, int z) {
        super(Methods.CraftBlock_new(chunk, x, y, z));
    }
}
