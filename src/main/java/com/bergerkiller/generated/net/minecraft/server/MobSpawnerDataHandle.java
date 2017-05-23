package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class MobSpawnerDataHandle extends Template.Handle {
    public static final MobSpawnerDataClass T = new MobSpawnerDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MobSpawnerDataHandle.class, "net.minecraft.server.MobSpawnerData");


    /* ============================================================================== */

    public static MobSpawnerDataHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        MobSpawnerDataHandle handle = new MobSpawnerDataHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class MobSpawnerDataClass extends Template.Class<MobSpawnerDataHandle> {
    }
}
