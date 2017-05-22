package com.bergerkiller.generated.net.minecraft.server;

import java.io.File;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class WorldNBTStorageHandle extends IDataManagerHandle {
    public static final WorldNBTStorageClass T = new WorldNBTStorageClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldNBTStorageHandle.class, "net.minecraft.server.WorldNBTStorage");


    /* ============================================================================== */

    public static WorldNBTStorageHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldNBTStorageHandle handle = new WorldNBTStorageHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public File getPlayerDir() {
        return T.getPlayerDir.invoke(instance);
    }

    public static final class WorldNBTStorageClass extends Template.Class<WorldNBTStorageHandle> {
        public final Template.Method<File> getPlayerDir = new Template.Method<File>();

    }
}
