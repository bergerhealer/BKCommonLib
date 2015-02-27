package com.bergerkiller.bukkit.common.controller;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;

import net.minecraft.server.v1_8_R1.AxisAlignedBB;
import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.Blocks;
import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.Entity;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import net.minecraft.server.v1_8_R1.BlockPosition;

public class EntityController<T extends CommonEntity<?>> extends CommonEntityController<T> {

    /**
     * Binds this Entity Controller to an Entity. This is called from elsewhere,
     * and should be ignored entirely.
     *
     * @param entity to bind with
     */
    @SuppressWarnings("unchecked")
    public final void bind(CommonEntity<?> entity) {
        if (this.entity != null) {
            this.onDetached();
        }
        this.entity = (T) entity;
        if (this.entity != null) {
            final Object handle = this.entity.getHandle();
            if (handle instanceof NMSEntityHook) {
                ((NMSEntityHook) handle).setController(this);
            }
            if (entity.isSpawned()) {
                this.onAttached();
            }
        }
    }

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
        entity.getHandle(NMSEntityHook.class).super_h();
    }

    /**
     * Called when the entity is interacted by something
     *
     * @param interacter that interacted
     * @return True if interaction occurred, False if not
     */
    public boolean onInteractBy(HumanEntity interacter) {
        return entity.getHandle(NMSEntityHook.class).super_c(CommonNMS.getNative(interacter));
    }

    /**
     * Called when the entity is damaged by something
     *
     * @param damageSource of the damage
     * @param damage amount
     */
    public void onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource damageSource, double damage) {
        entity.getHandle(NMSEntityHook.class).super_damageEntity((DamageSource) damageSource.getHandle(), (float) damage);
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
    public void onBurnDamage(double damage) {
        entity.getHandle(NMSEntityHook.class).super_burn((float) damage);
    }

    /**
     * Gets whether this Entity Controller allows players to take this Entity
     * with them. With this enabled, players take the vehicle with them. To
     * disable this default behavior, override this method.
     *
     * @return True if players can take the entity with them, False if not
     */
    public boolean isPlayerTakable() {
        return true;
    }

    /**
     * Gets the localized name of this Entity. Override this method to change
     * the name.
     *
     * @return Localized name
     */
    public String getLocalizedName() {
        return entity.getHandle(NMSEntityHook.class).super_getName();
    }

    public void onPush(double dx, double dy, double dz) {
        entity.getHandle(NMSEntityHook.class).super_g(dx, dy, dz);
    }

    /**
     * Performs Entity movement logic, to move the Entity and handle collisions
     * TODO: implement NMS code of this
     *
     * @param dx offset to move
     * @param dy offset to move
     * @param dz offset to move
     */
    public void onMove(double dx, double dy, double dz) {
        final Entity handle = entity.getHandle(Entity.class);
        if (handle.T) {
            handle.getBoundingBox().c(dx, dy, dz);
            handle.locX = CommonNMS.getMiddleX(handle.getBoundingBox());
            handle.locY = handle.getBoundingBox().b;
            handle.locZ = CommonNMS.getMiddleZ(handle.getBoundingBox());
        } else {
            //handle.W *= 0.4f;
            final double oldLocX = handle.locX;
            final double oldLocY = handle.locY;
            final double oldLocZ = handle.locZ;
            if (EntityRef.justLanded.get(handle)) {
                EntityRef.justLanded.set(handle, false);
                dx *= 0.25;
                dy *= 0.05;
                dz *= 0.25;
                entity.vel.setZero();
            }

            // Don't perform any movement updates when not moving
            if (dx == 0 && dy == 0 && dz == 0 && !entity.hasPassenger() && !entity.isInsideVehicle()) {
                //onPostMove(0.0, 0.0, 0.0);
                return;
            }

            // Perform movement updates
            final double oldDx = dx;
            final double oldDy = dy;
            final double oldDz = dz;
            AxisAlignedBB axisalignedbb = handle.getBoundingBox();
            List<AxisAlignedBB> list = EntityControllerCollisionHelper.getCollisions(this, handle.getBoundingBox().a(dx, dy, dz));

            // Collision testing using Y
            for (AxisAlignedBB aabb : list) {
                dy = aabb.b(handle.getBoundingBox(), dy);
            }
            handle.getBoundingBox().c(0.0, dy, 0.0);
            if (!handle.aS() && oldDy != dy) {
                dx = dy = dz = 0.0;
            }
            boolean isOnGround = handle.onGround || oldDy != dy && oldDy < 0.0;

            // Collision testing using X
            for (AxisAlignedBB aabb : list) {
                dx = aabb.a(handle.getBoundingBox(), dx);
            }
            handle.getBoundingBox().c(dx, 0.0, 0.0);
            if (!handle.aS() && oldDx != dx) {
                dx = dy = dz = 0.0;
            }

            // Collision testing using Z
            for (AxisAlignedBB aabb : list) {
                dz = aabb.c(handle.getBoundingBox(), dz);
            }
            handle.getBoundingBox().c(0.0, 0.0, dz);
            if (!handle.aS() && oldDz != dz) {
                dx = dy = dz = 0.0;
            }

            double moveDx;
            double moveDy;
            double moveDz;
            if (handle.U > 0.0f && handle.U < 0.05f && isOnGround && (oldDx != dx || oldDz != dz)) {
                moveDx = dx;
                moveDy = dy;
                moveDz = dz;
                dx = oldDx;
                dy = (double) handle.U;
                dz = oldDz;

                AxisAlignedBB axisalignedbb1 = handle.getBoundingBox();
                handle.getBoundingBox().a(axisalignedbb);

                list = EntityControllerCollisionHelper.getCollisions(this, handle.getBoundingBox().a(oldDx, dy, oldDz));

                // Collision testing using Y
                for (AxisAlignedBB aabb : list) {
                    dy = aabb.b(handle.getBoundingBox(), dy);
                }
                handle.getBoundingBox().c(0.0, dy, 0.0);
                if (!handle.aS() && oldDy != dy) {
                    dx = dy = dz = 0.0;
                }

                // Collision testing using X
                for (AxisAlignedBB aabb : list) {
                    dx = aabb.a(handle.getBoundingBox(), dx);
                }
                handle.getBoundingBox().c(dx, 0.0, 0.0);
                if (!handle.aS() && oldDx != dx) {
                    dx = dy = dz = 0.0;
                }

                // Collision testing using Z
                for (AxisAlignedBB aabb : list) {
                    dz = aabb.c(handle.getBoundingBox(), dz);
                }
                handle.getBoundingBox().c(0.0, 0.0, dz);
                if (!handle.aS() && oldDz != dz) {
                    dx = dy = dz = 0.0;
                }

                if (!handle.aS() && oldDy != dy) {
                    dx = dy = dz = 0.0;
                } else {
                    dy = (double) -handle.U;
                    for (int k = 0; k < list.size(); k++) {
                        dy = list.get(k).b(handle.getBoundingBox(), dy);
                    }
                    handle.getBoundingBox().c(0.0, dy, 0.0);
                }
                if (MathUtil.lengthSquared(moveDx, moveDz) >= MathUtil.lengthSquared(dx, dz)) {
                    dx = moveDx;
                    dy = moveDy;
                    dz = moveDz;
                    handle.getBoundingBox().a(axisalignedbb1);
                } else {
                    double subY = handle.getBoundingBox().b - (int) handle.getBoundingBox().b;
                    if (subY > 0.0) {
                        handle.U += subY + 0.01;
                    }
                }
            }
            handle.locX = CommonNMS.getMiddleX(handle.getBoundingBox());
            handle.locY = handle.getBoundingBox().b;
            handle.locZ = CommonNMS.getMiddleZ(handle.getBoundingBox());
            entity.setMovementImpaired(oldDx != dx || oldDz != dz);
            handle.onGround = oldDy != dy && oldDy < 0.0;
            handle.F = oldDy != dy;
            handle.F = entity.isMovementImpaired() || handle.F;
            EntityRef.updateFalling(handle, dy, handle.onGround);

            // ================ Collision slowdown caused by ==============
            if (oldDy != dy) {
                handle.motY = 0.0;
            }
            if (oldDx != dx && entity.vel.x.abs() > entity.vel.z.abs()) {
                handle.motX = 0.0;
            }
            if (oldDz != dz && entity.vel.z.abs() > entity.vel.x.abs()) {
                handle.motZ = 0.0;
            }
            // =============================================================

            moveDx = handle.locX - oldLocX;
            moveDy = handle.locY - oldLocY;
            moveDz = handle.locZ - oldLocZ;
            if (entity.getEntity() instanceof Vehicle && entity.isMovementImpaired()) {
                Vehicle vehicle = (Vehicle) entity.getEntity();
                org.bukkit.block.Block block = entity.getWorld().getBlockAt(entity.loc.x.block(), MathUtil.floor(handle.locY - (double) handle.width/2), entity.loc.z.block());
                if (oldDx > dx) {
                    block = block.getRelative(BlockFace.EAST);
                } else if (oldDx < dx) {
                    block = block.getRelative(BlockFace.WEST);
                } else if (oldDz > dz) {
                    block = block.getRelative(BlockFace.SOUTH);
                } else if (oldDz < dz) {
                    block = block.getRelative(BlockFace.NORTH);
                }
                CommonUtil.callEvent(new VehicleBlockCollisionEvent(vehicle, block));
            }
            // Perform post movement logic
            onPostMove(moveDx, moveDy, moveDz);
        }
    }

    private void onPostMove(double moveDx, double moveDy, double moveDz) {
        Entity handle = this.entity.getHandle(Entity.class);
        // Update entity movement sounds
        if (EntityRef.hasMovementSound(handle) && handle.vehicle == null) {
            int bX = entity.loc.x.block();
            int bY = MathUtil.floor(handle.locY - 0.2);
            int bZ = entity.loc.z.block();
            Block block = handle.world.getType(new BlockPosition(bX, bY, bZ)).getBlock();
            int j1 = handle.world.getType(new BlockPosition(bX, bY - 1, bZ)).getBlock().b();

            // Magic values! *gasp* Bad, BAD Minecraft! Go sit in a corner!
            if (j1 == 11 || j1 == 32 || j1 == 21) {
                block = handle.world.getType(new BlockPosition(bX, bY - 1, bZ)).getBlock();
            }
            if (block != Blocks.LADDER) {
                moveDy = 0.0;
            }

            handle.N += MathUtil.length(moveDx, moveDz) * 0.6;
            handle.L += MathUtil.length(moveDx, moveDy, moveDz) * 0.6;
            if (handle.L > EntityRef.stepCounter.get(entity.getHandle()) && block != Blocks.AIR) {
                EntityRef.stepCounter.set(entity.getHandle(), (int) handle.L + 1);
                if (entity.isInWater(true)) {
                    float f = (float) Math.sqrt(entity.vel.y.squared() + 0.2 * entity.vel.xz.lengthSquared()) * 0.35f;
                    if (f > 1.0f) {
                        f = 1.0f;
                    }
                    entity.makeRandomSound(EntityRef.getSwimSound.invoke(handle), f, 1.0f);
                }
                entity.makeStepSound(bX, bY, bZ, CommonNMS.getMaterial(block));
                block.a(handle.world, new BlockPosition(bX, bY, bZ), handle);
            }
        }

        EntityRef.updateBlockCollision(handle);

        // Fire tick calculation (check using block collision)
        final boolean isInWater = handle.U(); // In water or raining
        if (handle.world.e(handle.getBoundingBox().shrink(0.001, 0.001, 0.001))) {
            onBurnDamage(1);
            if (!isInWater) {
                handle.fireTicks++;
                if (handle.fireTicks <= 0) {
                    EntityCombustEvent event = new EntityCombustEvent(entity.getEntity(), 8);
                    if (!CommonUtil.callEvent(event).isCancelled()) {
                        handle.setOnFire(event.getDuration());
                    }
                } else {
                    handle.setOnFire(8);
                }
            }
        } else if (handle.fireTicks <= 0) {
            handle.fireTicks = -handle.maxFireTicks;
        }
        if (isInWater && handle.fireTicks > 0) {
            entity.makeRandomSound(Sound.FIZZ, 0.7f, 1.6f);
            handle.fireTicks = -handle.maxFireTicks;
        }
    }
}
