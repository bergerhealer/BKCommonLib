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
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.utils.WorldUtil;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class Operation extends ParameterWrapper {
	
	public Operation() {
		this.run();
	}
	public Operation(final Object... arguments) {
		this(true, arguments);
	}
	public Operation(boolean run, final Object... arguments) {
		super(arguments);
		if (run) this.run();
	}
	
	public abstract void run();
	
	private void multiAccess(final String location) {
		System.out.println("The " + location + " got accessed by more than one thread or got modified while operating on it!");
	}
		
	public final void doWorlds() {
		try {
			for (WorldServer world : WorldUtil.getWorlds()) {
				this.handle(world);
				this.handle(world.getWorld());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server world list");
		}
	}

	public final void doPlayers() {
		try {
			for (EntityPlayer player : (List<EntityPlayer>) ((CraftServer) Bukkit.getServer()).getHandle().players) {
				this.handle(player);
				if (player.netServerHandler == null) continue;
				if (player.netServerHandler.player != player) continue;
				this.handle(player.netServerHandler.getPlayer());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server player list");
		}
	}
	public final void doPlayers(World world) {
		try {
			for (EntityPlayer player : (List<EntityPlayer>) world.players) {
				this.handle(player);
				if (player.netServerHandler == null) continue;
				if (player.netServerHandler.player != player) continue;
				this.handle(player.netServerHandler.getPlayer());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("world player list of world '" + world.getWorld().getName() + "'");
		}
	}

	public final void doEntities() {
		try {
			for (WorldServer world : WorldUtil.getWorlds()) {
				this.doEntities(world);
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server world list");
		}
	}
	public final void doEntities(World world) {
		try {
			for (Entity e : (List<Entity>) world.entityList) {
				this.handle(e);
				this.handle((CraftEntity) e.getBukkitEntity());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("world entity list of world '" + world.getWorld().getName() + "'");
		}
	}
	public final void doEntities(Chunk chunk) {
		try {
			for (List list : chunk.entitySlices) {
				for (Entity e : (List<Entity>) list) {
					this.handle(e);
					this.handle((CraftEntity) e.getBukkitEntity());
				}
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("chunk entity list of world '" + chunk.world.getWorld().getName() + "'");
		}
	}

	public final void doChunks() {
		try {
			for (WorldServer world : WorldUtil.getWorlds()) {
				this.doChunks(world);
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server world list");
		}
	}
	public final void doChunks(World world) {
		this.doChunks(((WorldServer) world).chunkProviderServer);
	}
	public final void doChunks(ChunkProviderServer chunkProvider) {
		try {
			for (Chunk chunk : (List<Chunk>) chunkProvider.chunkList) {
				this.handle(chunk);
				if (chunk.bukkitChunk == null) continue;
				this.handle((CraftChunk) chunk.bukkitChunk);
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("world chunk list of world '" + chunkProvider.world.getWorld().getName() + "'");
		}
	}
	public Task createTask(JavaPlugin plugin) {
		final Operation op = this;
		return new Task(plugin) {
			public void run() {
				op.run();
			}
		};
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
