package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;

import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;
import com.bergerkiller.server.CommonNMS;
import com.google.common.collect.Lists;

import net.minecraft.server.v1_11_R1.AxisAlignedBB;
import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.BlockCobbleWall;
import net.minecraft.server.v1_11_R1.BlockFence;
import net.minecraft.server.v1_11_R1.BlockFenceGate;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Blocks;
import net.minecraft.server.v1_11_R1.CrashReport;
import net.minecraft.server.v1_11_R1.CrashReportSystemDetails;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.EnumDirection;
import net.minecraft.server.v1_11_R1.IBlockData;
import net.minecraft.server.v1_11_R1.Material;
import net.minecraft.server.v1_11_R1.MathHelper;
import net.minecraft.server.v1_11_R1.ReportedException;
import net.minecraft.server.v1_11_R1.SoundEffect;
import net.minecraft.server.v1_11_R1.SoundEffects;
import net.minecraft.server.v1_11_R1.World;
import net.minecraft.server.v1_11_R1.WorldBorder;

/**
 * Handles the full Entity move() physics function. It should be kept completely in sync with what is used on the server,
 * with the insertion of several important event handlers (block collision, entity collision)
 * 
 * These collisions are to be handled by the controller attached to the Entity
 */
public class EntityMoveHandler {
    private static final List<AxisAlignedBB> collisions_buffer = new ArrayList<AxisAlignedBB>();
    EntityController<?> controller;
    CommonEntity<?> entity;
    Entity that;

    public EntityMoveHandler(EntityController<?> theController) {
        controller = theController;
    }

    private boolean world_getBlockCollisions(Entity entity, AxisAlignedBB axisalignedbb, boolean flag, List<AxisAlignedBB> list) {
        if (!NMSWorld.getBlockCollisions.invoke(entity.world, entity, axisalignedbb, flag, list)) {
            return false;
        }

        org.bukkit.World bWorld = Conversion.toWorld.convert(entity.world);

        // Send all found bounds in the list through a filter calling onBlockCollision
        // Handle block collisions
        BlockFace hitFace;
        Iterator<AxisAlignedBB> iter = collisions_buffer.iterator();
        AxisAlignedBB blockBounds;
        double dx, dz;
        while (iter.hasNext()) {
            blockBounds = iter.next();
            // Convert to block and block coordinates
            org.bukkit.block.Block block = bWorld.getBlockAt(MathUtil.floor(blockBounds.a), MathUtil.floor(blockBounds.b), MathUtil.floor(blockBounds.c));

            // Find out what direction the block is hit
            if (axisalignedbb.e > blockBounds.e) {
                hitFace = BlockFace.UP;
            } else if (axisalignedbb.b < blockBounds.b) {
                hitFace = BlockFace.DOWN;
            } else {
                dx = entity.locX - block.getX() - 0.5;
                dz = entity.locZ - block.getZ() - 0.5;
                hitFace = FaceUtil.getDirection(dx, dz, false);
            }
            // Block collision event
            if (!controller.onBlockCollision(block, hitFace)) {
                iter.remove();
            }
        }

        return true;
    }

    private List<AxisAlignedBB> world_getCubes(Entity entity, AxisAlignedBB axisalignedbb) {
        collisions_buffer.clear(); // BKCommonLib edit: use cached list
        World world = entity.world;

        // BKCommonLib start: replace world_getBlockCollisions call; use cached list instead
        //world.a(entity, axisalignedbb, false, arraylist);
        world_getBlockCollisions(entity, axisalignedbb, false, collisions_buffer);
        // BKCommonLib end

        if (entity != null) {
            List<Entity> list = world.getEntities(entity, axisalignedbb.g(0.25D));

            for (int i = 0; i < list.size(); i++) {
                Entity entity1 = (Entity)list.get(i);

                if (!entity.x(entity1)) {
                    // BKCommonLib start: block collision event handler
                    AxisAlignedBB axisalignedbb1 = entity1.ag();
                    if (axisalignedbb1 != null && axisalignedbb1.c(axisalignedbb)
                            && controller.onEntityCollision(entity1.getBukkitEntity())) {

                        collisions_buffer.add(axisalignedbb1);
                    }

                    axisalignedbb1 = entity.j(entity1);
                    if (axisalignedbb1 != null && axisalignedbb1.c(axisalignedbb)
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
        that = (Entity) entity.getHandle();
        final Random this_random = NMSEntity.random.get(that);
        final double[] that_aI = NMSEntity.move_SomeArray.get(that);

        //org.bukkit.craftbukkit.SpigotTimings.entityMoveTimer.startTiming(); // Spigot
        if (that.noclip) {
            that.a(that.getBoundingBox().d(d0, d1, d2));
            that.recalcPosition();
        } else {
            // CraftBukkit start - Don't do anything if we aren't moving
            // We need to do that.regardless of whether or not we are moving thanks to portals
            try {
                NMSEntity.checkBlockCollisions.invoke(that);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

                that.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }
            // Check if we're moving
            if (d0 == 0 && d1 == 0 && d2 == 0 && that.isVehicle() && that.isPassenger()) {
                return;
            }
            // CraftBukkit end
            if (movetype == MoveType.PISTON) {
                long i = that.world.getTime();

                if (i != NMSEntity.move_SomeState.get(that)) {
                    Arrays.fill(that_aI, 0.0D);
                    NMSEntity.move_SomeState.set(that, i);
                }

                int j;
                double d3;

                if (d0 != 0.0D) {
                    j = EnumDirection.EnumAxis.X.ordinal();
                    d3 = MathHelper.a(d0 + that_aI[j], -0.51D, 0.51D);
                    d0 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d0) <= 9.999999747378752E-6D) {
                        return;
                    }
                } else if (d1 != 0.0D) {
                    j = EnumDirection.EnumAxis.Y.ordinal();
                    d3 = MathHelper.a(d1 + that_aI[j], -0.51D, 0.51D);
                    d1 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d1) <= 9.999999747378752E-6D) {
                        return;
                    }
                } else {
                    if (d2 == 0.0D) {
                        return;
                    }

                    j = EnumDirection.EnumAxis.Z.ordinal();
                    d3 = MathHelper.a(d2 + that_aI[j], -0.51D, 0.51D);
                    d2 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d2) <= 9.999999747378752E-6D) {
                        return;
                    }
                }
            }

            that.world.methodProfiler.a("move");
            double d4 = that.locX;
            double d5 = that.locY;
            double d6 = that.locZ;

            if (NMSEntity.justLanded.get(that)) {
                NMSEntity.justLanded.set(that, false);
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                that.motX = 0.0D;
                that.motY = 0.0D;
                that.motZ = 0.0D;
            }

            double d7 = d0;
            double d8 = d1;
            double d9 = d2;

            if ((movetype == MoveType.SELF || movetype == MoveType.PLAYER) && that.onGround && that.isSneaking() && that instanceof EntityHuman) {
                for (double d10 = 0.05D; d0 != 0.0D && that.world.getCubes(that, that.getBoundingBox().d(d0, (double) (-that.P), 0.0D)).isEmpty(); d7 = d0) {
                    if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                    } else {
                        d0 += 0.05D;
                    }
                }

                for (; d2 != 0.0D && that.world.getCubes(that, that.getBoundingBox().d(0.0D, (double) (-that.P), d2)).isEmpty(); d9 = d2) {
                    if (d2 < 0.05D && d2 >= -0.05D) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= 0.05D;
                    } else {
                        d2 += 0.05D;
                    }
                }

                for (; d0 != 0.0D && d2 != 0.0D && that.world.getCubes(that, that.getBoundingBox().d(d0, (double) (-that.P), d2)).isEmpty(); d9 = d2) {
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
            List<AxisAlignedBB> list = world_getCubes(that, that.getBoundingBox().b(d0, d1, d2));
            // BKCommonLib end

            AxisAlignedBB axisalignedbb = that.getBoundingBox();
            int k;
            int l;

            if (d1 != 0.0D) {
                k = 0;

                for (l = list.size(); k < l; ++k) {
                    d1 = ((AxisAlignedBB) list.get(k)).b(that.getBoundingBox(), d1);
                }

                that.a(that.getBoundingBox().d(0.0D, d1, 0.0D));
            }

            if (d0 != 0.0D) {
                k = 0;

                for (l = list.size(); k < l; ++k) {
                    d0 = ((AxisAlignedBB) list.get(k)).a(that.getBoundingBox(), d0);
                }

                if (d0 != 0.0D) {
                    that.a(that.getBoundingBox().d(d0, 0.0D, 0.0D));
                }
            }

            if (d2 != 0.0D) {
                k = 0;

                for (l = list.size(); k < l; ++k) {
                    d2 = ((AxisAlignedBB) list.get(k)).c(that.getBoundingBox(), d2);
                }

                if (d2 != 0.0D) {
                    that.a(that.getBoundingBox().d(0.0D, 0.0D, d2));
                }
            }

            boolean flag = that.onGround || d1 != d8 && d1 < 0.0D; // CraftBukkit - decompile error
            double d11;

            if (that.P > 0.0F && flag && (d7 != d0 || d9 != d2)) {
                double d12 = d0;
                double d13 = d1;
                double d14 = d2;
                AxisAlignedBB axisalignedbb1 = that.getBoundingBox();

                that.a(axisalignedbb);
                d1 = (double) that.P;

                // BKCommonLib start
                // collision event handler
                //List<AxisAlignedBB> list1 = that.world.getCubes(that, that.getBoundingBox().b(d7, d1, d9));
                List<AxisAlignedBB> list1 = world_getCubes(that, that.getBoundingBox().b(d7, d1, d9));
                // BKCommonLib end

                AxisAlignedBB axisalignedbb2 = that.getBoundingBox();
                AxisAlignedBB axisalignedbb3 = axisalignedbb2.b(d7, 0.0D, d9);

                d11 = d1;
                int i1 = 0;

                for (int j1 = list1.size(); i1 < j1; ++i1) {
                    d11 = ((AxisAlignedBB) list1.get(i1)).b(axisalignedbb3, d11);
                }

                axisalignedbb2 = axisalignedbb2.d(0.0D, d11, 0.0D);
                double d15 = d7;
                int k1 = 0;

                for (int l1 = list1.size(); k1 < l1; ++k1) {
                    d15 = ((AxisAlignedBB) list1.get(k1)).a(axisalignedbb2, d15);
                }

                axisalignedbb2 = axisalignedbb2.d(d15, 0.0D, 0.0D);
                double d16 = d9;
                int i2 = 0;

                for (int j2 = list1.size(); i2 < j2; ++i2) {
                    d16 = ((AxisAlignedBB) list1.get(i2)).c(axisalignedbb2, d16);
                }

                axisalignedbb2 = axisalignedbb2.d(0.0D, 0.0D, d16);
                AxisAlignedBB axisalignedbb4 = that.getBoundingBox();
                double d17 = d1;
                int k2 = 0;

                for (int l2 = list1.size(); k2 < l2; ++k2) {
                    d17 = ((AxisAlignedBB) list1.get(k2)).b(axisalignedbb4, d17);
                }

                axisalignedbb4 = axisalignedbb4.d(0.0D, d17, 0.0D);
                double d18 = d7;
                int i3 = 0;

                for (int j3 = list1.size(); i3 < j3; ++i3) {
                    d18 = ((AxisAlignedBB) list1.get(i3)).a(axisalignedbb4, d18);
                }

                axisalignedbb4 = axisalignedbb4.d(d18, 0.0D, 0.0D);
                double d19 = d9;
                int k3 = 0;

                for (int l3 = list1.size(); k3 < l3; ++k3) {
                    d19 = ((AxisAlignedBB) list1.get(k3)).c(axisalignedbb4, d19);
                }

                axisalignedbb4 = axisalignedbb4.d(0.0D, 0.0D, d19);
                double d20 = d15 * d15 + d16 * d16;
                double d21 = d18 * d18 + d19 * d19;

                if (d20 > d21) {
                    d0 = d15;
                    d2 = d16;
                    d1 = -d11;
                    that.a(axisalignedbb2);
                } else {
                    d0 = d18;
                    d2 = d19;
                    d1 = -d17;
                    that.a(axisalignedbb4);
                }

                int i4 = 0;

                for (int j4 = list1.size(); i4 < j4; ++i4) {
                    d1 = ((AxisAlignedBB) list1.get(i4)).b(that.getBoundingBox(), d1);
                }

                that.a(that.getBoundingBox().d(0.0D, d1, 0.0D));
                if (d12 * d12 + d14 * d14 >= d0 * d0 + d2 * d2) {
                    d0 = d12;
                    d1 = d13;
                    d2 = d14;
                    that.a(axisalignedbb1);
                }
            }

            that.world.methodProfiler.b();
            that.world.methodProfiler.a("rest");
            that.recalcPosition();
            that.positionChanged = d7 != d0 || d9 != d2;
            that.B = d1 != d8; // CraftBukkit - decompile error
            that.onGround = that.B && d8 < 0.0D;
            that.C = that.positionChanged || that.B;
            l = MathHelper.floor(that.locX);
            int k4 = MathHelper.floor(that.locY - 0.20000000298023224D);
            int l4 = MathHelper.floor(that.locZ);
            BlockPosition blockposition = new BlockPosition(l, k4, l4);
            IBlockData iblockdata = that.world.getType(blockposition);

            if (iblockdata.getMaterial() == Material.AIR) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata1 = that.world.getType(blockposition1);
                Block block = iblockdata1.getBlock();

                if (block instanceof BlockFence || block instanceof BlockCobbleWall || block instanceof BlockFenceGate) {
                    iblockdata = iblockdata1;
                    blockposition = blockposition1;
                }
            }

            NMSEntity.doFallUpdate.invoke(that, d1, that.onGround, iblockdata, blockposition);

            if (d7 != d0) {
                that.motX = 0.0D;
            }

            if (d9 != d2) {
                that.motZ = 0.0D;
            }

            Block block1 = iblockdata.getBlock();

            if (d8 != d1) {
                block1.a(that.world, that);
            }

            // CraftBukkit start
            if (that.positionChanged && that.getBukkitEntity() instanceof Vehicle) {
                Vehicle vehicle = (Vehicle) that.getBukkitEntity();
                org.bukkit.block.Block bl = that.world.getWorld().getBlockAt(MathHelper.floor(that.locX), MathHelper.floor(that.locY), MathHelper.floor(that.locZ));

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
                    that.world.getServer().getPluginManager().callEvent(event);
                }
            }
            // CraftBukkit end

            if (NMSEntity.hasMovementSound(that) && (!that.onGround || !that.isSneaking() || !(that instanceof EntityHuman)) && !that.isPassenger()) {
                double d22 = that.locX - d4;
                double d23 = that.locY - d5;

                d11 = that.locZ - d6;
                if (block1 != Blocks.LADDER) {
                    d23 = 0.0D;
                }

                if (block1 != null && that.onGround) {
                    block1.stepOn(that.world, blockposition, that);
                }

                that.J = (float) ((double) that.J + (double) MathHelper.sqrt(d22 * d22 + d11 * d11) * 0.6D);
                that.K = (float) ((double) that.K + (double) MathHelper.sqrt(d22 * d22 + d23 * d23 + d11 * d11) * 0.6D);
                if (that.K > (float) NMSEntity.stepCounter.get(that) && iblockdata.getMaterial() != Material.AIR) {
                    NMSEntity.stepCounter.set(that, (int) that.K + 1);
                    if (that.isInWater()) {
                        Entity entity = that.isVehicle() && that.bw() != null ? that.bw() : that;
                        float f = entity == that ? 0.35F : 0.4F;
                        float f1 = MathHelper.sqrt(entity.motX * entity.motX * 0.20000000298023224D + entity.motY * entity.motY + entity.motZ * entity.motZ * 0.20000000298023224D) * f;

                        if (f1 > 1.0F) {
                            f1 = 1.0F;
                        }

                        that.a((SoundEffect) NMSEntity.getSwimSound.invoke(that), f1, 1.0F + (this_random.nextFloat() - this_random.nextFloat()) * 0.4F);
                    } else {
                        NMSEntity.doStepSoundUpdate.invoke(that, blockposition, block1);
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

            boolean flag1 = that.ai();

            if (that.world.e(that.getBoundingBox().shrink(0.001D))) {
                NMSEntity.burn(that, 1);
                if (!flag1) {
                    ++that.fireTicks;
                    if (that.fireTicks == 0) {
                        // CraftBukkit start
                        EntityCombustEvent event = new org.bukkit.event.entity.EntityCombustByBlockEvent(null, entity.getEntity(), 8);
                        that.world.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            that.setOnFire(event.getDuration());
                        }
                        // CraftBukkit end
                    }
                }
            } else if (that.fireTicks <= 0) {
                that.fireTicks = -that.getMaxFireTicks();
            }

            if (flag1 && that.isBurning()) {
                that.a(SoundEffects.bQ, 0.7F, 1.6F + (this_random.nextFloat() - this_random.nextFloat()) * 0.4F);
                that.fireTicks = -that.getMaxFireTicks();
            }

            that.world.methodProfiler.b();
        }
        //org.bukkit.craftbukkit.SpigotTimings.entityMoveTimer.stopTiming(); // Spigot
    }

}
