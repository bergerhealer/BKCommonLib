package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.phys.Vec3</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.phys.Vec3")
public abstract class Vec3Handle extends Template.Handle {
    /** @see Vec3Class */
    public static final Vec3Class T = Template.Class.create(Vec3Class.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static Vec3Handle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final Vec3Handle createNew(double x, double y, double z) {
        return T.constr_x_y_z.newInstance(x, y, z);
    }

    /* ============================================================================== */

    public static Object fromBukkitRaw(Vector vector) {
        return T.fromBukkitRaw.invoker.invoke(null,vector);
    }

    public abstract Vector toBukkit();
    public static Vec3Handle fromBukkit(org.bukkit.util.Vector vector) {
        return createHandle(fromBukkitRaw(vector));
    }
    public abstract double getX();
    public abstract void setX(double value);
    public abstract double getY();
    public abstract void setY(double value);
    public abstract double getZ();
    public abstract void setZ(double value);
    /**
     * Stores class members for <b>net.minecraft.world.phys.Vec3</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class Vec3Class extends Template.Class<Vec3Handle> {
        public final Template.Constructor.Converted<Vec3Handle> constr_x_y_z = new Template.Constructor.Converted<Vec3Handle>();

        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();

        public final Template.StaticMethod<Object> fromBukkitRaw = new Template.StaticMethod<Object>();

        public final Template.Method<Vector> toBukkit = new Template.Method<Vector>();

    }

}

