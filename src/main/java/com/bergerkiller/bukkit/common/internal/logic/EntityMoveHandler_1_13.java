package com.bergerkiller.bukkit.common.internal.logic;

import java.util.List;

import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.VoxelShapeHandle;

/**
 * Logic for MC 1.13 and onwards
 */
public class EntityMoveHandler_1_13 extends EntityMoveHandler {

    @Override
    protected VoxelShapeHandle world_getCollisionShape(EntityHandle entity, double mx, double my, double mz) {
        // If all collision is disabled, simply return an empty shape
        if (!this.blockCollisionEnabled && !this.entityCollisionEnabled) {
            return VoxelShapeHandle.empty();
        }

        // MC 1.13: Combine voxel shapes from block collision and entity collision logic
        VoxelShapeHandle shape_blockCollisions = world_getBlockCollisionShape(entity, mx, my, mz);
        VoxelShapeHandle shape_entityCollisions = world_getEntityCollisionShape(entity, mx, my, mz);
        return VoxelShapeHandle.merge(shape_blockCollisions, shape_entityCollisions);
    }

    private VoxelShapeHandle world_getBlockCollisionShape(EntityHandle entity, double mx, double my, double mz) {
        if (!this.blockCollisionEnabled) {
            return VoxelShapeHandle.empty();
        }

        AxisAlignedBBHandle entityBounds = this.getBlockBoundingBox(entity);

        final double MIN_MOVE = 1.0E-7D;
        VoxelShapeHandle voxelshapeAABB = VoxelShapeHandle.fromAABB(entityBounds);
        VoxelShapeHandle voxelshapeAABBMoved = VoxelShapeHandle.fromAABB(entityBounds.translate(mx > 0.0D ? -MIN_MOVE : MIN_MOVE, my > 0.0D ? -MIN_MOVE : MIN_MOVE, mz > 0.0D ? -MIN_MOVE : MIN_MOVE));
        VoxelShapeHandle voxelshapeBounds = VoxelShapeHandle.mergeOnlyFirst(VoxelShapeHandle.fromAABB(entityBounds.transformB(mx, my, mz).growUniform(MIN_MOVE)), voxelshapeAABBMoved);

        if (voxelshapeBounds.isEmpty()) {
            return VoxelShapeHandle.empty();
        }

        /*
        boolean flag2 = world.i(entity);

        if (entity.bG() == flag2) {
            entity.n(!flag2);
        }

        return world.getBlockCollisions:a(voxelshapeBounds, voxelshapeAABB, false, flag2);
         */

        return VoxelShapeHandle.empty();
    }

    private VoxelShapeHandle world_getEntityCollisionShape(EntityHandle entity, double mx, double my, double mz) {
        if (!this.entityCollisionEnabled) {
            return VoxelShapeHandle.empty();
        }

        AxisAlignedBBHandle entityBounds = entity.getBoundingBox();

        final double MIN_MOVE = 1.0E-7D;
        VoxelShapeHandle voxelshapeAABBMoved = VoxelShapeHandle.fromAABB(entityBounds.translate(mx > 0.0D ? -MIN_MOVE : MIN_MOVE, my > 0.0D ? -MIN_MOVE : MIN_MOVE, mz > 0.0D ? -MIN_MOVE : MIN_MOVE));
        VoxelShapeHandle voxelshapeBounds = VoxelShapeHandle.mergeOnlyFirst(VoxelShapeHandle.fromAABB(entityBounds.transformB(mx, my, mz).growUniform(MIN_MOVE)), voxelshapeAABBMoved);

        if (voxelshapeBounds.isEmpty()) {
            return VoxelShapeHandle.empty();
        }

        // default VoxelShape IWorldReader::a(@Nullable Entity entity, VoxelShape voxelshape, boolean flag, Set<Entity> set)
        AxisAlignedBBHandle axisalignedbb = voxelshapeBounds.getBoundingBox();
        VoxelShapeHandle shape = VoxelShapeHandle.empty();

        if (entity != null && this.entityCollisionEnabled) {
            List<EntityHandle> list = entity.getWorld().getEntities(entity, axisalignedbb.growUniform(0.25D));

            for (int i = 0; i < list.size(); i++) {
                EntityHandle entity1 = list.get(i);

                if (!entity.isInSameVehicle(entity1)) {
                    // BKCommonLib start: block collision event handler
                    AxisAlignedBBHandle axisalignedbb1 = entity1.getOtherBoundingBox();
                    if (axisalignedbb1 != null && axisalignedbb1.bbTransformA(axisalignedbb)
                            && controller.onEntityCollision(entity1.getBukkitEntity())) {

                        shape = VoxelShapeHandle.merge(shape, VoxelShapeHandle.fromAABB(axisalignedbb1));
                    }

                    axisalignedbb1 = entity.getEntityBoundingBox(entity1);
                    if (axisalignedbb1 != null && axisalignedbb1.bbTransformA(axisalignedbb)
                            && controller.onEntityCollision(entity1.getBukkitEntity())) {

                        shape = VoxelShapeHandle.merge(shape, VoxelShapeHandle.fromAABB(axisalignedbb1));
                    }
                    // BKCommonLib end

                    /*
                    if (axisalignedbb1 != null && axisalignedbb1.c(axisalignedbb)) {
                        voxelshape1 = VoxelShapes.b(voxelshape1, VoxelShapes.a(axisalignedbb1), OperatorBoolean.OR);
                        if (flag) {
                            break;
                        }
                    }

                    axisalignedbb1 = entity.j(entity1);
                    if (axisalignedbb1 != null && axisalignedbb1.c(axisalignedbb)) {
                        voxelshape1 = VoxelShapes.b(voxelshape1, VoxelShapes.a(axisalignedbb1), OperatorBoolean.OR);
                        if (flag) {
                            break;
                        }
                    }
                    */
                }
            }
        }

        return shape;
    }
}
