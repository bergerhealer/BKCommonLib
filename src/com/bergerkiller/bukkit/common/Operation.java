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
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class Operation implements Runnable {

	/**
	 * Initializes a new Operation, it is run during initialization
	 */
	public Operation() {
		this(true);
	}

	/**
	 * Initializes a new Operation
	 * 
	 * @param run setting, True to run during initialization
	 */
	public Operation(boolean run) {
		if (run) {
			this.run();
		}
	}

	private void multiAccess(final String location) {
		Bukkit.getLogger().severe("The " + location + " got accessed by more than one thread or got modified while operating on it!");
	}

	/**
	 * Handles all the worlds on the server<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftWorld)<br>
	 * - handle(WorldServer)
	 */
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

	/**
	 * Handles all the players on the server<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftPlayer)<br>
	 * - handle(EntityPlayer)
	 */
	public final void doPlayers() {
		try {
			for (EntityPlayer player : CommonUtil.getOnlinePlayers()) {
				this.handle(player);
				if (player.netServerHandler == null)
					continue;
				if (player.netServerHandler.player != player)
					continue;
				this.handle(player.netServerHandler.getPlayer());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server player list");
		}
	}

	/**
	 * Handles all the players on a world<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftPlayer)<br>
	 * - handle(EntityPlayer)
	 * 
	 * @param world to handle the players of
	 */
	public final void doPlayers(World world) {
		try {
			for (EntityPlayer player : (List<EntityPlayer>) world.players) {
				this.handle(player);
				if (player.netServerHandler == null)
					continue;
				if (player.netServerHandler.player != player)
					continue;
				this.handle(player.netServerHandler.getPlayer());
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("world player list of world '" + world.getWorld().getName() + "'");
		}
	}

	/**
	 * Handles all the entities on the server<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftEntity)<br>
	 * - handle(Entity)
	 */
	public final void doEntities() {
		try {
			for (WorldServer world : WorldUtil.getWorlds()) {
				this.doEntities(world);
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server world list");
		}
	}

	/**
	 * Handles all the entities on a world<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftEntity)<br>
	 * - handle(Entity)
	 * 
	 * @param world to handle the entities of
	 */
	public final void doEntities(org.bukkit.World world) {
		doEntities(WorldUtil.getNative(world));
	}

	/**
	 * Handles all the entities on a world<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftEntity)<br>
	 * - handle(Entity)
	 * 
	 * @param world to handle the entities of
	 */
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

	/**
	 * Handles all the entities in a chunk<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftEntity)<br>
	 * - handle(Entity)
	 * 
	 * @param chunk to handle the entities of
	 */
	public final void doEntities(org.bukkit.Chunk chunk) {
		doEntities(WorldUtil.getNative(chunk));
	}

	/**
	 * Handles all the entities in a chunk<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftEntity)<br>
	 * - handle(Entity)
	 * 
	 * @param chunk to handle the entities of
	 */
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

	/**
	 * Handles all the chunks on the server<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftChunk)<br>
	 * - handle(Chunk)
	 */
	public final void doChunks() {
		try {
			for (WorldServer world : WorldUtil.getWorlds()) {
				this.doChunks(world);
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("server world list");
		}
	}

	/**
	 * Handles all the chunks on a world<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftChunk)<br>
	 * - handle(Chunk)
	 * 
	 * @param world to handle the chunks of
	 */
	public final void doChunks(World world) {
		this.doChunks(((WorldServer) world).chunkProviderServer);
	}

	/**
	 * Handles all the chunks on a world<br><br>
	 * 
	 * Calls:<br>
	 * - handle(CraftChunk)<br>
	 * - handle(Chunk)
	 * 
	 * @param chunkProvider to handle the chunks of
	 */
	public final void doChunks(ChunkProviderServer chunkProvider) {
		try {
			for (Chunk chunk : WorldUtil.getChunks(chunkProvider)) {
				this.handle(chunk);
				if (chunk.bukkitChunk == null)
					continue;
				this.handle((CraftChunk) chunk.bukkitChunk);
			}
		} catch (ConcurrentModificationException ex) {
			multiAccess("world chunk list of world '" + chunkProvider.world.getWorld().getName() + "'");
		}
	}

	/**
	 * Creates a new task that runs this Operation<br>
	 * <b>The task still has to be started</b>
	 * 
	 * @param plugin to set as owner
	 * @return a new Task
	 */
	public Task createTask(JavaPlugin plugin) {
		final Operation op = this;
		return new Task(plugin) {
			public void run() {
				op.run();
			}
		};
	}

	public void handle(WorldServer world) {
	};

	public void handle(CraftWorld world) {
	};

	public void handle(EntityPlayer player) {
	};

	public void handle(CraftPlayer player) {
	};

	public void handle(Entity entity) {
	};

	public void handle(CraftEntity entity) {
	};

	public void handle(Chunk chunk) {
	};

	public void handle(CraftChunk chunk) {
	};
}
