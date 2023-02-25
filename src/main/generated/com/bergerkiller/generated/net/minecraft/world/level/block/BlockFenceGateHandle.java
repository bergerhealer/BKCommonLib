package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.BlockFenceGate</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.BlockFenceGate")
public abstract class BlockFenceGateHandle extends Template.Handle {
    /** @See {@link BlockFenceGateClass} */
    public static final BlockFenceGateClass T = Template.Class.create(BlockFenceGateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockFenceGateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.block.BlockFenceGate</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockFenceGateClass extends Template.Class<BlockFenceGateHandle> {
    }

}

