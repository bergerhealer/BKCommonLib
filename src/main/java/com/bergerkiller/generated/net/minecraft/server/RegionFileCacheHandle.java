package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.File;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.RegionFileCache</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class RegionFileCacheHandle extends Template.Handle {
    /** @See {@link RegionFileCacheClass} */
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

    /**
     * Stores class members for <b>net.minecraft.server.RegionFileCache</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegionFileCacheClass extends Template.Class<RegionFileCacheHandle> {
        public final Template.StaticField.Converted<Map<File, RegionFileHandle>> FILES = new Template.StaticField.Converted<Map<File, RegionFileHandle>>();

    }

}

