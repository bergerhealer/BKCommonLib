package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class NBTBaseHandle extends Template.Handle {
    public static final NBTBaseClass T = new NBTBaseClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NBTBaseHandle.class, "net.minecraft.server.NBTBase");


    /* ============================================================================== */

    public static NBTBaseHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        NBTBaseHandle handle = new NBTBaseHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class NBTBaseClass extends Template.Class<NBTBaseHandle> {
    }
}
