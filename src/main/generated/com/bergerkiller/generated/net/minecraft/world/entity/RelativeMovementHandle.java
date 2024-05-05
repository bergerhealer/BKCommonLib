package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.RelativeMovement</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.RelativeMovement")
public abstract class RelativeMovementHandle extends Template.Handle {
    /** @see RelativeMovementClass */
    public static final RelativeMovementClass T = Template.Class.create(RelativeMovementClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final RelativeMovementHandle X = T.X.getSafe();
    public static final RelativeMovementHandle Y = T.Y.getSafe();
    public static final RelativeMovementHandle Z = T.Z.getSafe();
    public static final RelativeMovementHandle Y_ROT = T.Y_ROT.getSafe();
    public static final RelativeMovementHandle X_ROT = T.X_ROT.getSafe();
    /* ============================================================================== */

    public static RelativeMovementHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static java.util.Set<?> allAbsolute() {
        return java.util.EnumSet.noneOf((Class) T.getType());
    }

    public static java.util.Set<?> allRelative() {
        return java.util.EnumSet.allOf((Class) T.getType());
    }
    /**
     * Stores class members for <b>net.minecraft.world.entity.RelativeMovement</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RelativeMovementClass extends Template.Class<RelativeMovementHandle> {
        public final Template.EnumConstant.Converted<RelativeMovementHandle> X = new Template.EnumConstant.Converted<RelativeMovementHandle>();
        public final Template.EnumConstant.Converted<RelativeMovementHandle> Y = new Template.EnumConstant.Converted<RelativeMovementHandle>();
        public final Template.EnumConstant.Converted<RelativeMovementHandle> Z = new Template.EnumConstant.Converted<RelativeMovementHandle>();
        public final Template.EnumConstant.Converted<RelativeMovementHandle> Y_ROT = new Template.EnumConstant.Converted<RelativeMovementHandle>();
        public final Template.EnumConstant.Converted<RelativeMovementHandle> X_ROT = new Template.EnumConstant.Converted<RelativeMovementHandle>();

    }

}

