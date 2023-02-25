package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.ReportedException</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.ReportedException")
public abstract class ReportedExceptionHandle extends Template.Handle {
    /** @See {@link ReportedExceptionClass} */
    public static final ReportedExceptionClass T = Template.Class.create(ReportedExceptionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ReportedExceptionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ReportedExceptionHandle createNew(CrashReportHandle paramCrashReport) {
        return T.constr_paramCrashReport.newInstance(paramCrashReport);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.ReportedException</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ReportedExceptionClass extends Template.Class<ReportedExceptionHandle> {
        public final Template.Constructor.Converted<ReportedExceptionHandle> constr_paramCrashReport = new Template.Constructor.Converted<ReportedExceptionHandle>();

    }

}

