package com.bergerkiller.generated.org.bukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>org.bukkit.inventory.MainHand</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("org.bukkit.inventory.MainHand")
public abstract class MainHandHandle extends Template.Handle {
    /** @see MainHandClass */
    public static final MainHandClass T = Template.Class.create(MainHandClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final MainHandHandle LEFT = T.LEFT.getSafe();
    public static final MainHandHandle RIGHT = T.RIGHT.getSafe();
    /* ============================================================================== */

    public static MainHandHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.inventory.MainHand</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MainHandClass extends Template.Class<MainHandHandle> {
        public final Template.EnumConstant.Converted<MainHandHandle> LEFT = new Template.EnumConstant.Converted<MainHandHandle>();
        public final Template.EnumConstant.Converted<MainHandHandle> RIGHT = new Template.EnumConstant.Converted<MainHandHandle>();

    }

}

