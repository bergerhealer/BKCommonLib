package com.bergerkiller.generated.net.minecraft.world;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.InteractionHand</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.InteractionHand")
public abstract class InteractionHandHandle extends Template.Handle {
    /** @see InteractionHandClass */
    public static final InteractionHandClass T = Template.Class.create(InteractionHandClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final InteractionHandHandle MAIN_HAND = T.MAIN_HAND.getSafe();
    public static final InteractionHandHandle OFF_HAND = T.OFF_HAND.getSafe();
    /* ============================================================================== */

    public static InteractionHandHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.InteractionHand</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class InteractionHandClass extends Template.Class<InteractionHandHandle> {
        public final Template.EnumConstant.Converted<InteractionHandHandle> MAIN_HAND = new Template.EnumConstant.Converted<InteractionHandHandle>();
        public final Template.EnumConstant.Converted<InteractionHandHandle> OFF_HAND = new Template.EnumConstant.Converted<InteractionHandHandle>();

    }

}

