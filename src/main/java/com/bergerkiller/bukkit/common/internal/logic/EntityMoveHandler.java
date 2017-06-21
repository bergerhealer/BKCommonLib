package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockCobbleWallHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockFenceGateHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockFenceHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockHandle;
import com.bergerkiller.generated.net.minecraft.server.BlocksHandle;
import com.bergerkiller.generated.net.minecraft.server.CrashReportHandle;
import com.bergerkiller.generated.net.minecraft.server.CrashReportSystemDetailsHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.server.ReportedExceptionHandle;
import com.bergerkiller.generated.net.minecraft.server.SoundEffectsHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle.EnumAxisHandle;

/**
 * Handles the full Entity move() physics function. It should be kept completely in sync with what is used on the server,
 * with the insertion of several important event handlers (block collision, entity collision)
 * 
 * These collisions are to be handled by the controller attached to the Entity
 */
public class EntityMoveHandler {
    private static final List<AxisAlignedBBHandle> collisions_buffer = new ArrayList<AxisAlignedBBHandle>();

    EntityController<?> controller;
    CommonEntity<?> entity;
    EntityHandle that;

    public EntityMoveHandler(EntityController<?> theController) {
        controller = theController;
    }

    private boolean world_getBlockCollisions(EntityHandle entity, AxisAlignedBBHandle bounds, boolean flag) {
        Object entityWorld_Raw = entity.getWorld().getRaw();
        if (WorldHandle.T.getBlockCollisions.isAvailable()) {
            if (!WorldHandle.T.getBlockCollisions.invoke(entityWorld_Raw, entity, bounds, flag, collisions_buffer)) {
                return false;
            }
        } else if (WorldHandle.T.getBlockCollisions_old.isAvailable()) {
            List<AxisAlignedBBHandle> foundBounds = WorldHandle.T.getBlockCollisions_old.invoke(entityWorld_Raw, entity, bounds);
            if (foundBounds.isEmpty()) {
                return false;
            }
            collisions_buffer.clear();
            collisions_buffer.addAll(foundBounds);
        }

        org.bukkit.World bWorld = entity.getWorld().getWorld();

        // Send all found bounds in the list through a filter calling onBlockCollision
        // Handle block collisions
        BlockFace hitFace;
        Iterator<AxisAlignedBBHandle> iter = collisions_buffer.iterator();
        AxisAlignedBBHandle blockBounds;
        double dx, dz;
        while (iter.hasNext()) {
            blockBounds = iter.next();
            // Convert to block and block coordinates
            org.bukkit.block.Block block = bWorld.getBlockAt(MathUtil.floor(blockBounds.getMinX()),
                    MathUtil.floor(blockBounds.getMinY()), MathUtil.floor(blockBounds.getMinZ()));

            // Find out what direction the block is hit
            if (bounds.getMaxY() > blockBounds.getMaxY()) {
                hitFace = BlockFace.UP;
            } else if (bounds.getMinY() < blockBounds.getMinY()) {
                hitFace = BlockFace.DOWN;
            } else {
                dx = entity.getLocX() - block.getX() - 0.5;
                dz = entity.getLocZ() - block.getZ() - 0.5;
                hitFace = FaceUtil.getDirection(dx, dz, false);
            }
            // Block collision event
            if (!controller.onBlockCollision(block, hitFace)) {
                iter.remove();
            }
        }

        return true;
    }

    private List<AxisAlignedBBHandle> world_getCubes(EntityHandle entity, AxisAlignedBBHandle axisalignedbb) {
        collisions_buffer.clear(); // BKCommonLib edit: use cached list

        // BKCommonLib start: replace world_getBlockCollisions call; use cached list instead
        //world.a(entity, axisalignedbb, false, arraylist);
        world_getBlockCollisions(entity, axisalignedbb, false);
        // BKCommonLib end

        if (entity != null) {
            List<EntityHandle> list = entity.getWorld().getEntities(entity, axisalignedbb.growUniform(0.25D));

            for (int i = 0; i < list.size(); i++) {
                EntityHandle entity1 = list.get(i);

                if (!entity.isInSameVehicle(entity1)) {
                    // BKCommonLib start: block collision event handler
                    AxisAlignedBBHandle axisalignedbb1 = entity1.getOtherBoundingBox();
                    if (axisalignedbb1 != null && axisalignedbb1.bbTransformA(axisalignedbb)
                            && controller.onEntityCollision(entity1.getBukkitEntity())) {

                        collisions_buffer.add(axisalignedbb1);
                    }

                    axisalignedbb1 = entity.getEntityBoundingBox(entity1);
                    if (axisalignedbb1 != null && axisalignedbb1.bbTransformA(axisalignedbb)
                            && controller.onEntityCollision(entity1.getBukkitEntity())) {

                        collisions_buffer.add(axisalignedbb1);
                    }

                    /*
                    if ((axisalignedbb1 != null) && (axisalignedbb1.c(axisalignedbb))) {
                        list.add(axisalignedbb1);
                    }

                    axisalignedbb1 = entity.j(entity1);
                    if ((axisalignedbb1 != null) && (axisalignedbb1.c(axisalignedbb))) {
                        list.add(axisalignedbb1);
                    }
                    */

                    // BKCommonLib end
                }
            }
        }
        return collisions_buffer;
    }

    /**
     * This is the move function based on the original move function in the nms.Entity class.
     * It has been modified so it can run externally, outside the Entity class.
     * Calls to this.world.getCubes(this, this.getBoundingBox().b(a,b,c)) have been replaced with a callback.
     * This callback is getCollisions above, which is a modified copy of the world.getCubes function.
     * 
     * @param movetype move type
     * @param d0 dx
     * @param d1 dy
     * @param d2 dz
     */
    public void move(MoveType movetype, double d0, double d1, double d2) {
        entity = controller.getEntity();
        if (entity == null) {
            throw new IllegalStateException("Entity Controller is not attached to an Entity");
        }
        that = EntityHandle.createHandle(entity.getHandle());

        final Random this_random = that.getRandom();
        WorldHandle world = that.getWorld();

        //org.bukkit.craftbukkit.SpigotTimings.entityMoveTimer.startTiming(); // Spigot
        if (that.isNoclip()) {
            that.setBoundingBox(that.getBoundingBox().translate(d0, d1, d2));
            that.recalcPosition();
        } else {
            // CraftBukkit start - Don't do anything if we aren't moving
            // We need to do that.regardless of whether or not we are moving thanks to portals
            try {
                that.checkBlockCollisions();
            } catch (Throwable throwable) {
                CrashReportHandle crashreport = CrashReportHandle.create(throwable, "Checking entity block collision");
                CrashReportSystemDetailsHandle crashreportsystemdetails = crashreport.getSystemDetails("Entity being checked for collision");

                that.appendEntityCrashDetails(crashreportsystemdetails);
                throw (RuntimeException) ReportedExceptionHandle.createNew(crashreport).getRaw();
            }
            // Check if we're moving
            if (d0 == 0 && d1 == 0 && d2 == 0 && that.isVehicle() && that.isPassenger()) {
                return;
            }
            // CraftBukkit end

            // This logic is only >= 1.11.2
            if (EntityHandle.IS_NEW_MOVE_FUNCTION && movetype == MoveType.PISTON) {
                long i = world.getTime();

                final double[] that_aI = EntityHandle.T.move_SomeArray.get(entity.getHandle());

                if (i != EntityHandle.T.move_SomeState.getLong(entity.getHandle())) {
                    Arrays.fill(that_aI, 0.0D);
                    EntityHandle.T.move_SomeState.setLong(entity.getHandle(), i);
                }

                int j;
                double d3;

                if (d0 != 0.0D) {
                    j = EnumAxisHandle.X.ordinal();
                    d3 = MathUtil.clamp(d0 + that_aI[j], -0.51D, 0.51D);
                    d0 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d0) <= 9.999999747378752E-6D) {
                        return;
                    }
                } else if (d1 != 0.0D) {
                    j = EnumAxisHandle.Y.ordinal();
                    d3 = MathUtil.clamp(d1 + that_aI[j], -0.51D, 0.51D);
                    d1 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d1) <= 9.999999747378752E-6D) {
                        return;
                    }
                } else {
                    if (d2 == 0.0D) {
                        return;
                    }

                    j = EnumAxisHandle.Z.ordinal();
                    d3 = MathUtil.clamp(d2 + that_aI[j], -0.51D, 0.51D);
                    d2 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d2) <= 9.999999747378752E-6D) {
                        return;
                    }
                }
            }

            world.getMethodProfiler().begin("move");

            double d4 = that.getLocX();
            double d5 = that.getLocY();
            double d6 = that.getLocZ();

            if (that.isJustLanded()) {
                that.setJustLanded(false);
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                that.setMotX(0.0);
                that.setMotY(0.0);
                that.setMotZ(0.0);
            }

            double d7 = d0;
            double d8 = d1;
            double d9 = d2;

            if ((movetype == MoveType.SELF || movetype == MoveType.PLAYER) && that.isOnGround() && that.isSneaking() && that.isInstanceOf(EntityHumanHandle.T)) {
                for (/* double d10 = 0.05D*/; d0 != 0.0D && world.getCubes(that, that.getBoundingBox().translate(d0, (double) (-that.getHeightOffset()), 0.0D)).isEmpty(); d7 = d0) {
                    if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                    } else {
                        d0 += 0.05D;
                    }
                }

                for (; d2 != 0.0D && world.getCubes(that, that.getBoundingBox().translate(0.0D, (double) (-that.getHeightOffset()), d2)).isEmpty(); d9 = d2) {
                    if (d2 < 0.05D && d2 >= -0.05D) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= 0.05D;
                    } else {
                        d2 += 0.05D;
                    }
                }

                for (; d0 != 0.0D && d2 != 0.0D && world.getCubes(that, that.getBoundingBox().translate(d0, (double) (-that.getHeightOffset()), d2)).isEmpty(); d9 = d2) {
                    if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                    } else {
                        d0 += 0.05D;
                    }

                    d7 = d0;
                    if (d2 < 0.05D && d2 >= -0.05D) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= 0.05D;
                    } else {
                        d2 += 0.05D;
                    }
                }
            }

            // BKCommonLib start
            // collision event handler
            //List<AxisAlignedBB> list = that.world.getCubes(that, that.getBoundingBox().b(d0, d1, d2));
            List<AxisAlignedBBHandle> list = world_getCubes(that, that.getBoundingBox().transformB(d0, d1, d2));
            // BKCommonLib end

            AxisAlignedBBHandle axisalignedbb = that.getBoundingBox();
            int k;
            int l;

            if (d1 != 0.0D) {
                k = 0;

                for (l = list.size(); k < l; ++k) {
                    d1 = list.get(k).calcSomeY(that.getBoundingBox(), d1);
                }

                that.setBoundingBox(that.getBoundingBox().translate(0.0D, d1, 0.0D));
            }

            if (d0 != 0.0D) {
                k = 0;

                for (l = list.size(); k < l; ++k) {
                    d0 = list.get(k).calcSomeX(that.getBoundingBox(), d0);
                }

                if (d0 != 0.0D) {
                    that.setBoundingBox(that.getBoundingBox().translate(d0, 0.0D, 0.0D));
                }
            }

            if (d2 != 0.0D) {
                k = 0;

                for (l = list.size(); k < l; ++k) {
                    d2 = list.get(k).calcSomeZ(that.getBoundingBox(), d2);
                }

                if (d2 != 0.0D) {
                    that.setBoundingBox(that.getBoundingBox().translate(0.0D, 0.0D, d2));
                }
            }

            boolean flag = that.isOnGround() || d1 != d8 && d1 < 0.0D; // CraftBukkit - decompile error
            double d11;

            if (that.getHeightOffset() > 0.0F && flag && (d7 != d0 || d9 != d2)) {
                double d12 = d0;
                double d13 = d1;
                double d14 = d2;
                AxisAlignedBBHandle axisalignedbb1 = that.getBoundingBox();

                that.setBoundingBox(axisalignedbb);
                d1 = (double) that.getHeightOffset();

                // BKCommonLib start
                // collision event handler
                //List<AxisAlignedBB> list1 = that.world.getCubes(that, that.getBoundingBox().b(d7, d1, d9));
                List<AxisAlignedBBHandle> list1 = world_getCubes(that, that.getBoundingBox().transformB(d7, d1, d9));
                // BKCommonLib end

                AxisAlignedBBHandle axisalignedbb2 = that.getBoundingBox();
                AxisAlignedBBHandle axisalignedbb3 = axisalignedbb2.transformB(d7, 0.0D, d9);

                d11 = d1;
                int i1 = 0;

                for (int j1 = list1.size(); i1 < j1; ++i1) {
                    d11 = list1.get(i1).calcSomeY(axisalignedbb3, d11);
                }

                axisalignedbb2 = axisalignedbb2.translate(0.0D, d11, 0.0D);
                double d15 = d7;
                int k1 = 0;

                for (int l1 = list1.size(); k1 < l1; ++k1) {
                    d15 = list1.get(k1).calcSomeX(axisalignedbb2, d15);
                }

                axisalignedbb2 = axisalignedbb2.translate(d15, 0.0D, 0.0D);
                double d16 = d9;
                int i2 = 0;

                for (int j2 = list1.size(); i2 < j2; ++i2) {
                    d16 = list1.get(i2).calcSomeZ(axisalignedbb2, d16);
                }

                axisalignedbb2 = axisalignedbb2.translate(0.0D, 0.0D, d16);
                AxisAlignedBBHandle axisalignedbb4 = that.getBoundingBox();
                double d17 = d1;
                int k2 = 0;

                for (int l2 = list1.size(); k2 < l2; ++k2) {
                    d17 = list1.get(k2).calcSomeY(axisalignedbb4, d17);
                }

                axisalignedbb4 = axisalignedbb4.translate(0.0D, d17, 0.0D);
                double d18 = d7;
                int i3 = 0;

                for (int j3 = list1.size(); i3 < j3; ++i3) {
                    d18 = list1.get(i3).calcSomeX(axisalignedbb4, d18);
                }

                axisalignedbb4 = axisalignedbb4.translate(d18, 0.0D, 0.0D);
                double d19 = d9;
                int k3 = 0;

                for (int l3 = list1.size(); k3 < l3; ++k3) {
                    d19 = list1.get(k3).calcSomeZ(axisalignedbb4, d19);
                }

                axisalignedbb4 = axisalignedbb4.translate(0.0D, 0.0D, d19);
                double d20 = d15 * d15 + d16 * d16;
                double d21 = d18 * d18 + d19 * d19;

                if (d20 > d21) {
                    d0 = d15;
                    d2 = d16;
                    d1 = -d11;
                    that.setBoundingBox(axisalignedbb2);
                } else {
                    d0 = d18;
                    d2 = d19;
                    d1 = -d17;
                    that.setBoundingBox(axisalignedbb4);
                }

                int i4 = 0;

                for (int j4 = list1.size(); i4 < j4; ++i4) {
                    d1 = list1.get(i4).calcSomeY(that.getBoundingBox(), d1);
                }

                that.setBoundingBox(that.getBoundingBox().translate(0.0D, d1, 0.0D));
                if (d12 * d12 + d14 * d14 >= d0 * d0 + d2 * d2) {
                    d0 = d12;
                    d1 = d13;
                    d2 = d14;
                    that.setBoundingBox(axisalignedbb1);
                }
            }

            world.getMethodProfiler().end();
            world.getMethodProfiler().begin("rest");

            that.recalcPosition();
            that.setHorizontalMovementImpaired(d7 != d0 || d9 != d2);
            that.setVerticalMovementImpaired(d1 != d8); // CraftBukkit - decompile error
            that.setOnGround(that.isVerticalMovementImpaired() && d8 < 0.0);
            that.setMovementImpaired(that.isHorizontalMovementImpaired() || that.isVerticalMovementImpaired());
            l = MathUtil.floor(that.getLocX());
            int k4 = MathUtil.floor(that.getLocY() - 0.2);
            int l4 = MathUtil.floor(that.getLocZ());
            IntVector3 blockposition = new IntVector3(l, k4, l4);

            BlockData iblockdata = world.getBlockData(blockposition);

            if (iblockdata.getType() == org.bukkit.Material.AIR) {
                IntVector3 blockposition1 = blockposition.add(0, -1, 0);
                BlockData iblockdata1 = world.getBlockData(blockposition1);

                BlockHandle block = iblockdata1.getBlock();
                if (block.isInstanceOf(BlockFenceHandle.T) || block.isInstanceOf(BlockCobbleWallHandle.T) || block.isInstanceOf(BlockFenceGateHandle.T)) {
                    iblockdata = iblockdata1;
                    blockposition = blockposition1;
                }
            }

            that.doFallUpdate(d1, that.isOnGround(), iblockdata, blockposition);

            if (d7 != d0) {
                that.setMotX(0.0);
            }

            if (d9 != d2) {
                that.setMotZ(0.0);
            }

            BlockHandle block1 = iblockdata.getBlock();

            if (d8 != d1) {
                block1.entityHitVertical(world, that);
            }

            // CraftBukkit start
            if (that.isHorizontalMovementImpaired() && that.getBukkitEntity() instanceof Vehicle) {
                Vehicle vehicle = (Vehicle) that.getBukkitEntity();
                org.bukkit.block.Block bl = entity.getWorld().getBlockAt(MathUtil.floor(that.getLocX()), MathUtil.floor(that.getLocY()), MathUtil.floor(that.getLocZ()));

                if (d6 > d0) {
                    bl = bl.getRelative(BlockFace.EAST);
                } else if (d6 < d0) {
                    bl = bl.getRelative(BlockFace.WEST);
                } else if (d8 > d2) {
                    bl = bl.getRelative(BlockFace.SOUTH);
                } else if (d8 < d2) {
                    bl = bl.getRelative(BlockFace.NORTH);
                }

                if (bl.getType() != org.bukkit.Material.AIR) {
                    VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl);
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
            // CraftBukkit end

            if (that.hasMovementSound() && (!that.isOnGround() || !that.isSneaking() || !that.isInstanceOf(EntityHumanHandle.T)) && !that.isPassenger()) {
                double d22 = that.getLocX() - d4;
                double d23 = that.getLocY() - d5;

                d11 = that.getLocZ() - d6;
                if (block1.getRaw() != BlocksHandle.LADDER) {
                    d23 = 0.0D;
                }

                if (block1 != null && that.isOnGround()) {
                    iblockdata.stepOn(this.entity.getWorld(), blockposition, this.entity.getEntity());
                }

                that.setWalkedDistanceXZ((float) ((double) that.getWalkedDistanceXZ() + Math.sqrt(d22 * d22 + d11 * d11) * 0.6D));
                that.setWalkedDistanceXYZ((float) ((double) that.getWalkedDistanceXYZ() + Math.sqrt(d22 * d22 + d23 * d23 + d11 * d11) * 0.6D));
                if (that.getWalkedDistanceXYZ() > (float) that.getStepCounter() && iblockdata.getType() != org.bukkit.Material.AIR) {
                    that.setStepCounter((int) that.getWalkedDistanceXYZ() + 1);
                    if (that.isInWater()) {
                        EntityHandle entity = that.isVehicle() ? that.getDriverEntity() : null;

                        float f = entity == this.entity.getEntity() ? 0.35F : 0.4F;
                        float f1 = (float) Math.sqrt(entity.getMotX() * entity.getMotX() * 0.2 + entity.getMotY() * entity.getMotY() + entity.getMotZ() * entity.getMotZ() * 0.2) * f;

                        if (f1 > 1.0F) {
                            f1 = 1.0F;
                        }

                        that.makeSound(that.getSwimSound(), f1, 1.0F + (this_random.nextFloat() - this_random.nextFloat()) * 0.4F);
                    } else {
                        that.doStepSoundUpdate(blockposition, iblockdata);
                    }
                }
            }

            // CraftBukkit start - Move to the top of the method
            /*
            try {
                that.checkBlockCollisions();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

                that.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }
            */
            // CraftBukkit end

            boolean flag1 = that.isWet();

            if (world.isBurnArea(that.getBoundingBox().shrinkUniform(0.001))) {
                that.burn(1.0f);
                if (!flag1) {
                    that.setFireTicks(that.getFireTicks() + 1);
                    if (that.getFireTicks() == 0) {
                        // CraftBukkit start
                        EntityCombustEvent event = new org.bukkit.event.entity.EntityCombustByBlockEvent(null, entity.getEntity(), 8);
                        Bukkit.getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            that.setOnFire(event.getDuration());
                        }
                        // CraftBukkit end
                    }
                }
            } else if (that.getFireTicks() <= 0) {
                that.setFireTicks(-that.getMaxFireTicks());
            }

            if (flag1 && that.isBurning()) {
                that.makeSound(SoundEffectsHandle.EXTINGUISH_FIRE, 0.7F, 1.6F + (this_random.nextFloat() - this_random.nextFloat()) * 0.4F);
                that.setFireTicks(-that.getMaxFireTicks());
            }

            world.getMethodProfiler().end();
        }
        //org.bukkit.craftbukkit.SpigotTimings.entityMoveTimer.stopTiming(); // Spigot
    }

}
