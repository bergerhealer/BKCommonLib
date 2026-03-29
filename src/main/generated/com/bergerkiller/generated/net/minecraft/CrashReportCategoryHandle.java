package com.bergerkiller.generated.net.minecraft;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.CrashReportCategory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.CrashReportCategory")
public abstract class CrashReportCategoryHandle extends Template.Handle {
    /** @see CrashReportCategoryClass */
    public static final CrashReportCategoryClass T = Template.Class.create(CrashReportCategoryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CrashReportCategoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.CrashReportCategory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CrashReportCategoryClass extends Template.Class<CrashReportCategoryHandle> {
    }

}

