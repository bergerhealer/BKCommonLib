package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MovingObjectPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class MovingObjectPositionHandle extends Template.Handle {
    /** @See {@link MovingObjectPositionClass} */
    public static final MovingObjectPositionClass T = new MovingObjectPositionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MovingObjectPositionHandle.class, "net.minecraft.server.MovingObjectPosition");

    /* ============================================================================== */

    public static MovingObjectPositionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockFace getDirection();
    public abstract void setDirection(BlockFace value);
    public abstract Vector getPos();
    public abstract void setPos(Vector value);
    /**
     * Stores class members for <b>net.minecraft.server.MovingObjectPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MovingObjectPositionClass extends Template.Class<MovingObjectPositionHandle> {
        public final Template.Field.Converted<BlockFace> direction = new Template.Field.Converted<BlockFace>();
        public final Template.Field.Converted<Vector> pos = new Template.Field.Converted<Vector>();

    }

}

