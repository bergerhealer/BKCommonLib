package com.bergerkiller.bukkit.common.bases;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.util.LongHash;

import com.bergerkiller.bukkit.common.utils.WorldUtil;

public class LongHashSetBase extends org.bukkit.craftbukkit.v1_8_R1.util.LongHashSet {

    public void addAllChunks(World world) {
        for (org.bukkit.Chunk chunk : WorldUtil.getChunks(world)) {
            addChunk(chunk);
        }
    }

    public void addChunk(Chunk chunk) {
        add(LongHash.toLong(chunk.getX(), chunk.getZ()));
    }
}
