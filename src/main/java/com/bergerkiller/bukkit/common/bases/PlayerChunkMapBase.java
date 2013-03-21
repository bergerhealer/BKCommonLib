package com.bergerkiller.bukkit.common.bases;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.PlayerChunkMap;
import net.minecraft.server.v1_5_R2.WorldServer;

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
		return CommonNMS.getNative(this.getWorld());
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
		this.addChunksToSend(CommonNMS.getPlayer(entityplayer));
	}

	/**
	 * @deprecated use {@link addChunksToSend()} instead
	 */
	@Deprecated
	@Override
	public void addPlayer(EntityPlayer arg0) {
		this.addPlayer(CommonNMS.getPlayer(arg0));
	}

	/**
	 * @deprecated use {@link addChunksToSend()} instead
	 */
	@Deprecated
	@Override
	public void movePlayer(EntityPlayer arg0) {
		this.movePlayer(CommonNMS.getPlayer(arg0));
	}

	/**
	 * @deprecated use {@link addChunksToSend()} instead
	 */
	@Deprecated
	@Override
	public void removePlayer(EntityPlayer arg0) {
		removePlayer(CommonNMS.getPlayer(arg0));
	}

	public void movePlayer(Player player) {
		super.movePlayer(CommonNMS.getNative(player));
	}

	public void addPlayer(Player player) {
		super.addPlayer(CommonNMS.getNative(player));
	}

	public void removePlayer(Player player) {
		super.removePlayer(CommonNMS.getNative(player));
	}

	public void addChunksToSend(Player player) {
		super.b(CommonNMS.getNative(player));
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
