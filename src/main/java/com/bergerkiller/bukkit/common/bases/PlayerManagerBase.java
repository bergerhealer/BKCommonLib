package com.bergerkiller.bukkit.common.bases;

import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.PlayerChunkMap;
import net.minecraft.server.v1_4_R1.WorldServer;

public class PlayerManagerBase extends PlayerChunkMap {

	public PlayerManagerBase(WorldServer worldserver, int viewDistace) {
		super(worldserver, viewDistace);
	}

	@Override
	public final WorldServer a() {
		return this.getWorld();
	}

	@Override
	public final boolean a(EntityPlayer entityplayer, int x, int z) {
		return this.containsPlayer(entityplayer, x, z);
	}

	@Override
	public final void b(EntityPlayer entityplayer) {
		this.addChunksToSend(entityplayer);
	}

	public void addChunksToSend(EntityPlayer entityplayer) {
		super.b(entityplayer);
	}

	public WorldServer getWorld() {
		return super.a();
	}

	public boolean containsPlayer(EntityPlayer entityplayer, int chunkX, int chunkZ) {
		return super.a(entityplayer, chunkX, chunkZ);
	}
}
