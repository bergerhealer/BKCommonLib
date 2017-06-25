package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.AxisAlignedBB</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class AxisAlignedBBHandle extends Template.Handle {
    /** @See {@link AxisAlignedBBClass} */
    public static final AxisAlignedBBClass T = new AxisAlignedBBClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(AxisAlignedBBHandle.class, "net.minecraft.server.AxisAlignedBB");

    /* ============================================================================== */

    public static AxisAlignedBBHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        AxisAlignedBBHandle handle = new AxisAlignedBBHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final AxisAlignedBBHandle createNew(double x1, double y1, double z1, double x2, double y2, double z2) {
        return T.constr_x1_y1_z1_x2_y2_z2.newInstance(x1, y1, z1, x2, y2, z2);
    }

    /* ============================================================================== */

    public AxisAlignedBBHandle grow(double sx, double sy, double sz) {
        return T.grow.invoke(instance, sx, sy, sz);
    }

    public AxisAlignedBBHandle transformB(double lx, double ly, double lz) {
        return T.transformB.invoke(instance, lx, ly, lz);
    }

    public AxisAlignedBBHandle translate(double dx, double dy, double dz) {
        return T.translate.invoke(instance, dx, dy, dz);
    }

    public boolean bbTransformA(AxisAlignedBBHandle paramAxisAlignedBB) {
        return T.bbTransformA.invoke(instance, paramAxisAlignedBB);
    }

    public double calcSomeX(AxisAlignedBBHandle paramAxisAlignedBB, double paramDouble) {
        return T.calcSomeX.invoke(instance, paramAxisAlignedBB, paramDouble);
    }

    public double calcSomeY(AxisAlignedBBHandle paramAxisAlignedBB, double paramDouble) {
        return T.calcSomeY.invoke(instance, paramAxisAlignedBB, paramDouble);
    }

    public double calcSomeZ(AxisAlignedBBHandle paramAxisAlignedBB, double paramDouble) {
        return T.calcSomeZ.invoke(instance, paramAxisAlignedBB, paramDouble);
    }


    public AxisAlignedBBHandle growUniform(double size) {
        return grow(size, size, size);
    }

    public AxisAlignedBBHandle shrinkUniform(double size) {
        return growUniform(-size);
    }
    public double getMinX() {
        return T.minX.getDouble(instance);
    }

    public void setMinX(double value) {
        T.minX.setDouble(instance, value);
    }

    public double getMinY() {
        return T.minY.getDouble(instance);
    }

    public void setMinY(double value) {
        T.minY.setDouble(instance, value);
    }

    public double getMinZ() {
        return T.minZ.getDouble(instance);
    }

    public void setMinZ(double value) {
        T.minZ.setDouble(instance, value);
    }

    public double getMaxX() {
        return T.maxX.getDouble(instance);
    }

    public void setMaxX(double value) {
        T.maxX.setDouble(instance, value);
    }

    public double getMaxY() {
        return T.maxY.getDouble(instance);
    }

    public void setMaxY(double value) {
        T.maxY.setDouble(instance, value);
    }

    public double getMaxZ() {
        return T.maxZ.getDouble(instance);
    }

    public void setMaxZ(double value) {
        T.maxZ.setDouble(instance, value);
    }

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

