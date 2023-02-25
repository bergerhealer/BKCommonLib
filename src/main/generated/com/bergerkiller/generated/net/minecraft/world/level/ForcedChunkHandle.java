package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.ForcedChunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.level.ForcedChunk")
public abstract class ForcedChunkHandle extends Template.Handle {
    /** @see ForcedChunkClass */
    public static final ForcedChunkClass T = Template.Class.create(ForcedChunkClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ForcedChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.ForcedChunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ForcedChunkClass extends Template.Class<ForcedChunkHandle> {
    }

}

