package com.bergerkiller.bukkit.common.bases;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PlayerChunkMap;
import net.minecraft.server.WorldServer;

public class PlayerChunkMapBase extends PlayerChunkMap {

	public PlayerChunkMapBase(World world, int viewDistace) {
		super((WorldServer) Conversion.toWorldHandle.convert(world), viewDistace);
	}

	/**
	 * @deprecated use {@link #getWorld()} instead
	 */
	@Deprecated
	@Override
	public final WorldServer a() {
		return CommonNMS.getNative(this.getWorld());
	}

	/**
	 * @deprecated use {@link #containsPlayer(Player, int, int) containsPlayer(player, x, z)} instead
	 */
	@Deprecated
	@Override
	public final boolean a(EntityPlayer entityplayer, int x, int z) {
		return this.containsPlayer(CommonNMS.getPlayer(entityplayer), x, z);
	}

	/**
	 * @deprecated use {@link #addChunksToSend(Player)} instead
	 */
	@Deprecated
	@Override
	public final void b(EntityPlayer entityplayer) {
		this.addChunksToSend(CommonNMS.getPlayer(entityplayer));
	}

	/**
	 * @deprecated use {@link #addPlayer(Player)} instead
	 */
	@Deprecated
	@Override
	public void addPlayer(EntityPlayer arg0) {
		this.addPlayer(CommonNMS.getPlayer(arg0));
	}

	/**
	 * @deprecated use {@link #movePlayer(Player)} instead
	 */
	@Deprecated
	@Override
	public void movePlayer(EntityPlayer arg0) {
		this.movePlayer(CommonNMS.getPlayer(arg0));
	}

	/**
	 * @deprecated use {@link #removePlayer(Player)} instead
	 */
	@Deprecated
	@Override
	public void removePlayer(EntityPlayer arg0) {
		removePlayer(CommonNMS.getPlayer(arg0));
	}

	/**
	 * Updates player movement
	 * 
	 * @param player to update
	 */
	public void movePlayer(Player player) {
		super.movePlayer(CommonNMS.getNative(player));
	}

	/**
	 * Adds a new player
	 * 
	 * @param player to add
	 */
	public void addPlayer(Player player) {
		super.addPlayer(CommonNMS.getNative(player));
	}

	/**
	 * Removes an existing player
	 * 
	 * @param player to remove
	 */
	public void removePlayer(Player player) {
		super.removePlayer(CommonNMS.getNative(player));
	}

	/**
	 * Adds all chunks near a player to the chunk sending queue of a player
	 * 
	 * @param player to add the chunks to send to
	 */
	public void addChunksToSend(Player player) {
		super.b(CommonNMS.getNative(player));
	}

	/**
	 * Gets whether a player is registered for a Chunk.
	 * If this is the case, the player is liable for entity or block
	 * updates from entities or blocks in the chunk.
	 * 
	 * @param player to check
	 * @param chunkX of the Chunk
	 * @param chunkZ of the Chunk
	 * @return True if the player is contained, False if not
	 */
	public boolean containsPlayer(Player player, int chunkX, int chunkZ) {
		return super.a(CommonNMS.getNative(player), chunkX, chunkZ);
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
}
