package com.bergerkiller.bukkit.common.bases;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_4_R1.util.LongHash;

import com.bergerkiller.bukkit.common.utils.WorldUtil;

public class LongHashSet extends org.bukkit.craftbukkit.v1_4_R1.util.LongHashSet {
	public void addAllChunks(World world) {
		for (org.bukkit.Chunk chunk : WorldUtil.getChunks(world)) {
			add(LongHash.toLong(chunk.getX(), chunk.getZ()));
		}
	}
}
