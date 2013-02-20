package com.bergerkiller.bukkit.common.bases;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.Conversion;

import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.PlayerChunkMap;
import net.minecraft.server.v1_4_R1.WorldServer;

public class PlayerChunkMapBase extends PlayerChunkMap {

	public PlayerChunkMapBase(World world, int viewDistace) {
		super((WorldServer) Conversion.toWorldHandle.convert(world), viewDistace);
	}

	/**
	 * @deprecated use {@link getWorld()} instead
	 */
	@Deprecated
	@Override
	public final WorldServer a() {
		return (WorldServer) Conversion.toWorldHandle.convert(this.getWorld());
	}

	/**
	 * @deprecated use {@link containsPlayer()} instead
	 */
	@Deprecated
	@Override
	public final boolean a(EntityPlayer entityplayer, int x, int z) {
		return this.containsPlayer(entityplayer, x, z);
	}

	/**
	 * @deprecated use {@link addChunksToSend()} instead
	 */
	@Deprecated
	@Override
	public final void b(EntityPlayer entityplayer) {
		this.addChunksToSend(entityplayer);
	}

	public void addChunksToSend(EntityPlayer entityplayer) {
		super.b(entityplayer);
	}

	/**
	 * Gets the world from this PlayerManager<br>
	 * Is called by the PlayerChunkInstance initializer as well
	 * 
	 * @return World
	 */
	public World getWorld() {
		return Conversion.toWorld.convert(super.a());
	}

	public boolean containsPlayer(EntityPlayer entityplayer, int chunkX, int chunkZ) {
		return super.a(entityplayer, chunkX, chunkZ);
	}
}
