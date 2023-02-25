package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.EnumMainHand</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.EnumMainHand")
public abstract class EnumMainHandHandle extends Template.Handle {
    /** @see EnumMainHandClass */
    public static final EnumMainHandClass T = Template.Class.create(EnumMainHandClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final EnumMainHandHandle LEFT = T.LEFT.getSafe();
    public static final EnumMainHandHandle RIGHT = T.RIGHT.getSafe();
    /* ============================================================================== */

    public static EnumMainHandHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.EnumMainHand</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumMainHandClass extends Template.Class<EnumMainHandHandle> {
        public final Template.EnumConstant.Converted<EnumMainHandHandle> LEFT = new Template.EnumConstant.Converted<EnumMainHandHandle>();
        public final Template.EnumConstant.Converted<EnumMainHandHandle> RIGHT = new Template.EnumConstant.Converted<EnumMainHandHandle>();

    }

}

