package com.bergerkiller.bukkit.common.utils;

import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_4_R1.Chunk;
import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.EntityTrackerEntry;
import net.minecraft.server.v1_4_R1.IntHashMap;
import net.minecraft.server.v1_4_R1.MathHelper;
import net.minecraft.server.v1_4_R1.World;
import net.minecraft.server.v1_4_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;

public class EntityUtil extends EntityPropertyUtil {

	public static <T extends org.bukkit.entity.Entity> T getEntity(org.bukkit.World world, UUID uid, Class<T> type) {
		return CommonUtil.tryCast(getEntity(world, uid), type);
	}

	public static org.bukkit.entity.Entity getEntity(org.bukkit.World world, UUID uid) {
		Entity e = getEntity(NativeUtil.getNative(world), uid);
		return e == null ? null : NativeUtil.getEntity(e);
	}

	@SuppressWarnings("unchecked")
	public static Entity getEntity(World world, UUID uid) {
		for (Entity e : (List<Entity>) world.entityList) {
			if (e.uniqueId.equals(uid))
				return e;
		}
		return null;
	}

	/**
	 * Adds a single entity to the server
	 * 
	 * @param entity to add
	 */
	public static void addEntity(org.bukkit.entity.Entity entity) {
		Entity nmsentity = NativeUtil.getNative(entity);
		nmsentity.world.getChunkAt(MathUtil.toChunk(nmsentity.locX), MathUtil.toChunk(nmsentity.locZ));
		nmsentity.dead = false;
		nmsentity.world.addEntity(nmsentity);
	}

	/**
	 * Replaces a given Entity with another Entity<br>
	 * The entity is not respawned to the clients!
	 * 
	 * @param toReplace Entity, which will be removed
	 * @param with Entity, which will be added in its place
	 */
	public static void setEntity(Entity toreplace, Entity with) {
		setEntity(toreplace, with, WorldUtil.getTrackerEntry(toreplace));
	}

	/**
	 * Replaces a given Entity with another Entity<br>
	 * The entity is not respawned to the clients!
	 * 
	 * @param toreplace Entity, which will be removed
	 * @param with Entity, which will be added in its place
	 * @param tracker to use for the new entity
	 */
	@SuppressWarnings("unchecked")
	public static void setEntity(final Entity toreplace, final Entity with, EntityTrackerEntry tracker) {
		// transfer important information
		with.locX = toreplace.locX;
		with.locY = toreplace.locY;
		with.locZ = toreplace.locZ;
		EntityRef.chunkX.transfer(toreplace, with);
		EntityRef.chunkY.transfer(toreplace, with);
		EntityRef.chunkZ.transfer(toreplace, with);
		with.world = toreplace.world;
		with.id = toreplace.id;
		toreplace.dead = true;
		with.dead = false;
		// Bukkit entity
		EntityRef.bukkitEntity.transfer(toreplace, with);
		((CraftEntity) NativeUtil.getEntity(with)).setHandle(with);
		// Passenger
		if (toreplace.passenger != null) {
			toreplace.passenger.setPassengerOf(with);
		}

		// make sure the chunk is loaded prior to swapping
		// this may cause the chunk unload to be delayed one tick
		Chunk chunk = toreplace.world.chunkProvider.getChunkAt(EntityRef.chunkX.get(with), EntityRef.chunkZ.get(with));

		// replace the entity in the world
		List<Entity> worldEntities = toreplace.world.entityList;
		for (int i = 0; i < worldEntities.size(); i++) {
			if (worldEntities.get(i).id == toreplace.id) {
				toreplace.world.entityList.set(i, with);
				break;
			}
		}

		// replace the entity in the 'entities by id' map
		final IntHashMap entitiesById = WorldServerRef.entitiesById.get(toreplace.world);
		if (entitiesById.d(toreplace.id) == null) {
			CommonUtil.nextTick(new Runnable() {
				public void run() {
					entitiesById.a(toreplace.id, with);
				}
			});
		} else {
			entitiesById.a(toreplace.id, with);
		}

		// replace the entity in the chunk
		int chunkY = EntityRef.chunkY.get(with);
		if (!replaceInChunk(chunk, chunkY, toreplace, with)) {
			for (int y = 0; y < chunk.entitySlices.length; y++) {
				if (y != chunkY && replaceInChunk(chunk, y, toreplace, with)) {
					break;
				}
			}
		}

		// put the new entity tracker
		tracker.tracker = with;
		WorldUtil.setTrackerEntry(toreplace, tracker);
	}

	@SuppressWarnings({"unchecked"})
	private static boolean replaceInChunk(Chunk chunk, int chunkY, Entity toreplace, Entity with) {
		List<Entity> list = chunk.entitySlices[chunkY];
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).id == toreplace.id) {
				list.set(i, with);
				//set invalid
				chunk.m = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a given Entity should be ignored when working with it<br>
	 * This could be because another plugin is operating on it, or for Virtual items
	 * 
	 * @param entity to check
	 * @return True if the entity should be ignored, False if not
	 */
	public static boolean isIgnored(org.bukkit.entity.Entity entity) {
		return CommonPlugin.getInstance().isEntityIgnored(entity);
	}

	/*
	 * Is near something?
	 */
	public static boolean isNearChunk(org.bukkit.entity.Entity entity, final int cx, final int cz, final int chunkview) {
		return isNearChunk(NativeUtil.getNative(entity), cx, cz, chunkview);
	}

	public static boolean isNearChunk(Entity entity, final int cx, final int cz, final int chunkview) {
		if (Math.abs(MathUtil.toChunk(entity.locX) - cx) > chunkview)
			return false;
		if (Math.abs(MathUtil.toChunk(entity.locZ) - cz) > chunkview)
			return false;
		return true;
	}

	public static boolean isNearBlock(org.bukkit.entity.Entity entity, final int bx, final int bz, final int blockview) {
		return isNearBlock(NativeUtil.getNative(entity), bx, bz, blockview);
	}

	public static boolean isNearBlock(Entity entity, final int bx, final int bz, final int blockview) {
		if (Math.abs(MathHelper.floor(entity.locX) - bx) > blockview)
			return false;
		if (Math.abs(MathHelper.floor(entity.locZ) - bz) > blockview)
			return false;
		return true;
	}

	/**
	 * Teleports an entity in the next tick
	 * 
	 * @param entity to teleport
	 * @param to location to teleport to
	 */
	public static void teleportNextTick(final org.bukkit.entity.Entity entity, final Location to) {
		CommonUtil.nextTick(new Runnable() {
			public void run() {
				teleport(entity, to);
			}
		});
	}

	/**
	 * Teleports an entity
	 * 
	 * @param entity to teleport
	 * @param to location to teleport to
	 */
	public static boolean teleport(org.bukkit.entity.Entity entity, final Location to) {
		return teleport(NativeUtil.getNative(entity), to);
	}

	/**
	 * Teleports an entity
	 * 
	 * @param entity to teleport
	 * @param to location to teleport to
	 */
	public static boolean teleport(final Entity entity, final Location to) {
		WorldServer newworld = NativeUtil.getNative(to.getWorld());
		WorldUtil.loadChunks(to, 3);
		if (entity.world != newworld && !(entity instanceof EntityPlayer)) {
			final Entity passenger = entity.passenger;
			if (passenger != null) {
				entity.passenger = null;
				passenger.vehicle = null;
				if (teleport(passenger, to)) {
					CommonUtil.nextTick(new Runnable() {
						public void run() {
							passenger.setPassengerOf(entity);
						}
					});
				}
			}

			// teleport this entity
			entity.world.removeEntity(entity);
			entity.dead = false;
			entity.world = newworld;
			entity.setLocation(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
			entity.world.addEntity(entity);
			return true;
		} else {
			return NativeUtil.getEntity(entity).teleport(to);
		}
	}
}
