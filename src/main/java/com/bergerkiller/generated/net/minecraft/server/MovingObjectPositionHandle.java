package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.util.Vector;

public class MovingObjectPositionHandle extends Template.Handle {
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

    public static final class MovingObjectPositionClass extends Template.Class<MovingObjectPositionHandle> {
        public final Template.Field.Converted<Vector> pos = new Template.Field.Converted<Vector>();

    }
}
