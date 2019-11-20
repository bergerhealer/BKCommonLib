package com.bergerkiller.bukkit.common.internal.logic;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.io.StreamAccumulator;
import com.bergerkiller.bukkit.common.resources.CommonSounds;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
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
import com.bergerkiller.generated.net.minecraft.server.VoxelShapeHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle.EnumAxisHandle;

/**
 * Handles the full Entity move() physics function. It should be kept completely in sync with what is used on the server,
 * with the insertion of several important event handlers (block collision, entity collision)
 * 
 * These collisions are to be handled by the controller attached to the Entity
 */
public abstract class EntityMoveHandler {
    protected boolean blockCollisionEnabled = true;
    protected boolean entityCollisionEnabled = true;
    protected Vector customBlockCollisionBounds = null; // null = entity.getBoundingBox() unchanged
    protected EntityController<?> controller;
    protected EntityHandle that;
    private CommonEntity<?> entity;
    private final StreamAccumulator<VoxelShapeHandle> shapeAccumulator = new StreamAccumulator<VoxelShapeHandle>();

    protected EntityMoveHandler() {
    }

    public static EntityMoveHandler create(EntityController<?> controller) {
        EntityMoveHandler handler;
        if (Common.evaluateMCVersion(">=", "1.14")) {
            handler = new EntityMoveHandler_1_14();
        } else if (CommonCapabilities.HAS_VOXELSHAPE_LOGIC) {
            handler = new EntityMoveHandler_1_13();
        } else if (WorldHandle.T.getBlockCollisions.isAvailable()) {
            handler = new EntityMoveHandler_1_11_2();
        } else {
            handler = new EntityMoveHandler_1_8();
        }
        handler.controller = controller;
        return handler;
    }

    public void setBlockCollisionEnabled(boolean enabled) {
        this.blockCollisionEnabled = enabled;
    }

    public void setEntityCollisionEnabled(boolean enabled) {
        this.entityCollisionEnabled = enabled;
    }

    public void setCustomBlockCollisionBounds(Vector bounds) {
        if (bounds == null) {
            this.customBlockCollisionBounds = null;
        } else {
            this.customBlockCollisionBounds = bounds.clone();
        }
    }

    // Gets the AxisAlignedBB bounding box to use for computing Block Collisions
    protected AxisAlignedBBHandle getBlockBoundingBox(EntityHandle entity) {
        AxisAlignedBBHandle boundingBox = entity.getBoundingBox();
        if (this.blockCollisionEnabled && this.customBlockCollisionBounds != null) {
            double x = 0.5 * (boundingBox.getMinX() + boundingBox.getMaxX());
            double y = boundingBox.getMinY();
            double z = 0.5 * (boundingBox.getMinZ() + boundingBox.getMaxZ());
            return AxisAlignedBBHandle.createNew(
                    x - 0.5 * this.customBlockCollisionBounds.getX(),
                    y,
                    z - 0.5 * this.customBlockCollisionBounds.getZ(),
                    x + 0.5 * this.customBlockCollisionBounds.getX(),
                    y + this.customBlockCollisionBounds.getY(),
                    z + 0.5 * this.customBlockCollisionBounds.getZ());
        }
        return boundingBox;
    }

    /**
     * Creates a VoxelShape combining all the shaped collided with moving the entity with an offset
     * 
     * @param entity
     * @param dx movement
     * @param dy movement
     * @param dz movement
     * @return stream of collision shapes
     */
    protected abstract Stream<VoxelShapeHandle> world_getCollisionShapes(EntityHandle entity, double dx, double dy, double dz);

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
            if (CommonCapabilities.ENTITY_MOVE_VER2 && movetype == MoveType.PISTON) {
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

            world.method_profiler_begin("move");

            double d4 = that.getLocX();
            double d5 = that.getLocY();
            double d6 = that.getLocZ();

            if (that.isCollidingWithBlock()) {
                if (this.blockCollisionEnabled) {
                    Vector multiplier = that.getBlockCollisionMultiplier();
                    d0 *= multiplier.getX();
                    d1 *= multiplier.getY();
                    d2 *= multiplier.getZ();
                    that.setMot(0.0, 0.0, 0.0);
                }
                that.setNotCollidingWithBlock();
            }

            double d7 = d0;
            double d8 = d1;
            double d9 = d2;

            if ((movetype == MoveType.SELF || movetype == MoveType.PLAYER) && that.isOnGround() && that.isSneaking() && that.isInstanceOf(EntityHumanHandle.T)) {
                for (/* double d10 = 0.05D*/; d0 != 0.0D && world.isNotCollidingWithBlocks(that, that.getBoundingBox().translate(d0, (double) (-that.getHeightOffset()), 0.0D)); d7 = d0) {
                    if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                    } else {
                        d0 += 0.05D;
                    }
                }

                for (; d2 != 0.0D && world.isNotCollidingWithBlocks(that, that.getBoundingBox().translate(0.0D, (double) (-that.getHeightOffset()), d2)); d9 = d2) {
                    if (d2 < 0.05D && d2 >= -0.05D) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= 0.05D;
                    } else {
                        d2 += 0.05D;
                    }
                }

                for (; d0 != 0.0D && d2 != 0.0D && world.isNotCollidingWithBlocks(that, that.getBoundingBox().translate(d0, (double) (-that.getHeightOffset()), d2)); d9 = d2) {
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

            // Store old bounding box before changes to it are made
            AxisAlignedBBHandle axisalignedbb = that.getBoundingBox();

            if (d0 != 0.0D || d1 != 0.0D || d2 != 0.0D) {
                // BKCommonLib start
                // collision event handler
                shapeAccumulator.open(world_getCollisionShapes(that, d0, d1, d2));
                // BKCommonLib end

                // BKCommonLib: add isEmpty() optimization
                if (!shapeAccumulator.isEmpty()) {
                    if (d1 <= -1.0E-7D || d1 >= 1.0E-7D) {
                        d1 = VoxelShapeHandle.traceAxis(EnumAxisHandle.Y, that.getBoundingBox(), shapeAccumulator.stream(), d1);
                        if (d1 != 0.0D) {
                            that.setBoundingBox(that.getBoundingBox().translate(0.0D, d1, 0.0D));
                        }
                    }

                    if (d0 <= -1.0E-7D || d0 >= 1.0E-7D) {
                        d0 = VoxelShapeHandle.traceAxis(EnumAxisHandle.X, that.getBoundingBox(), shapeAccumulator.stream(), d0);
                        if (d0 != 0.0D) {
                            that.setBoundingBox(that.getBoundingBox().translate(d0, 0.0D, 0.0D));
                        }
                    }

                    if (d2 <= -1.0E-7D || d2 >= 1.0E-7D) {
                        d2 = VoxelShapeHandle.traceAxis(EnumAxisHandle.Z, that.getBoundingBox(), shapeAccumulator.stream(), d2);
                        if (d2 != 0.0D) {
                            that.setBoundingBox(that.getBoundingBox().translate(0.0D, 0.0D, d2));
                        }
                    }
                } else {
                    that.setBoundingBox(that.getBoundingBox().translate(d0, d1, d2));
                }
            }

            boolean flag = that.isOnGround() || d1 != d8 && d1 < 0.0D; // CraftBukkit - decompile error

            // NB: This code only executes with entities that are tall (have a head), like players
            if (that.getHeightOffset() > 0.0F && flag && (d7 != d0 || d9 != d2)) {
                double d12 = d0;
                double d13 = d1;
                double d14 = d2;
                AxisAlignedBBHandle axisalignedbb1 = that.getBoundingBox();

                that.setBoundingBox(axisalignedbb);
                d1 = (double) that.getHeightOffset();

                // BKCommonLib start
                // collision event handler
                shapeAccumulator.open(world_getCollisionShapes(that, d7, d1, d9));
                // BKCommonLib end

                AxisAlignedBBHandle axisalignedbb2 = that.getBoundingBox();
                AxisAlignedBBHandle axisalignedbb3 = axisalignedbb2.transformB(d7, 0.0D, d9);

                double d11 = d1;
                d11 = VoxelShapeHandle.traceAxis(EnumAxisHandle.Y, axisalignedbb3, shapeAccumulator.stream(), d11);
                if (d11 != 0.0D) {
                    axisalignedbb2 = axisalignedbb2.translate(0.0D, d11, 0.0D);
                }

                double d15 = d7;
                d15 = VoxelShapeHandle.traceAxis(EnumAxisHandle.X, axisalignedbb2, shapeAccumulator.stream(), d15);
                if (d15 != 0.0D) {
                    axisalignedbb2 = axisalignedbb2.translate(d15, 0.0D, 0.0D);
                }

                double d16 = d9;
                d16 = VoxelShapeHandle.traceAxis(EnumAxisHandle.Z, axisalignedbb2, shapeAccumulator.stream(), d16);
                if (d16 != 0.0D) {
                    axisalignedbb2 = axisalignedbb2.translate(0.0D, 0.0D, d16);
                }

                AxisAlignedBBHandle axisalignedbb4 = that.getBoundingBox();

                double d17 = d1;
                d17 = VoxelShapeHandle.traceAxis(EnumAxisHandle.Y, axisalignedbb4, shapeAccumulator.stream(), d17);
                if (d17 != 0.0D) {
                    axisalignedbb4 = axisalignedbb4.translate(0.0D, d17, 0.0D);
                }

                double d18 = d7;
                d18 = VoxelShapeHandle.traceAxis(EnumAxisHandle.X, axisalignedbb4, shapeAccumulator.stream(), d18);
                if (d18 != 0.0D) {
                    axisalignedbb4 = axisalignedbb4.translate(d18, 0.0D, 0.0D);
                }

                double d19 = d9;
                d19 = VoxelShapeHandle.traceAxis(EnumAxisHandle.Z, axisalignedbb4, shapeAccumulator.stream(), d19);
                if (d19 != 0.0D) {
                    axisalignedbb4 = axisalignedbb4.translate(0.0D, 0.0D, d19);
                }

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

                d1 = VoxelShapeHandle.traceAxis(EnumAxisHandle.Y, that.getBoundingBox(), shapeAccumulator.stream(), d1);
                if (d1 != 0.0D) {
                    that.setBoundingBox(that.getBoundingBox().translate(0.0D, d1, 0.0D));
                }

                if (d12 * d12 + d14 * d14 >= d0 * d0 + d2 * d2) {
                    d0 = d12;
                    d1 = d13;
                    d2 = d14;
                    that.setBoundingBox(axisalignedbb1);
                }
            } // NB: This code only executes with entities that are tall (have a head), like players

            world.method_profiler_end();
            world.method_profiler_begin("rest");

            that.recalcPosition();
            that.setHorizontalMovementImpaired(d7 != d0 || d9 != d2);
            that.setVerticalMovementImpaired(d1 != d8); // CraftBukkit - decompile error
            that.setOnGround(that.isVerticalMovementImpaired() && d8 < 0.0);
            that.setMovementImpaired(that.isHorizontalMovementImpaired() || that.isVerticalMovementImpaired());

            int l = MathUtil.floor(that.getLocX());
            int k4 = MathUtil.floor(that.getLocY() - 0.2);
            int l4 = MathUtil.floor(that.getLocZ());
            IntVector3 blockposition = new IntVector3(l, k4, l4);

            BlockData iblockdata = world.getBlockData(blockposition);

            if (iblockdata.isType(org.bukkit.Material.AIR)) {
                IntVector3 blockposition1 = blockposition.add(0, -1, 0);
                BlockData iblockdata1 = world.getBlockData(blockposition1);

                BlockHandle block = iblockdata1.getBlock();
                if (block.isInstanceOf(BlockFenceHandle.T) || block.isInstanceOf(BlockCobbleWallHandle.T) || block.isInstanceOf(BlockFenceGateHandle.T)) {
                    iblockdata = iblockdata1;
                    blockposition = blockposition1;
                }
            }

            that.updateFalling(d1, that.isOnGround(), iblockdata, blockposition);

            if (d7 != d0) {
                that.setMotX(0.0);
            }

            if (d9 != d2) {
                that.setMotZ(0.0);
            }

            if (d8 != d1) {
                iblockdata.getBlock().entityHitVertical(world, that);
            }

            // CraftBukkit start
            if (that.isHorizontalMovementImpaired() && that.getBukkitEntity() instanceof Vehicle) {

                /*
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
                */

                // BKCommonLib: avoid creating an extra Block with getRelative if we don't have to!
                // Similarly, we don't have to create this Block when the block is air...
                org.bukkit.World bl_world = entity.getWorld();
                int bl_x = MathUtil.floor(that.getLocX());
                int bl_y = MathUtil.floor(that.getLocY());
                int bl_z = MathUtil.floor(that.getLocZ());
                if (d6 > d0) {
                    bl_x++;
                } else if (d6 < d0) {
                    bl_x--;
                } else if (d8 > d2) {
                    bl_z++;
                } else if (d8 < d2) {
                    bl_z--;
                }
                if (!WorldUtil.getBlockData(bl_world, bl_x, bl_y, bl_z).isType(org.bukkit.Material.AIR)) {
                    org.bukkit.block.Block bl = bl_world.getBlockAt(bl_x, bl_y, bl_z);
                    Vehicle vehicle = (Vehicle) that.getBukkitEntity();
                    VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl);
                    Bukkit.getPluginManager().callEvent(event);
                }
                // BKCommonLib end

            }
            // CraftBukkit end

            if (that.hasMovementSound() && (!that.isOnGround() || !that.isSneaking() || !that.isInstanceOf(EntityHumanHandle.T)) && !that.isPassenger()) {
                double d22 = that.getLocX() - d4;
                double d23 = that.getLocY() - d5;
                double d11 = that.getLocZ() - d6;

                BlockHandle block1 = iblockdata.getBlock();

                if (block1.getRaw() != BlocksHandle.LADDER) {
                    d23 = 0.0D;
                }

                if (block1 != null && that.isOnGround()) {
                    iblockdata.stepOn(this.entity.getWorld(), blockposition, this.entity.getEntity());
                }

                that.setWalkedDistanceXZ((float) ((double) that.getWalkedDistanceXZ() + Math.sqrt(d22 * d22 + d11 * d11) * 0.6D));
                that.setWalkedDistanceXYZ((float) ((double) that.getWalkedDistanceXYZ() + Math.sqrt(d22 * d22 + d23 * d23 + d11 * d11) * 0.6D));
                if (that.getWalkedDistanceXYZ() > (float) that.getStepCounter() && !iblockdata.isType(org.bukkit.Material.AIR)) {
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
                        that.playStepSound(blockposition, iblockdata);
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
                that.makeSound(CommonSounds.EXTINGUISH, 0.7F, 1.6F + (this_random.nextFloat() - this_random.nextFloat()) * 0.4F);
                that.setFireTicks(-that.getMaxFireTicks());
            }

            world.method_profiler_end();
        }
        //org.bukkit.craftbukkit.SpigotTimings.entityMoveTimer.stopTiming(); // Spigot
    }

}
