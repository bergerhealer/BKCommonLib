package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.proxies.BlockProxy;
import org.bukkit.Chunk;
<<<<<<< HEAD
import org.bukkit.craftbukkit.v1_9_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R1.block.CraftBlock;
=======
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
>>>>>>> 6c6809c31fa3f2895f50a974cd9b182317b26eb3

public class BlockBase extends BlockProxy {

    public BlockBase(Chunk chunk, int x, int y, int z) {
        super(new CraftBlock((CraftChunk) chunk, x, y, z));
    }
}
