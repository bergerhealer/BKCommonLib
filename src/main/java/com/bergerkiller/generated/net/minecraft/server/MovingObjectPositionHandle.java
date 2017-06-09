package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MovingObjectPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class MovingObjectPositionHandle extends Template.Handle {
    /** @See {@link MovingObjectPositionClass} */
    public static final MovingObjectPositionClass T = new MovingObjectPositionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MovingObjectPositionHandle.class, "net.minecraft.server.MovingObjectPosition");

    /* ============================================================================== */

    public static MovingObjectPositionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        MovingObjectPositionHandle handle = new MovingObjectPositionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Vector getPos() {
        return T.pos.get(instance);
    }

    public void setPos(Vector value) {
        T.pos.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.MovingObjectPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MovingObjectPositionClass extends Template.Class<MovingObjectPositionHandle> {
        public final Template.Field.Converted<Vector> pos = new Template.Field.Converted<Vector>();

    }

}

