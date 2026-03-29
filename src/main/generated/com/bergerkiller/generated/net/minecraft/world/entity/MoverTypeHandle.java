package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.MoverType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.entity.MoverType")
public abstract class MoverTypeHandle extends Template.Handle {
    /** @see MoverTypeClass */
    public static final MoverTypeClass T = Template.Class.create(MoverTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final MoverTypeHandle SELF = T.SELF.getSafe();
    public static final MoverTypeHandle PLAYER = T.PLAYER.getSafe();
    public static final MoverTypeHandle PISTON = T.PISTON.getSafe();
    public static final MoverTypeHandle SHULKER_BOX = T.SHULKER_BOX.getSafe();
    public static final MoverTypeHandle SHULKER = T.SHULKER.getSafe();
    /* ============================================================================== */

    public static MoverTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.MoverType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MoverTypeClass extends Template.Class<MoverTypeHandle> {
        public final Template.EnumConstant.Converted<MoverTypeHandle> SELF = new Template.EnumConstant.Converted<MoverTypeHandle>();
        public final Template.EnumConstant.Converted<MoverTypeHandle> PLAYER = new Template.EnumConstant.Converted<MoverTypeHandle>();
        public final Template.EnumConstant.Converted<MoverTypeHandle> PISTON = new Template.EnumConstant.Converted<MoverTypeHandle>();
        public final Template.EnumConstant.Converted<MoverTypeHandle> SHULKER_BOX = new Template.EnumConstant.Converted<MoverTypeHandle>();
        public final Template.EnumConstant.Converted<MoverTypeHandle> SHULKER = new Template.EnumConstant.Converted<MoverTypeHandle>();

    }

}

