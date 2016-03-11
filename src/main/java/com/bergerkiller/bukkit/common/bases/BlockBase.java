package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.proxies.BlockProxy;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_9_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R1.block.CraftBlock;

public class BlockBase extends BlockProxy {

    public BlockBase(Chunk chunk, int x, int y, int z) {
        super(new CraftBlock((CraftChunk) chunk, x, y, z));
    }
}
