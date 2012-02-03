package com.bergerkiller.bukkit.common;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.bergerkiller.bukkit.common.utils.WorldUtil;

@SuppressWarnings("unchecked")
public abstract class Operation extends ParameterWrapper {
	
	public Operation() {
		this(true);
	}
	public Operation(boolean run, final Object... arguments) {
		super(arguments);
		if (run) this.run();
	}
	
	public abstract void run();
	
	private void multiAccess(final String location) {
		System.out.println("The " + location + " got accessed by more than one thread or got modified while operating on it!");
	}
	
	public final void doPlayers() {
		try {
			for (EntityPlayer player : (List<EntityPlayer>) ((CraftServer) Bukkit.getServer()).getHandle().players) {
				handle(player);
				if (player.netServerHandler == null) continue;
				if (player.netServerHandler.player != player) continue;
				handle(player.netServerHandler.getPlayer());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server player list");
		}
	}
	
	public final void doWorlds() {
		try {
			for (WorldServer world : WorldUtil.getWorlds()) {
				handle(world);
				handle(world.getWorld());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server world list");
		}
	}

	public final void doPlayers(World world) {
		try {
			for (EntityPlayer player : (List<EntityPlayer>) world.players) {
				handle(player);
				if (player.netServerHandler == null) continue;
				if (player.netServerHandler.player != player) continue;
				handle(player.netServerHandler.getPlayer());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("world player list of world '" + world.getWorld().getName() + "'");
		}
	}

	public final void doEntities(World world) {
		try {
			for (Entity e : (List<Entity>) world.entityList) {
				handle(e);
				handle((CraftEntity) e.getBukkitEntity());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("world entity list of world '" + world.getWorld().getName() + "'");
		}
	}

	public final void doChunks(World world) {
		this.doChunks(((WorldServer) world).chunkProviderServer);
	}
	public final void doChunks(ChunkProviderServer chunkProvider) {
		try {
			for (Chunk chunk : (List<Chunk>) chunkProvider.chunkList) {
				handle(chunk);
				if (chunk.bukkitChunk == null) continue;
				handle((CraftChunk) chunk.bukkitChunk);
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("world chunk list of world '" + chunkProvider.world.getWorld().getName() + "'");
		}
	}
		
	public void handle(WorldServer world) {};
	public void handle(CraftWorld world) {};
	public void handle(EntityPlayer player) {};
	public void handle(CraftPlayer player) {};
	public void handle(Entity entity) {};
	public void handle(CraftEntity entity) {};
	public void handle(Chunk chunk) {};
	public void handle(CraftChunk chunk) {};

}
