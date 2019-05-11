package com.bergerkiller.bukkit.common.internal.logic;

import java.util.stream.Stream;

import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.VoxelShapeHandle;

public class EntityMoveHandler_1_14 extends EntityMoveHandler {

    @Override
    protected Stream<VoxelShapeHandle> world_getCollisionShapes(EntityHandle entity, double dx, double dy, double dz) {
        return Stream.empty();
    }

}
