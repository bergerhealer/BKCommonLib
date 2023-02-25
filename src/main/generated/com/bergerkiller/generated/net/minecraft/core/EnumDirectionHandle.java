package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.EnumDirection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.EnumDirection")
public abstract class EnumDirectionHandle extends Template.Handle {
    /** @see EnumDirectionClass */
    public static final EnumDirectionClass T = Template.Class.create(EnumDirectionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final EnumDirectionHandle DOWN = T.DOWN.getSafe();
    public static final EnumDirectionHandle UP = T.UP.getSafe();
    public static final EnumDirectionHandle NORTH = T.NORTH.getSafe();
    public static final EnumDirectionHandle SOUTH = T.SOUTH.getSafe();
    public static final EnumDirectionHandle WEST = T.WEST.getSafe();
    public static final EnumDirectionHandle EAST = T.EAST.getSafe();
    /* ============================================================================== */

    public static EnumDirectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.core.EnumDirection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumDirectionClass extends Template.Class<EnumDirectionHandle> {
        public final Template.EnumConstant.Converted<EnumDirectionHandle> DOWN = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> UP = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> NORTH = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> SOUTH = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> WEST = new Template.EnumConstant.Converted<EnumDirectionHandle>();
        public final Template.EnumConstant.Converted<EnumDirectionHandle> EAST = new Template.EnumConstant.Converted<EnumDirectionHandle>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.core.EnumDirection.EnumAxis</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.core.EnumDirection.EnumAxis")
    public abstract static class EnumAxisHandle extends Template.Handle {
        /** @see EnumAxisClass */
        public static final EnumAxisClass T = Template.Class.create(EnumAxisClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final EnumAxisHandle X = T.X.getSafe();
        public static final EnumAxisHandle Y = T.Y.getSafe();
        public static final EnumAxisHandle Z = T.Z.getSafe();
        /* ============================================================================== */

        public static EnumAxisHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */


        public int ordinal() {
            return ((Enum<?>) getRaw()).ordinal();
        }
        /**
         * Stores class members for <b>net.minecraft.core.EnumDirection.EnumAxis</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumAxisClass extends Template.Class<EnumAxisHandle> {
            public final Template.EnumConstant.Converted<EnumAxisHandle> X = new Template.EnumConstant.Converted<EnumAxisHandle>();
            public final Template.EnumConstant.Converted<EnumAxisHandle> Y = new Template.EnumConstant.Converted<EnumAxisHandle>();
            public final Template.EnumConstant.Converted<EnumAxisHandle> Z = new Template.EnumConstant.Converted<EnumAxisHandle>();

        }

    }

}

