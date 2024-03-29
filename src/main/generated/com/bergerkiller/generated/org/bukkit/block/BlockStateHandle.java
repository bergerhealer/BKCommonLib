package com.bergerkiller.generated.org.bukkit.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Instance wrapper handle for type <b>org.bukkit.block.BlockState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.block.BlockState")
public abstract class BlockStateHandle extends Template.Handle {
    /** @see BlockStateClass */
    public static final BlockStateClass T = Template.Class.create(BlockStateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockData getBlockData();
    /**
     * Stores class members for <b>org.bukkit.block.BlockState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockStateClass extends Template.Class<BlockStateHandle> {
        @Template.Optional
        public final Template.Method<Boolean> isPlaced = new Template.Method<Boolean>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();

    }

}

