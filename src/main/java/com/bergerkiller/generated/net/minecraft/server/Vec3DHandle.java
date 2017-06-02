package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class Vec3DHandle extends Template.Handle {
    public static final Vec3DClass T = new Vec3DClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(Vec3DHandle.class, "net.minecraft.server.Vec3D");

    /* ============================================================================== */

    public static Vec3DHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        Vec3DHandle handle = new Vec3DHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final Vec3DHandle createNew(double x, double y, double z) {
        return T.constr_x_y_z.newInstance(x, y, z);
    }

    /* ============================================================================== */

    public double getX() {
        return T.x.getDouble(instance);
    }

    public void setX(double value) {
        T.x.setDouble(instance, value);
    }

    public double getY() {
        return T.y.getDouble(instance);
    }

    public void setY(double value) {
        T.y.setDouble(instance, value);
    }

    public double getZ() {
        return T.z.getDouble(instance);
    }

    public void setZ(double value) {
        T.z.setDouble(instance, value);
    }

    public static final class Vec3DClass extends Template.Class<Vec3DHandle> {
        public final Template.Constructor.Converted<Vec3DHandle> constr_x_y_z = new Template.Constructor.Converted<Vec3DHandle>();

        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();

    }

}

