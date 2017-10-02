package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ReportedException</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ReportedExceptionHandle extends Template.Handle {
    /** @See {@link ReportedExceptionClass} */
    public static final ReportedExceptionClass T = new ReportedExceptionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ReportedExceptionHandle.class, "net.minecraft.server.ReportedException");

    /* ============================================================================== */

    public static ReportedExceptionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ReportedExceptionHandle createNew(CrashReportHandle paramCrashReport) {
        return T.constr_paramCrashReport.newInstance(paramCrashReport);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.ReportedException</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ReportedExceptionClass extends Template.Class<ReportedExceptionHandle> {
        public final Template.Constructor.Converted<ReportedExceptionHandle> constr_paramCrashReport = new Template.Constructor.Converted<ReportedExceptionHandle>();

    }

}

