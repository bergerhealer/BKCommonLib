package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.phys.Vec3D</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.phys.Vec3D")
public abstract class Vec3DHandle extends Template.Handle {
    /** @See {@link Vec3DClass} */
    public static final Vec3DClass T = Template.Class.create(Vec3DClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static Vec3DHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final Vec3DHandle createNew(double x, double y, double z) {
        return T.constr_x_y_z.newInstance(x, y, z);
    }

    /* ============================================================================== */

    public static Object fromBukkitRaw(Vector vector) {
        return T.fromBukkitRaw.invoker.invoke(null,vector);
    }

    public abstract Vector toBukkit();

    public static Vec3DHandle fromBukkit(org.bukkit.util.Vector vector) {
        return createHandle(fromBukkitRaw(vector));
    }
    public abstract double getX();
    public abstract void setX(double value);
    public abstract double getY();
    public abstract void setY(double value);
    public abstract double getZ();
    public abstract void setZ(double value);
    /**
     * Stores class members for <b>net.minecraft.world.phys.Vec3D</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class Vec3DClass extends Template.Class<Vec3DHandle> {
        public final Template.Constructor.Converted<Vec3DHandle> constr_x_y_z = new Template.Constructor.Converted<Vec3DHandle>();

        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();

        public final Template.StaticMethod<Object> fromBukkitRaw = new Template.StaticMethod<Object>();

        public final Template.Method<Vector> toBukkit = new Template.Method<Vector>();

    }

}

