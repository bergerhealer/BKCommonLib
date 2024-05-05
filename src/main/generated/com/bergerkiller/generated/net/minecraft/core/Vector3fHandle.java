package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.Vector3f</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.Vector3f")
public abstract class Vector3fHandle extends Template.Handle {
    /** @see Vector3fClass */
    public static final Vector3fClass T = Template.Class.create(Vector3fClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static Vector3fHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final Vector3fHandle createNew(float x, float y, float z) {
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
    public static Vector3fHandle fromBukkit(org.bukkit.util.Vector vector) {
        return createHandle(fromBukkitRaw(vector));
    }
    /**
     * Stores class members for <b>net.minecraft.core.Vector3f</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class Vector3fClass extends Template.Class<Vector3fHandle> {
        public final Template.Constructor.Converted<Vector3fHandle> constr_x_y_z = new Template.Constructor.Converted<Vector3fHandle>();

        public final Template.StaticMethod<Object> fromBukkitRaw = new Template.StaticMethod<Object>();

        public final Template.Method<Float> getX = new Template.Method<Float>();
        public final Template.Method<Float> getY = new Template.Method<Float>();
        public final Template.Method<Float> getZ = new Template.Method<Float>();
        public final Template.Method<Vector> toBukkit = new Template.Method<Vector>();

    }

}

