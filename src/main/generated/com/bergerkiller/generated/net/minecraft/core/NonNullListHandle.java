package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.NonNullList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.core.NonNullList")
public abstract class NonNullListHandle extends Template.Handle {
    /** @see NonNullListClass */
    public static final NonNullListClass T = Template.Class.create(NonNullListClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NonNullListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static List<?> create() {
        return T.create.invoke();
    }

    /**
     * Stores class members for <b>net.minecraft.core.NonNullList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NonNullListClass extends Template.Class<NonNullListHandle> {
        public final Template.StaticMethod.Converted<List<?>> create = new Template.StaticMethod.Converted<List<?>>();

    }

}

