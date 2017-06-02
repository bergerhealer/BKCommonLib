package com.bergerkiller.generated.net.minecraft.server;

import java.io.File;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.Map;

public class RegionFileCacheHandle extends Template.Handle {
    public static final RegionFileCacheClass T = new RegionFileCacheClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegionFileCacheHandle.class, "net.minecraft.server.RegionFileCache");

    public static final Map<File, RegionFileHandle> FILES = T.FILES.getSafe();
    /* ============================================================================== */

    public static RegionFileCacheHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        RegionFileCacheHandle handle = new RegionFileCacheHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class RegionFileCacheClass extends Template.Class<RegionFileCacheHandle> {
        public final Template.StaticField.Converted<Map<File, RegionFileHandle>> FILES = new Template.StaticField.Converted<Map<File, RegionFileHandle>>();

    }

}

