package com.bergerkiller.bukkit.common.utils;

import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_5_R1.Chunk;
import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityPlayer;
import net.minecraft.server.v1_5_R1.IntHashMap;
import net.minecraft.server.v1_5_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftEntity;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;

public class EntityUtil extends EntityPropertyUtil {

	public static <T extends org.bukkit.entity.Entity> T getEntity(org.bukkit.World world, UUID uid, Class<T> type) {
		return CommonUtil.tryCast(getEntity(world, uid), type);
	}

	public static org.bukkit.entity.Entity getEntity(org.bukkit.World world, UUID uid) {
		Entity e = getEntity(CommonNMS.getNative(world), uid);
		return Conversion.toEntity.convert(e);
	}

	@SuppressWarnings("unchecked")
	public static Entity getEntity(World world, UUID uid) {
		for (Entity e : (List<Entity>) world.entityList) {
			if (e.uniqueID.equals(uid)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Adds a single entity to the server
	 * 
	 * @param entity to add
	 */
	public static void addEntity(org.bukkit.entity.Entity entity) {
		Entity nmsentity = CommonNMS.getNative(entity);
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
	public static void setEntity(org.bukkit.entity.Entity toreplace, org.bukkit.entity.Entity with) {
		setEntity(toreplace, with, WorldUtil.getTrackerEntry(toreplace));
	}

	/**
	 * Replaces a given Entity with another Entity<br>
	 * The entity is not respawned to the clients!
	 * 
	 * @param toreplace Entity, which will be removed
	 * @param with Entity, which will be added in its place
	 * @param entityTrackerEntry to use for the new entity
	 */
	@SuppressWarnings("unchecked")
	public static void setEntity(org.bukkit.entity.Entity toreplace, org.bukkit.entity.Entity with, Object entityTrackerEntry) {
		final Entity replacedHndl = CommonNMS.getNative(toreplace);
		final Entity withHndl = CommonNMS.getNative(with);
		// transfer important information
		withHndl.locX = replacedHndl.locX;
		withHndl.locY = replacedHndl.locY;
		withHndl.locZ = replacedHndl.locZ;
		EntityRef.chunkX.transfer(replacedHndl, withHndl);
		EntityRef.chunkY.transfer(replacedHndl, withHndl);
		EntityRef.chunkZ.transfer(replacedHndl, withHndl);
		withHndl.world = replacedHndl.world;
		withHndl.id = replacedHndl.id;
		replacedHndl.dead = true;
		withHndl.dead = false;
		// Bukkit entity
		EntityRef.bukkitEntity.transfer(replacedHndl, withHndl);
		((CraftEntity) Conversion.toEntity.convert(withHndl)).setHandle(withHndl);
		// Passenger
		if (replacedHndl.passenger != null) {
			replacedHndl.passenger.setPassengerOf(withHndl);
		}

		// make sure the chunk is loaded prior to swapping
		// this may cause the chunk unload to be delayed one tick
		Chunk chunk = replacedHndl.world.chunkProvider.getChunkAt(EntityRef.chunkX.get(withHndl), EntityRef.chunkZ.get(withHndl));

		// replace the entity in the world
		List<Entity> worldEntities = replacedHndl.world.entityList;
		for (int i = 0; i < worldEntities.size(); i++) {
			if (worldEntities.get(i).id == replacedHndl.id) {
				replacedHndl.world.entityList.set(i, withHndl);
				break;
			}
		}

		// replace the entity in the 'entities by id' map
		final IntHashMap entitiesById = (IntHashMap) WorldServerRef.entitiesById.get(replacedHndl.world);
		if (entitiesById.d(replacedHndl.id) == null) {
			CommonUtil.nextTick(new Runnable() {
				public void run() {
					entitiesById.a(replacedHndl.id, withHndl);
				}
			});
		} else {
			entitiesById.a(replacedHndl.id, withHndl);
		}

		// replace the entity in the chunk
		int chunkY = EntityRef.chunkY.get(withHndl);
		if (!replaceInChunk(chunk, chunkY, replacedHndl, withHndl)) {
			for (int y = 0; y < chunk.entitySlices.length; y++) {
				if (y != chunkY && replaceInChunk(chunk, y, replacedHndl, withHndl)) {
					break;
				}
			}
		}

		// put the new entity tracker
		EntityTrackerEntryRef.tracker.setInternal(entityTrackerEntry, withHndl);
		WorldUtil.setTrackerEntry(withHndl.getBukkitEntity(), entityTrackerEntry);
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
		final int x = MathUtil.toChunk(getLocX(entity)) - cx;
		final int z = MathUtil.toChunk(getLocZ(entity)) - cz;
		return Math.abs(x) <= chunkview && Math.abs(z) <= chunkview;
	}

	public static boolean isNearBlock(org.bukkit.entity.Entity entity, final int bx, final int bz, final int blockview) {
		final int x = MathUtil.floor(getLocX(entity) - bx);
		final int z = MathUtil.floor(getLocZ(entity) - bz);
		return Math.abs(x) <= blockview && Math.abs(z) <= blockview;
	}

	/**
	 * Performs entity on entity collision logic for an entity.
	 * This will perform the push logic caused by collision.
	 * 
	 * @param entity to work on
	 * @param with the entity to collide
	 */
	public static void doCollision(org.bukkit.entity.Entity entity, org.bukkit.entity.Entity with) {
		CommonNMS.getNative(entity).collide(CommonNMS.getNative(with));
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
	public static boolean teleport(final org.bukkit.entity.Entity entity, final Location to) {
		final Entity entityHandle = CommonNMS.getNative(entity);
		final Entity passenger = entityHandle.passenger;
		World newworld = CommonNMS.getNative(to.getWorld());
		WorldUtil.loadChunks(to, 3);
		if (entityHandle.world != newworld && !(entityHandle instanceof EntityPlayer)) {
			if (passenger != null) {
				entityHandle.passenger = null;
				passenger.vehicle = null;
				if (teleport(passenger.getBukkitEntity(), to)) {
					CommonUtil.nextTick(new Runnable() {
						public void run() {
							passenger.setPassengerOf(entityHandle);
						}
					});
				}
			}

			// teleport this entity
			entityHandle.world.removeEntity(entityHandle);
			entityHandle.dead = false;
			entityHandle.world = newworld;
			entityHandle.setLocation(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
			entityHandle.world.addEntity(entityHandle);
			return true;
		} else {
			// If in a vehicle, make sure we eject first
			if (entityHandle.vehicle != null) {
				entityHandle.setPassengerOf(null);
			}
			// If vehicle, eject the passenger first
			if (passenger != null) {
				passenger.vehicle = null;
				entityHandle.passenger = null;
			}
			final boolean succ = entity.teleport(to);
			// If there was a passenger, let passenger enter again
			if (passenger != null) {
				passenger.vehicle = entityHandle;
				entityHandle.passenger = passenger;
			}
			return succ;
		}
	}
}
