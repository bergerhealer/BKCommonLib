package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.File;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldNBTStorage</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class WorldNBTStorageHandle extends IDataManagerHandle {
    /** @See {@link WorldNBTStorageClass} */
    public static final WorldNBTStorageClass T = new WorldNBTStorageClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldNBTStorageHandle.class, "net.minecraft.server.WorldNBTStorage");

    /* ============================================================================== */

    public static WorldNBTStorageHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public File getPlayerDir() {
        return T.getPlayerDir.invoke(getRaw());
    }

    /**
     * Stores class members for <b>net.minecraft.server.WorldNBTStorage</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldNBTStorageClass extends Template.Class<WorldNBTStorageHandle> {
        public final Template.Method<File> getPlayerDir = new Template.Method<File>();

    }

}

