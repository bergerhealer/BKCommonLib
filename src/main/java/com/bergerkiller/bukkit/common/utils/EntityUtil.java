package com.bergerkiller.bukkit.common.utils;

import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityPlayer;
import net.minecraft.server.v1_5_R1.World;
import org.bukkit.Location;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;

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
		// Remove an entity tracker for this entity if it was present
		WorldUtil.getTracker(entity.getWorld()).stopTracking(entity);
		// Add the entity to the world
		nmsentity.world.addEntity(nmsentity);
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
