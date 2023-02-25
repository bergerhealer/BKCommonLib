package com.bergerkiller.generated.net.minecraft.world.level.chunk.storage;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.chunk.storage.RegionFile</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.chunk.storage.RegionFile")
public abstract class RegionFileHandle extends Template.Handle {
    /** @See {@link RegionFileClass} */
    public static final RegionFileClass T = Template.Class.create(RegionFileClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RegionFileHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void closeStream();
    public abstract boolean chunkExists(int cx, int cz);
    /**
     * Stores class members for <b>net.minecraft.world.level.chunk.storage.RegionFile</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegionFileClass extends Template.Class<RegionFileHandle> {
        public final Template.Method<Void> closeStream = new Template.Method<Void>();
        public final Template.Method<Boolean> chunkExists = new Template.Method<Boolean>();

    }

}

