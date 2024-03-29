package com.bergerkiller.generated.net.minecraft.world.phys.shapes;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.core.EnumDirectionHandle.EnumAxisHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AxisAlignedBBHandle;
import java.util.stream.Stream;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.phys.shapes.VoxelShape</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.phys.shapes.VoxelShape")
public abstract class VoxelShapeHandle extends Template.Handle {
    /** @see VoxelShapeClass */
    public static final VoxelShapeClass T = Template.Class.create(VoxelShapeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static VoxelShapeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static VoxelShapeHandle empty() {
        return T.empty.invoke();
    }

    public static Object createRawFromAABB(Object handles) {
        return T.createRawFromAABB.invoke(handles);
    }

    public static VoxelShapeHandle fromAABB(AxisAlignedBBHandle aabb) {
        return T.fromAABB.invoke(aabb);
    }

    public static VoxelShapeHandle mergeOnlyFirst(VoxelShapeHandle a, VoxelShapeHandle b) {
        return T.mergeOnlyFirst.invoke(a, b);
    }

    public static VoxelShapeHandle merge(VoxelShapeHandle a, VoxelShapeHandle b) {
        return T.merge.invoke(a, b);
    }

    public static double traceAxis(EnumAxisHandle axis, AxisAlignedBBHandle boundingBox, Stream<VoxelShapeHandle> voxels, double coordinate) {
        return T.traceAxis.invoke(axis, boundingBox, voxels, coordinate);
    }

    public abstract AxisAlignedBBHandle getBoundingBox();
    public abstract boolean isEmpty();
    /**
     * Stores class members for <b>net.minecraft.world.phys.shapes.VoxelShape</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class VoxelShapeClass extends Template.Class<VoxelShapeHandle> {
        public final Template.StaticMethod.Converted<VoxelShapeHandle> empty = new Template.StaticMethod.Converted<VoxelShapeHandle>();
        public final Template.StaticMethod.Converted<Object> createRawFromAABB = new Template.StaticMethod.Converted<Object>();
        public final Template.StaticMethod.Converted<VoxelShapeHandle> fromAABB = new Template.StaticMethod.Converted<VoxelShapeHandle>();
        public final Template.StaticMethod.Converted<VoxelShapeHandle> mergeOnlyFirst = new Template.StaticMethod.Converted<VoxelShapeHandle>();
        public final Template.StaticMethod.Converted<VoxelShapeHandle> merge = new Template.StaticMethod.Converted<VoxelShapeHandle>();
        public final Template.StaticMethod.Converted<Double> traceAxis = new Template.StaticMethod.Converted<Double>();

        public final Template.Method.Converted<AxisAlignedBBHandle> getBoundingBox = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();

    }

}

