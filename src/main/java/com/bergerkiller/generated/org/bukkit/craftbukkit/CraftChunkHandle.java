package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftChunkHandle extends Template.Handle {
    public static final CraftChunkClass T = new CraftChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftChunkHandle.class, "org.bukkit.craftbukkit.CraftChunk");


    /* ============================================================================== */

    public static CraftChunkHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftChunkHandle handle = new CraftChunkHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class CraftChunkClass extends Template.Class<CraftChunkHandle> {
    }
}
