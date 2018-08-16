package com.bergerkiller.bukkit.common.internal.logic;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.VoxelShapeHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Logic for MC 1.13 and onwards
 */
public class EntityMoveHandler_1_13 extends EntityMoveHandler {
    private static FastMethod<Object> getBlockCollisions_method = new FastMethod<Object>();
    private static final boolean getBlockCollisions_method_init;

    static {
        boolean success = true;
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(CommonUtil.getNMSClass("World"));
        try {
            String method_path = "/com/bergerkiller/bukkit/common/internal/logic/EntityMoveHandler_1_13_getBlockCollisions.txt";
            try (InputStream input = EntityMoveHandler_1_13.class.getResourceAsStream(method_path)) {
                try (Scanner scanner = new Scanner(input, "UTF-8")) {
                    scanner.useDelimiter("\\A");
                    String method_body = scanner.next();
                    method_body = SourceDeclaration.preprocess(method_body);
                    method_body = method_body.replaceAll("this", "instance");
                    method_body = method_body.replaceAll("BlockPosition\\.b", "BlockPosition\\$b");
                    method_body = method_body.trim();
                    getBlockCollisions_method.init(new MethodDeclaration(resolver, method_body));
                    getBlockCollisions_method.forceInitialization();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            success = false;
        }
        getBlockCollisions_method_init = success;
    }

    // For under test
    public static boolean isBlockCollisionsMethodInitialized() {
        return getBlockCollisions_method_init;
    }

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

    // Called from getBlockCollisions_method
    public boolean onBlockCollided(Block block) {
        // Find out what direction the block is hit
        BlockFace hitFace;
        AxisAlignedBBHandle entityBounds = this.that.getBoundingBox();
        if (entityBounds.getMaxY() > (block.getY() + 1.0)) {
            hitFace = BlockFace.UP;
        } else if (entityBounds.getMinY() < (double) block.getY()) {
            hitFace = BlockFace.DOWN;
        } else {
            double dx = this.that.getLocX() - block.getX() - 0.5;
            double dz = this.that.getLocZ() - block.getZ() - 0.5;
            hitFace = FaceUtil.getDirection(dx, dz, false);
        }
        // Block collision event
        return controller.onBlockCollision(block, hitFace);
    }

    private VoxelShapeHandle world_getBlockCollisionShape(EntityHandle entity, double mx, double my, double mz) {
        if (!this.blockCollisionEnabled || !isBlockCollisionsMethodInitialized()) {
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

        // Check and update that the entity is within the world border
        WorldHandle world = entity.getWorld();
        boolean inWorldBorder = world.isWithinWorldBorder(entity);
        if (inWorldBorder == entity.isOutsideWorldBorder()) {
            entity.setOutsideWorldBorder(!inWorldBorder);
        }

        return VoxelShapeHandle.createHandle(getBlockCollisions_method.invoke(world.getRaw(), this, voxelshapeBounds.getRaw(), voxelshapeAABB.getRaw(), false, inWorldBorder));
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
