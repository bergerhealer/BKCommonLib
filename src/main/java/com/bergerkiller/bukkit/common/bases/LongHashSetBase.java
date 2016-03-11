package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.utils.WorldUtil;
import org.bukkit.Chunk;
import org.bukkit.World;

public class LongHashSetBase extends org.bukkit.craftbukkit.v1_9_R1.util.LongHashSet {

    public void addAllChunks(World world) {
        for (org.bukkit.Chunk chunk : WorldUtil.getChunks(world)) {
            addChunk(chunk);
        }
    }

    public void addChunk(Chunk chunk) {
        add(LongHash.toLong(chunk.getX(), chunk.getZ()));
    }
}
