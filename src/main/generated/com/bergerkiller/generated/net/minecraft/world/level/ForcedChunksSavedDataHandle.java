package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.ForcedChunksSavedData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.world.level.ForcedChunksSavedData")
public abstract class ForcedChunksSavedDataHandle extends Template.Handle {
    /** @see ForcedChunksSavedDataClass */
    public static final ForcedChunksSavedDataClass T = Template.Class.create(ForcedChunksSavedDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ForcedChunksSavedDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.ForcedChunksSavedData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ForcedChunksSavedDataClass extends Template.Class<ForcedChunksSavedDataHandle> {
    }

}

