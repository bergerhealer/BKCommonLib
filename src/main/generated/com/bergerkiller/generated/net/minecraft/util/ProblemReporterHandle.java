package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.util.ProblemReporter</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.util.ProblemReporter")
public abstract class ProblemReporterHandle extends Template.Handle {
    /** @see ProblemReporterClass */
    public static final ProblemReporterClass T = Template.Class.create(ProblemReporterClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ProblemReporterHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ProblemReporterHandle createScoped() {
        return T.createScoped.invoke();
    }

    public abstract void close();
    /**
     * Stores class members for <b>net.minecraft.util.ProblemReporter</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ProblemReporterClass extends Template.Class<ProblemReporterHandle> {
        public final Template.StaticMethod.Converted<ProblemReporterHandle> createScoped = new Template.StaticMethod.Converted<ProblemReporterHandle>();

        public final Template.Method<Void> close = new Template.Method<Void>();

    }

}

