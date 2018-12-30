package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.AxisAlignedBB</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class AxisAlignedBBHandle extends Template.Handle {
    /** @See {@link AxisAlignedBBClass} */
    public static final AxisAlignedBBClass T = new AxisAlignedBBClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(AxisAlignedBBHandle.class, "net.minecraft.server.AxisAlignedBB", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static AxisAlignedBBHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final AxisAlignedBBHandle createNew(double x1, double y1, double z1, double x2, double y2, double z2) {
        return T.constr_x1_y1_z1_x2_y2_z2.newInstanceVA(x1, y1, z1, x2, y2, z2);
    }

    /* ============================================================================== */

    public abstract AxisAlignedBBHandle grow(double sx, double sy, double sz);
    public abstract AxisAlignedBBHandle transformB(double lx, double ly, double lz);
    public abstract AxisAlignedBBHandle translate(double dx, double dy, double dz);
    public abstract boolean bbTransformA(AxisAlignedBBHandle paramAxisAlignedBB);
    public abstract double calcSomeX(AxisAlignedBBHandle axisalignedbb, double d0);
    public abstract double calcSomeY(AxisAlignedBBHandle axisalignedbb, double d0);
    public abstract double calcSomeZ(AxisAlignedBBHandle axisalignedbb, double d0);

    public AxisAlignedBBHandle growUniform(double size) {
        return grow(size, size, size);
    }

    public AxisAlignedBBHandle shrinkUniform(double size) {
        return growUniform(-size);
    }
    public abstract double getMinX();
    public abstract void setMinX(double value);
    public abstract double getMinY();
    public abstract void setMinY(double value);
    public abstract double getMinZ();
    public abstract void setMinZ(double value);
    public abstract double getMaxX();
    public abstract void setMaxX(double value);
    public abstract double getMaxY();
    public abstract void setMaxY(double value);
    public abstract double getMaxZ();
    public abstract void setMaxZ(double value);
    /**
     * Stores class members for <b>net.minecraft.server.AxisAlignedBB</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AxisAlignedBBClass extends Template.Class<AxisAlignedBBHandle> {
        public final Template.Constructor.Converted<AxisAlignedBBHandle> constr_x1_y1_z1_x2_y2_z2 = new Template.Constructor.Converted<AxisAlignedBBHandle>();

        public final Template.Field.Double minX = new Template.Field.Double();
        public final Template.Field.Double minY = new Template.Field.Double();
        public final Template.Field.Double minZ = new Template.Field.Double();
        public final Template.Field.Double maxX = new Template.Field.Double();
        public final Template.Field.Double maxY = new Template.Field.Double();
        public final Template.Field.Double maxZ = new Template.Field.Double();

        public final Template.Method.Converted<AxisAlignedBBHandle> grow = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method.Converted<AxisAlignedBBHandle> transformB = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method.Converted<AxisAlignedBBHandle> translate = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method.Converted<Boolean> bbTransformA = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Double> calcSomeX = new Template.Method.Converted<Double>();
        public final Template.Method.Converted<Double> calcSomeY = new Template.Method.Converted<Double>();
        public final Template.Method.Converted<Double> calcSomeZ = new Template.Method.Converted<Double>();

    }

}

