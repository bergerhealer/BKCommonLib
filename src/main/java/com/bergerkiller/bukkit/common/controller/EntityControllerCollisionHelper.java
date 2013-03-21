package com.bergerkiller.bukkit.common.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;

import net.minecraft.server.v1_5_R2.AxisAlignedBB;
import net.minecraft.server.v1_5_R2.Block;
import net.minecraft.server.v1_5_R2.Entity;

/**
 * Class that deals with AABB-collision resolving for Entity Controllers.
 * This method is moved to hide it from the API - results in Class Hierarchy errors otherwise.
 */
class EntityControllerCollisionHelper {
	private static final List<AxisAlignedBB> collisionBuffer = new ArrayList<AxisAlignedBB>();

	/**
	 * Obtains all entities/blocks that can be collided with, checking collisions along the way.
	 * This is similar to NMS.World.getCubes, but with inserted events.
	 * 
	 * @param bounds
	 * @return referenced list of collision cubes
	 */
	public static List<AxisAlignedBB> getCollisions(EntityController<?> controller, AxisAlignedBB bounds) {
		final CommonEntity<?> entity = controller.getEntity();
		final Entity handle = entity.getHandle(Entity.class);
		collisionBuffer.clear();
		final int xmin = MathUtil.floor(bounds.a);
		final int ymin = MathUtil.floor(bounds.b);
		final int zmin = MathUtil.floor(bounds.c);
		final int xmax = MathUtil.floor(bounds.d + 1.0);
		final int ymax = MathUtil.floor(bounds.e + 1.0);
		final int zmax = MathUtil.floor(bounds.f + 1.0);

		// Add block collisions
		int x, y, z;
		for (x = xmin; x < xmax; ++x) {
			for (z = zmin; z < zmax; ++z) {
				if (handle.world.isLoaded(x, 64, z)) {
					for (y = ymin - 1; y < ymax; ++y) {
						Block block = Block.byId[handle.world.getTypeId(x, y, z)];
						if (block != null) {
							block.a(handle.world, x, y, z, bounds, collisionBuffer, handle);
						}
					}
				}
			}
		}

		// Handle block collisions
		double dx, dy, dz;
		BlockFace hitFace;
		Iterator<AxisAlignedBB> iter = collisionBuffer.iterator();
		AxisAlignedBB blockBounds;
		while (iter.hasNext()) {
			blockBounds = iter.next();
			// Convert to block and block coordinates
			org.bukkit.block.Block block = entity.getWorld().getBlockAt(MathUtil.floor(blockBounds.a), MathUtil.floor(blockBounds.b), MathUtil.floor(blockBounds.c));
			dx = entity.loc.getX() - block.getX() - 0.5;
			dy = entity.loc.getY() - block.getY() - 0.5;
			dz = entity.loc.getZ() - block.getZ() - 0.5;

			// Find out what direction the block is hit
			if (Math.abs(dx) < 0.1 && Math.abs(dz) < 0.1) {
				hitFace = dy >= 0.0 ? BlockFace.UP : BlockFace.DOWN;
			} else {
				hitFace = FaceUtil.getDirection(dx, dz, false);
			}
			// Block collision event
			if (!controller.onBlockCollision(block, hitFace)) {
				iter.remove();
			}
		}

		// Handle and add entities
		AxisAlignedBB entityBounds;
		for (Entity collider : CommonNMS.getEntitiesIn(handle.world, handle, bounds.grow(0.25, 0.25, 0.25))) {
			/*
			 * This part is completely pointless as E() always returns null May
			 * this ever change, make sure E() is handled correctly.
			 * 
			 * entityBounds = entity.E(); if (entityBounds != null &&
			 * entityBounds.a(bounds)) { collisionBuffer.add(entityBounds); }
			 */

			entityBounds = collider.boundingBox;
			// Entity collision event after the null/inBounds check
			if (entityBounds != null && entityBounds.a(bounds) && controller.onEntityCollision(Conversion.toEntity.convert(collider))) {
				collisionBuffer.add(entityBounds);
			}
		}

		// Done
		return collisionBuffer;
	}
}
