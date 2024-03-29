package com.bergerkiller.generated.org.bukkit.event.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockCanBuildEvent;

/**
 * Instance wrapper handle for type <b>org.bukkit.event.block.BlockCanBuildEvent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.event.block.BlockCanBuildEvent")
public abstract class BlockCanBuildEventHandle extends Template.Handle {
    /** @see BlockCanBuildEventClass */
    public static final BlockCanBuildEventClass T = Template.Class.create(BlockCanBuildEventClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockCanBuildEventHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static BlockCanBuildEvent create(Block block, BlockData data, boolean canBuild) {
        return T.create.invoker.invoke(null,block, data, canBuild);
    }

    /**
     * Stores class members for <b>org.bukkit.event.block.BlockCanBuildEvent</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockCanBuildEventClass extends Template.Class<BlockCanBuildEventHandle> {
        public final Template.StaticMethod<BlockCanBuildEvent> create = new Template.StaticMethod<BlockCanBuildEvent>();

    }

}

