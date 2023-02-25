package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftChunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.CraftChunk")
public abstract class CraftChunkHandle extends Template.Handle {
    /** @see CraftChunkClass */
    public static final CraftChunkClass T = Template.Class.create(CraftChunkClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getHandle();
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.CraftChunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftChunkClass extends Template.Class<CraftChunkHandle> {
        public final Template.Method<Object> getHandle = new Template.Method<Object>();

    }

}

