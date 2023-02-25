package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.Blocks</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.Blocks")
public abstract class BlocksHandle extends Template.Handle {
    /** @see BlocksClass */
    public static final BlocksClass T = Template.Class.create(BlocksClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final Object AIR = T.AIR.getSafe();
    public static final Object LADDER = T.LADDER.getSafe();
    /* ============================================================================== */

    public static BlocksHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.block.Blocks</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlocksClass extends Template.Class<BlocksHandle> {
        public final Template.StaticField.Converted<Object> AIR = new Template.StaticField.Converted<Object>();
        public final Template.StaticField.Converted<Object> LADDER = new Template.StaticField.Converted<Object>();

    }

}

