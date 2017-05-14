package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CrashReportSystemDetailsHandle extends Template.Handle {
    public static final CrashReportSystemDetailsClass T = new CrashReportSystemDetailsClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CrashReportSystemDetailsHandle.class, "net.minecraft.server.CrashReportSystemDetails");


    /* ============================================================================== */

    public static final CrashReportSystemDetailsHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CrashReportSystemDetailsHandle handle = new CrashReportSystemDetailsHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class CrashReportSystemDetailsClass extends Template.Class {
    }
}
