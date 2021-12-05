package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.proxy.VoxelShapeProxy;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.world.phys.shapes.VoxelShapeHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AxisAlignedBBHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;

/**
 * Logic for MC 1.8 - 1.11
 */
class EntityMoveHandler_1_8 extends EntityMoveHandler {
    private static boolean loggedEntityCollisionUndoFailure = false;
    private static final List<AxisAlignedBBHandle> collisions_buffer = new ArrayList<AxisAlignedBBHandle>();

    @Override
    protected Stream<VoxelShapeHandle> world_getCollisionShapes(EntityHandle entity, double mx, double my, double mz) {
        // If all collision is disabled, simply return an empty shape
        if (!this.blockCollisionEnabled && !this.entityCollisionEnabled) {
            return Stream.empty();
        }

        // Use legacy logic on 1.12.2 and earlier
        List<AxisAlignedBBHandle> cubes = world_getCubes(entity, mx, my, mz);
        if (cubes.isEmpty()) {
            return Stream.empty();
        } else {
            return MountiplexUtil.toStream(VoxelShapeHandle.createHandle(VoxelShapeProxy.fromAABBHandles(cubes)));
        }
    }

    @Override
    public boolean onBlockCollided(Block block) {
        return true;
    }

    @Override
    public boolean isBlockCollisionsMethodInitialized() {
        return true;
    }

    /**
     * Adds all the bounding boxes of blocks that collide with the movedBounds boundingbox
     * 
     * @param entity that moved
     * @param movedBounds bounding box
     * @param cubes result list
     * @return True if cubes were found
     */
    protected boolean world_getBlockCubes(EntityHandle entity, AxisAlignedBBHandle movedBounds, List<AxisAlignedBBHandle> cubes) {
        List<AxisAlignedBBHandle> foundBounds = WorldHandle.T.opt_getCubes_1_8.invoke(entity.getWorld().getRaw(), entity, movedBounds);
        if (foundBounds.isEmpty()) {
            return false;
        }

        // Remove all collisions that have to do with Entities; not Blocks.
        // This is a bit of a hacked in way for backwards <= 1.10.2 support
        // Basically, we repeat getCubes() and ignore all found bounding boxes in here
        List<EntityHandle> list = entity.getWorld().getNearbyEntities(entity, movedBounds.growUniform(0.25D));
        for (EntityHandle entity1 : list) {
            if (CommonCapabilities.VEHICLES_COLLIDE_WITH_PASSENGERS || !entity.isInSameVehicle(entity1)) {
                // BKCommonLib start: block collision event handler
                AxisAlignedBBHandle axisalignedbb1 = entity1.getOtherBoundingBox();
                if (axisalignedbb1 != null && axisalignedbb1.bbTransformA(movedBounds)) {
                    removeFromList(foundBounds, axisalignedbb1);
                }

                axisalignedbb1 = entity.getEntityBoundingBox(entity1);
                if (axisalignedbb1 != null && axisalignedbb1.bbTransformA(movedBounds)) {
                    removeFromList(foundBounds, axisalignedbb1);
                }
                // BKCommonLib end

                /*
                if ((axisalignedbb1 != null) && (axisalignedbb1.c(axisalignedbb))) {
                    list.add(axisalignedbb1);
                }

                axisalignedbb1 = entity.j(entity1);
                if ((axisalignedbb1 != null) && (axisalignedbb1.c(axisalignedbb))) {
                    list.add(axisalignedbb1);
                }
                */
            }
        }

        cubes.addAll(foundBounds);
        return true;
    }

    private void world_addEntityCubes(EntityHandle entity, AxisAlignedBBHandle axisalignedbb) {
        if (entity != null && this.entityCollisionEnabled) {
            List<EntityHandle> list = entity.getWorld().getNearbyEntities(entity, axisalignedbb.growUniform(0.25D));

            for (EntityHandle entity1 : list) {
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
                    // BKCommonLib end

                    /*
                    if ((axisalignedbb1 != null) && (axisalignedbb1.c(axisalignedbb))) {
                        list.add(axisalignedbb1);
                    }

                    axisalignedbb1 = entity.j(entity1);
                    if ((axisalignedbb1 != null) && (axisalignedbb1.c(axisalignedbb))) {
                        list.add(axisalignedbb1);
                    }
                    */
                }
            }
        }
    }

    private List<AxisAlignedBBHandle> world_getCubes(EntityHandle entity, double mx, double my, double mz) {
        AxisAlignedBBHandle axisalignedbb_old = entity.getBoundingBox();

        collisions_buffer.clear(); // BKCommonLib edit: use cached list

        // BKCommonLib start: replace world_getBlockCollisions call; use cached list instead + allow configurable bounds
        //world.a(entity, axisalignedbb, false, arraylist);
        AxisAlignedBBHandle entityBlockAABB = this.getBlockBoundingBox(entity);
        entity.setBoundingBoxField(entityBlockAABB);
        world_getBlockCollisions(entity, entityBlockAABB, entityBlockAABB.transformB(mx, my, mz));
        entity.setBoundingBoxField(axisalignedbb_old);
        // BKCommonLib end

        world_addEntityCubes(entity,  axisalignedbb_old.transformB(mx, my, mz));

        return collisions_buffer;
    }

    private boolean world_getBlockCollisions(EntityHandle entity, AxisAlignedBBHandle entityBounds, AxisAlignedBBHandle movedBounds) {
        // When disabled, return right away and don't add any bounding boxes
        if (!this.blockCollisionEnabled) {
            return true;
        }

        if (!world_getBlockCubes(entity, movedBounds, collisions_buffer)) {
            return false;
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
            if (movedBounds.getMaxY() > blockBounds.getMaxY()) {
                hitFace = BlockFace.UP;
            } else if (movedBounds.getMinY() < blockBounds.getMinY()) {
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

    private void removeFromList(List<AxisAlignedBBHandle> bounds, AxisAlignedBBHandle toRemove) {
        ListIterator<AxisAlignedBBHandle> iter = bounds.listIterator(bounds.size());
        while (iter.hasPrevious()) {
            AxisAlignedBBHandle b = iter.previous();
            if (b.getMinX() == toRemove.getMinX() && b.getMinY() == toRemove.getMinY() && b.getMinZ() == toRemove.getMinZ() &&
                    b.getMaxX() == toRemove.getMaxX() && b.getMaxY() == toRemove.getMaxY() && b.getMaxZ() == toRemove.getMaxZ())
            {
                iter.remove();
                return; // success
            }
        }
        if (!loggedEntityCollisionUndoFailure) {
            loggedEntityCollisionUndoFailure = true;
            Logging.LOGGER_DEBUG.severe("EntityMoveHandler failed to undo Entity Collision");
        }
    }
}
