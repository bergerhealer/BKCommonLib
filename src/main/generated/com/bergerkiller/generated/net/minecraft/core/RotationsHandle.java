package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.Rotations</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.Rotations")
public abstract class RotationsHandle extends Template.Handle {
    /** @see RotationsClass */
    public static final RotationsClass T = Template.Class.create(RotationsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RotationsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final RotationsHandle createNew(float x, float y, float z) {
        return T.constr_x_y_z.newInstance(x, y, z);
    }

    /* ============================================================================== */

    public static Object fromBukkitRaw(Vector vector) {
        return T.fromBukkitRaw.invoker.invoke(null,vector);
    }

    public abstract float getX();
    public abstract float getY();
    public abstract float getZ();
    public abstract Vector toBukkit();
    public static RotationsHandle fromBukkit(org.bukkit.util.Vector vector) {
        return createHandle(fromBukkitRaw(vector));
    }
    /**
     * Stores class members for <b>net.minecraft.core.Rotations</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RotationsClass extends Template.Class<RotationsHandle> {
        public final Template.Constructor.Converted<RotationsHandle> constr_x_y_z = new Template.Constructor.Converted<RotationsHandle>();

        public final Template.StaticMethod<Object> fromBukkitRaw = new Template.StaticMethod<Object>();

        public final Template.Method<Float> getX = new Template.Method<Float>();
        public final Template.Method<Float> getY = new Template.Method<Float>();
        public final Template.Method<Float> getZ = new Template.Method<Float>();
        public final Template.Method<Vector> toBukkit = new Template.Method<Vector>();

    }

}

