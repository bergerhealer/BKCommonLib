package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.CrashReportSystemDetailsHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.generated.net.minecraft.server.CrashReportHandle;

public class CrashReportHandle extends Template.Handle {
    public static final CrashReportClass T = new CrashReportClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CrashReportHandle.class, "net.minecraft.server.CrashReport");


    /* ============================================================================== */

    public static final CrashReportHandle createHandle(Object handleInstance) {
        if (handleInstance == null) throw new IllegalArgumentException("Handle instance can not be null");
        CrashReportHandle handle = new CrashReportHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static CrashReportHandle create(Throwable throwable, String message) {
        return T.create.invoke(throwable, message);
    }

    public CrashReportSystemDetailsHandle getSystemDetails(String message) {
        return T.getSystemDetails.invoke(instance, message);
    }

    public static final class CrashReportClass extends Template.Class {
        public final Template.StaticMethod.Converted<CrashReportHandle> create = new Template.StaticMethod.Converted<CrashReportHandle>();

        public final Template.Method.Converted<CrashReportSystemDetailsHandle> getSystemDetails = new Template.Method.Converted<CrashReportSystemDetailsHandle>();

    }
}
