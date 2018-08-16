package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle.EnumAxisHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.VoxelShape</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class VoxelShapeHandle extends Template.Handle {
    /** @See {@link VoxelShapeClass} */
    public static final VoxelShapeClass T = new VoxelShapeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(VoxelShapeHandle.class, "net.minecraft.server.VoxelShape");

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

    public abstract double traceAxis(EnumAxisHandle axis, AxisAlignedBBHandle boundingBox, double coordinate);
    public abstract boolean isEmpty();
    /**
     * Stores class members for <b>net.minecraft.server.VoxelShape</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class VoxelShapeClass extends Template.Class<VoxelShapeHandle> {
        public final Template.StaticMethod.Converted<VoxelShapeHandle> empty = new Template.StaticMethod.Converted<VoxelShapeHandle>();
        public final Template.StaticMethod.Converted<Object> createRawFromAABB = new Template.StaticMethod.Converted<Object>();

        public final Template.Method.Converted<Double> traceAxis = new Template.Method.Converted<Double>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();

    }

}

