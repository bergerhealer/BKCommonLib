package com.bergerkiller.bukkit.common.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;

import net.minecraft.server.v1_5_R1.AxisAlignedBB;
import net.minecraft.server.v1_5_R1.Block;
import net.minecraft.server.v1_5_R1.DamageSource;
import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityHuman;
import net.minecraft.server.v1_5_R1.EntityLiving;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;

public class EntityController<T extends CommonEntity<?>> extends CommonEntityController<T> {
	private static final List<AxisAlignedBB> collisionBuffer = new ArrayList<AxisAlignedBB>();

	/**
	 * Called when this Entity dies (could be called more than one time)
	 */
	public void onDie() {
		entity.getHandle(NMSEntityHook.class).super_die();
	}

	/**
	 * Called every tick to update the entity
	 */
	public void onTick() {
		entity.getHandle(NMSEntityHook.class).super_onTick(); 
	}

	/**
	 * Called when the entity is interacted by something
	 * 
	 * @param interacter that interacted
	 * @return True if interaction occurred, False if not
	 */
	public boolean onInteractBy(HumanEntity interacter) {
		return entity.getHandle(NMSEntityHook.class).super_onInteract(CommonNMS.getNative(interacter)); 
	}

	/**
	 * Called when the entity is damaged by something
	 * 
	 * @param damager that dealt the damage
	 * @param damage amount
	 * @return True if damage was dealt, False if not
	 */
	public boolean onEntityDamage(org.bukkit.entity.Entity damager, int damage) {
		DamageSource source;
		if (damager instanceof Player) {
			source = DamageSource.playerAttack(CommonNMS.getNative(damager, EntityHuman.class));
		} else if (damager instanceof LivingEntity) {
			source = DamageSource.mobAttack(CommonNMS.getNative(damager, EntityLiving.class));
		} else {
			source = DamageSource.GENERIC;
		}
		return entity.getHandle(NMSEntityHook.class).super_damageEntity(source, damage);
	}

	/**
	 * Handles the collision of this minecart with another Entity
	 * 
	 * @param e entity with which is collided
	 * @return True if collision is allowed, False if it is ignored
	 */
	public boolean onEntityCollision(org.bukkit.entity.Entity e) {
		return true;
	}

	/**
	 * Handles the collision of this minecart with a Block
	 * 
	 * @param block with which this minecart collided
	 * @param hitFace of the block that the minecart hit
	 * @return True if collision is allowed, False if it is ignored
	 */
	public boolean onBlockCollision(org.bukkit.block.Block block, BlockFace hitFace) {
		return true;
	}

	/**
	 * Fired when the entity is getting burned by something
	 * 
	 * @param damage dealt
	 */
	public void onBurnDamage(int damage) {
		entity.getHandle(NMSEntityHook.class).super_onBurn(damage); 
	}

	/**
	 * Gets the localized name of this Entity. Override this method to change the name.
	 * 
	 * @return Localized name
	 */
	public String getLocalizedName() {
		return entity.getHandle(NMSEntityHook.class).super_getLocalizedName();
	}

	/**
	 * Obtains all entities/blocks that can be collided with, checking collisions along the way.
	 * This is similar to NMS.World.getCubes, but with inserted events.
	 * 
	 * @param bounds
	 * @return referenced list of collision cubes
	 */
	private List<AxisAlignedBB> getCollisions(AxisAlignedBB bounds) {
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
			dx = entity.getLocX() - block.getX() - 0.5;
			dy = entity.getLocY() - block.getY() - 0.5;
			dz = entity.getLocZ() - block.getZ() - 0.5;

			// Find out what direction the block is hit
			if (Math.abs(dx) < 0.1 && Math.abs(dz) < 0.1) {
				hitFace = dy >= 0.0 ? BlockFace.UP : BlockFace.DOWN;
			} else {
				hitFace = FaceUtil.getDirection(dx, dz, false);
			}
			// Block collision event
			if (!this.onBlockCollision(block, hitFace)) {
				iter.remove();
			}
		}

		// Handle and add entities
		AxisAlignedBB entityBounds;
		for (Entity entity : CommonNMS.getEntitiesIn(handle.world, handle, bounds.grow(0.25, 0.25, 0.25))) {
			/*
			 * This part is completely pointless as E() always returns null May
			 * this ever change, make sure E() is handled correctly.
			 * 
			 * entityBounds = entity.E(); if (entityBounds != null &&
			 * entityBounds.a(bounds)) { collisionBuffer.add(entityBounds); }
			 */

			entityBounds = entity.boundingBox;
			// Entity collision event after the null/inBounds check
			if (entityBounds != null && entityBounds.a(bounds) && onEntityCollision(Conversion.toEntity.convert(entity))) {
				collisionBuffer.add(entityBounds);
			}
		}

		// Done
		return collisionBuffer;
	}
	
	public void onMove(double dx, double dy, double dz) {
		final Entity handle = entity.getHandle(Entity.class);
		if (handle.Z) {
			handle.boundingBox.d(dx, dy, dz);
			handle.locX = (handle.boundingBox.a + handle.boundingBox.d) / 2.0D;
			handle.locY = (handle.boundingBox.b + (double) handle.height) - (double) handle.Y;
			handle.locZ = (handle.boundingBox.c + handle.boundingBox.f) / 2.0D;
		} else {
			handle.Y *= 0.4f;
			final double oldLocX = handle.locX;
			final double oldLocY = handle.locY;
			final double oldLocZ = handle.locZ;
			if (EntityRef.justLanded.get(handle)) {
				EntityRef.justLanded.set(handle, false);
				dx *= 0.25;
				dy *= 0.05;
				dz *= 0.25;
				handle.motX = 0.0;
				handle.motY = 0.0;
				handle.motZ = 0.0;
			}
			final double oldDx = dx;
			final double oldDy = dy;
			final double oldDz = dz;
			AxisAlignedBB axisalignedbb = handle.boundingBox.clone();
			List<AxisAlignedBB> list = getCollisions(handle.boundingBox.a(dx, dy, dz));

			// Collision testing using Y
			for (AxisAlignedBB aabb : list) {
				dy = aabb.b(handle.boundingBox, dy);
			}
			handle.boundingBox.d(0.0, dy, 0.0);
			if (!handle.L && oldDy != dy) {
				dz = 0.0D;
				dy = 0.0D;
				dx = 0.0D;
			}
			boolean isOnGround = handle.onGround || oldDy != dy && oldDy < 0.0;

			// Collision testing using X
			for (AxisAlignedBB aabb : list) {
				dx = aabb.a(handle.boundingBox, dx);
			}
			handle.boundingBox.d(dx, 0.0, 0.0);
			if (!handle.L && oldDx != dx) {
				dz = 0.0;
				dy = 0.0;
				dx = 0.0;
			}

			// Collision testing using Z
			for (AxisAlignedBB aabb : list) {
				dz = aabb.c(handle.boundingBox, dz);
			}
			handle.boundingBox.d(0.0, 0.0, dz);
			if (!handle.L && oldDz != dz) {
				dz = 0.0;
				dy = 0.0;
				dx = 0.0;
			}

			double moveDx;
			double moveDy;
			double moveDz;

			if (handle.Y > 0.0f && handle.Y < 0.05f && isOnGround && (oldDx != dx || oldDz != dz)) {
				moveDx = dx;
				moveDy = dy;
				moveDz = dz;
				dx = oldDx;
				dy = (double) handle.Y;
				dz = oldDz;

				AxisAlignedBB axisalignedbb1 = handle.boundingBox.clone();
				handle.boundingBox.c(axisalignedbb);

				list = getCollisions(handle.boundingBox.a(oldDx, dy, oldDz));

				// Collision testing using Y
				for (AxisAlignedBB aabb : list) {
					dy = aabb.b(handle.boundingBox, dy);
				}
				handle.boundingBox.d(0.0, dy, 0.0);
				if (!handle.L && oldDy != dy) {
					dz = 0.0;
					dy = 0.0;
					dx = 0.0;
				}

				// Collision testing using X
				for (AxisAlignedBB aabb : list) {
					dx = aabb.a(handle.boundingBox, dx);
				}
				handle.boundingBox.d(dx, 0.0, 0.0D);
				if (!handle.L && oldDx != dx) {
					dz = 0.0;
					dy = 0.0;
					dx = 0.0;
				}

				// Collision testing using Z
				for (AxisAlignedBB aabb : list) {
					dz = aabb.c(handle.boundingBox, dz);
				}
				handle.boundingBox.d(0.0, 0.0, dz);
				if (!handle.L && oldDz != dz) {
					dz = 0.0;
					dy = 0.0;
					dx = 0.0;
				}

				if (!handle.L && oldDy != dy) {
					dz = 0.0;
					dy = 0.0;
					dx = 0.0;
				} else {
					dy = (double) -handle.Y;
					for (int k = 0; k < list.size(); k++) {
						dy = list.get(k).b(handle.boundingBox, dy);
					}
					handle.boundingBox.d(0.0, dy, 0.0);
				}
				if (MathUtil.lengthSquared(moveDx, moveDz) >= MathUtil.lengthSquared(dx, dz)) {
					dx = moveDx;
					dy = moveDy;
					dz = moveDz;
					handle.boundingBox.c(axisalignedbb1);
				} else {
					double subY = handle.boundingBox.b - (int) handle.boundingBox.b;
					if (subY > 0.0) {
						handle.Y += subY + 0.01;
					}
				}
			}

			handle.locX = (handle.boundingBox.a + handle.boundingBox.d) / 2D;
			handle.locY = (handle.boundingBox.b + (double) handle.height) - (double) handle.Y;
			handle.locZ = (handle.boundingBox.c + handle.boundingBox.f) / 2D;
			entity.setMovementImpaired(oldDx != dx || oldDz != dz);
			handle.H = oldDy != dy;
			handle.onGround = oldDy != dy && oldDy < 0.0D;
			handle.I = entity.isMovementImpaired() || handle.H;
			EntityRef.updateFalling(handle, dy, handle.onGround);

			if (oldDy != dy) {
				handle.motY = 0.0;
			}
			// ========Math.abs check to prevent collision slowdown=====
			if (oldDx != dx && Math.abs(handle.motX) > Math.abs(handle.motZ)) {
				handle.motX = 0.0;
			}
			if (oldDz != dz && Math.abs(handle.motZ) > Math.abs(handle.motX)) {
				handle.motZ = 0.0;
			}
			// ===========================================================================

			moveDx = handle.locX - oldLocX;
			moveDy = handle.locY - oldLocY;
			moveDz = handle.locZ - oldLocZ;
			if (entity.isMovementImpaired()) {
				Vehicle vehicle = (Vehicle) entity.getEntity();
				org.bukkit.block.Block block = entity.getWorld().getBlockAt(MathUtil.floor(handle.locX), MathUtil.floor(handle.locY - (double) handle.height), MathUtil.floor(handle.locZ));
				if (oldDx > dx) {
					block = block.getRelative(BlockFace.EAST);
				} else if (oldDx < dx) {
					block = block.getRelative(BlockFace.WEST);
				} else if (oldDz > dz) {
					block = block.getRelative(BlockFace.SOUTH);
				} else if (oldDz < dz) {
					block = block.getRelative(BlockFace.NORTH);
				}
				VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, block);
				entity.getServer().getPluginManager().callEvent(event);
			}

			// Update entity movement sounds
			if (EntityRef.hasMovementSound(handle) && handle.vehicle == null) {
				int bX = MathUtil.floor(handle.locX);
				int bY = MathUtil.floor(handle.locY - 0.2D - (double) handle.height);
				int bZ = MathUtil.floor(handle.locZ);
				int typeId = handle.world.getTypeId(bX, bY, bZ);

				// Some special type cases (this is sooooooo hacked in...)
				if (typeId == 0 && handle.world.getTypeId(bX, bY - 1, bZ) == Material.FENCE.getId()) {
					typeId = Material.FENCE.getId();
				}
				if (typeId != Material.LADDER.getId()) {
					moveDy = 0.0;
				}

				handle.R += Math.sqrt(moveDx * moveDx + moveDz * moveDz) * 0.6;
				handle.S += Math.sqrt(moveDx * moveDx + moveDy * moveDy + moveDz * moveDz) * 0.6;
				if (handle.S > EntityRef.stepCounter.get(entity.getHandle()) && typeId > 0) {
					EntityRef.stepCounter.set(entity.getHandle(), (int) handle.S + 1);
					if (handle.H()) {
						float f = (float) Math.sqrt(handle.motX * handle.motX * 0.2 + handle.motY * handle.motY + handle.motZ * handle.motZ * 0.2) * 0.35F;
						if (f > 1.0F) {
							f = 1.0F;
						}
						entity.makeRandomSound(Sound.SWIM, f, 1.0f);
					}

					entity.makeStepSound(bX, bY, bZ, typeId);
					Block.byId[bZ].b(handle.world, bX, bY, bZ, handle);
				}
			}

			EntityRef.updateBlockCollision(handle);

			// Fire tick calculation (check using block collision)
			boolean flag2 = handle.G();
			if (handle.world.e(handle.boundingBox.shrink(0.001D, 0.001D, 0.001D))) {
				onBurnDamage(1);
				if (!flag2) {
					handle.fireTicks++;
					if (handle.fireTicks <= 0) {
						EntityCombustEvent event = new EntityCombustEvent(entity.getEntity(), 8);
						entity.getServer().getPluginManager().callEvent(event);
						if (!event.isCancelled()) {
							handle.setOnFire(event.getDuration());
						}
					} else {
						handle.setOnFire(8);
					}
				}
			} else if (handle.fireTicks <= 0) {
				handle.fireTicks = -handle.maxFireTicks;
			}
			if (flag2 && handle.fireTicks > 0) {
				entity.makeRandomSound(Sound.FIZZ, 0.7f, 1.6f);
				handle.fireTicks = -handle.maxFireTicks;
			}
		}
	}
}
