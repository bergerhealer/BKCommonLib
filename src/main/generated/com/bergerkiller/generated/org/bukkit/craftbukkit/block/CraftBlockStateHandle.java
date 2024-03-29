package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.block.CraftBlockState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.block.CraftBlockState")
public abstract class CraftBlockStateHandle extends Template.Handle {
    /** @see CraftBlockStateClass */
    public static final CraftBlockStateClass T = Template.Class.create(CraftBlockStateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftBlockStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final BlockState createNew(Block block) {
        return T.constr_block.newInstance(block);
    }

    /* ============================================================================== */

    public abstract int getFlag();
    public abstract void setFlag(int value);
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.block.CraftBlockState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftBlockStateClass extends Template.Class<CraftBlockStateHandle> {
        public final Template.Constructor.Converted<BlockState> constr_block = new Template.Constructor.Converted<BlockState>();

        public final Template.Field.Integer flag = new Template.Field.Integer();

        @Template.Optional
        public final Template.Method<Void> init = new Template.Method<Void>();

    }

}

