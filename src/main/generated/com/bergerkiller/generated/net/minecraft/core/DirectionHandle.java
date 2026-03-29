package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.Direction</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.Direction")
public abstract class DirectionHandle extends Template.Handle {
    /** @see DirectionClass */
    public static final DirectionClass T = Template.Class.create(DirectionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final DirectionHandle DOWN = T.DOWN.getSafe();
    public static final DirectionHandle UP = T.UP.getSafe();
    public static final DirectionHandle NORTH = T.NORTH.getSafe();
    public static final DirectionHandle SOUTH = T.SOUTH.getSafe();
    public static final DirectionHandle WEST = T.WEST.getSafe();
    public static final DirectionHandle EAST = T.EAST.getSafe();
    /* ============================================================================== */

    public static DirectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.core.Direction</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DirectionClass extends Template.Class<DirectionHandle> {
        public final Template.EnumConstant.Converted<DirectionHandle> DOWN = new Template.EnumConstant.Converted<DirectionHandle>();
        public final Template.EnumConstant.Converted<DirectionHandle> UP = new Template.EnumConstant.Converted<DirectionHandle>();
        public final Template.EnumConstant.Converted<DirectionHandle> NORTH = new Template.EnumConstant.Converted<DirectionHandle>();
        public final Template.EnumConstant.Converted<DirectionHandle> SOUTH = new Template.EnumConstant.Converted<DirectionHandle>();
        public final Template.EnumConstant.Converted<DirectionHandle> WEST = new Template.EnumConstant.Converted<DirectionHandle>();
        public final Template.EnumConstant.Converted<DirectionHandle> EAST = new Template.EnumConstant.Converted<DirectionHandle>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.core.Direction.Axis</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.core.Direction.Axis")
    public abstract static class AxisHandle extends Template.Handle {
        /** @see AxisClass */
        public static final AxisClass T = Template.Class.create(AxisClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final AxisHandle X = T.X.getSafe();
        public static final AxisHandle Y = T.Y.getSafe();
        public static final AxisHandle Z = T.Z.getSafe();
        /* ============================================================================== */

        public static AxisHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public int ordinal() {
            return ((Enum<?>) getRaw()).ordinal();
        }
        /**
         * Stores class members for <b>net.minecraft.core.Direction.Axis</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class AxisClass extends Template.Class<AxisHandle> {
            public final Template.EnumConstant.Converted<AxisHandle> X = new Template.EnumConstant.Converted<AxisHandle>();
            public final Template.EnumConstant.Converted<AxisHandle> Y = new Template.EnumConstant.Converted<AxisHandle>();
            public final Template.EnumConstant.Converted<AxisHandle> Z = new Template.EnumConstant.Converted<AxisHandle>();

        }

    }

}

