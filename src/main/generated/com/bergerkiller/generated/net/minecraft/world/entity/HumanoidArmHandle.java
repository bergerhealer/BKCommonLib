package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.HumanoidArm</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.HumanoidArm")
public abstract class HumanoidArmHandle extends Template.Handle {
    /** @see HumanoidArmClass */
    public static final HumanoidArmClass T = Template.Class.create(HumanoidArmClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final HumanoidArmHandle LEFT = T.LEFT.getSafe();
    public static final HumanoidArmHandle RIGHT = T.RIGHT.getSafe();
    /* ============================================================================== */

    public static HumanoidArmHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.HumanoidArm</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class HumanoidArmClass extends Template.Class<HumanoidArmHandle> {
        public final Template.EnumConstant.Converted<HumanoidArmHandle> LEFT = new Template.EnumConstant.Converted<HumanoidArmHandle>();
        public final Template.EnumConstant.Converted<HumanoidArmHandle> RIGHT = new Template.EnumConstant.Converted<HumanoidArmHandle>();

    }

}

