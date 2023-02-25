package com.bergerkiller.generated.net.minecraft.world;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.EnumHand</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.EnumHand")
public abstract class EnumHandHandle extends Template.Handle {
    /** @see EnumHandClass */
    public static final EnumHandClass T = Template.Class.create(EnumHandClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final EnumHandHandle MAIN_HAND = T.MAIN_HAND.getSafe();
    public static final EnumHandHandle OFF_HAND = T.OFF_HAND.getSafe();
    /* ============================================================================== */

    public static EnumHandHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.EnumHand</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumHandClass extends Template.Class<EnumHandHandle> {
        public final Template.EnumConstant.Converted<EnumHandHandle> MAIN_HAND = new Template.EnumConstant.Converted<EnumHandHandle>();
        public final Template.EnumConstant.Converted<EnumHandHandle> OFF_HAND = new Template.EnumConstant.Converted<EnumHandHandle>();

    }

}

