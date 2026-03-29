package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.phys.AABB</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.phys.AABB")
public abstract class AABBHandle extends Template.Handle {
    /** @see AABBClass */
    public static final AABBClass T = Template.Class.create(AABBClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static AABBHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final AABBHandle createNew(double x1, double y1, double z1, double x2, double y2, double z2) {
        return T.constr_x1_y1_z1_x2_y2_z2.newInstanceVA(x1, y1, z1, x2, y2, z2);
    }

    /* ============================================================================== */

    public abstract AABBHandle grow(double sx, double sy, double sz);
    public abstract AABBHandle transformB(double lx, double ly, double lz);
    public abstract AABBHandle translate(double dx, double dy, double dz);
    public abstract boolean bbTransformA(AABBHandle paramAABB);
    public abstract double calcSomeX(AABBHandle axisalignedbb, double d0);
    public abstract double calcSomeY(AABBHandle axisalignedbb, double d0);
    public abstract double calcSomeZ(AABBHandle axisalignedbb, double d0);
    public AABBHandle growUniform(double size) {
        return grow(size, size, size);
    }

    public AABBHandle shrinkUniform(double size) {
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
     * Stores class members for <b>net.minecraft.world.phys.AABB</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AABBClass extends Template.Class<AABBHandle> {
        public final Template.Constructor.Converted<AABBHandle> constr_x1_y1_z1_x2_y2_z2 = new Template.Constructor.Converted<AABBHandle>();

        public final Template.Field.Double minX = new Template.Field.Double();
        public final Template.Field.Double minY = new Template.Field.Double();
        public final Template.Field.Double minZ = new Template.Field.Double();
        public final Template.Field.Double maxX = new Template.Field.Double();
        public final Template.Field.Double maxY = new Template.Field.Double();
        public final Template.Field.Double maxZ = new Template.Field.Double();

        public final Template.Method.Converted<AABBHandle> grow = new Template.Method.Converted<AABBHandle>();
        public final Template.Method.Converted<AABBHandle> transformB = new Template.Method.Converted<AABBHandle>();
        public final Template.Method.Converted<AABBHandle> translate = new Template.Method.Converted<AABBHandle>();
        public final Template.Method.Converted<Boolean> bbTransformA = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Double> calcSomeX = new Template.Method.Converted<Double>();
        public final Template.Method.Converted<Double> calcSomeY = new Template.Method.Converted<Double>();
        public final Template.Method.Converted<Double> calcSomeZ = new Template.Method.Converted<Double>();

    }

}

