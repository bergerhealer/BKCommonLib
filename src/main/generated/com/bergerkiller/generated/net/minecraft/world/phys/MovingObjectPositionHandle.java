package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.phys.MovingObjectPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.phys.MovingObjectPosition")
public abstract class MovingObjectPositionHandle extends Template.Handle {
    /** @see MovingObjectPositionClass */
    public static final MovingObjectPositionClass T = Template.Class.create(MovingObjectPositionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MovingObjectPositionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockFace getDirection();
    @Template.Readonly
    public abstract Vector getPos();
    /**
     * Stores class members for <b>net.minecraft.world.phys.MovingObjectPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MovingObjectPositionClass extends Template.Class<MovingObjectPositionHandle> {
        @Template.Readonly
        public final Template.Field.Converted<Vector> pos = new Template.Field.Converted<Vector>();

        public final Template.Method.Converted<BlockFace> getDirection = new Template.Method.Converted<BlockFace>();

    }

}

