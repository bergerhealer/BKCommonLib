package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.CrashReport</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.CrashReport")
public abstract class CrashReportHandle extends Template.Handle {
    /** @see CrashReportClass */
    public static final CrashReportClass T = Template.Class.create(CrashReportClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CrashReportHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static CrashReportHandle create(Throwable throwable, String message) {
        return T.create.invoke(throwable, message);
    }

    public abstract CrashReportSystemDetailsHandle getSystemDetails(String message);
    /**
     * Stores class members for <b>net.minecraft.CrashReport</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CrashReportClass extends Template.Class<CrashReportHandle> {
        public final Template.StaticMethod.Converted<CrashReportHandle> create = new Template.StaticMethod.Converted<CrashReportHandle>();

        public final Template.Method.Converted<CrashReportSystemDetailsHandle> getSystemDetails = new Template.Method.Converted<CrashReportSystemDetailsHandle>();

    }

}

