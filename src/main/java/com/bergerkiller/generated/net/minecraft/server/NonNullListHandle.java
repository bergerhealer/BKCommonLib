package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.NonNullList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public abstract class NonNullListHandle extends Template.Handle {
    /** @See {@link NonNullListClass} */
    public static final NonNullListClass T = new NonNullListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NonNullListHandle.class, "net.minecraft.server.NonNullList", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static NonNullListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static List<?> create() {
        return T.create.invoke();
    }

    /**
     * Stores class members for <b>net.minecraft.server.NonNullList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NonNullListClass extends Template.Class<NonNullListHandle> {
        public final Template.StaticMethod.Converted<List<?>> create = new Template.StaticMethod.Converted<List<?>>();

    }

}

