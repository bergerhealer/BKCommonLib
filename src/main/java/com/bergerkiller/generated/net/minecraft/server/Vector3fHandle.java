package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Vector3f</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class Vector3fHandle extends Template.Handle {
    /** @See {@link Vector3fClass} */
    public static final Vector3fClass T = new Vector3fClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(Vector3fHandle.class, "net.minecraft.server.Vector3f", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static Vector3fHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final Vector3fHandle createNew(float x, float y, float z) {
        return T.constr_x_y_z.newInstance(x, y, z);
    }

    /* ============================================================================== */

    public abstract float getX();
    public abstract float getY();
    public abstract float getZ();
    /**
     * Stores class members for <b>net.minecraft.server.Vector3f</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class Vector3fClass extends Template.Class<Vector3fHandle> {
        public final Template.Constructor.Converted<Vector3fHandle> constr_x_y_z = new Template.Constructor.Converted<Vector3fHandle>();

        public final Template.Method<Float> getX = new Template.Method<Float>();
        public final Template.Method<Float> getY = new Template.Method<Float>();
        public final Template.Method<Float> getZ = new Template.Method<Float>();

    }

}

