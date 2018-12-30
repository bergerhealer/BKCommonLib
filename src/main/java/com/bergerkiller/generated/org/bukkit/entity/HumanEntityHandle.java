package com.bergerkiller.generated.org.bukkit.entity;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>org.bukkit.entity.HumanEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class HumanEntityHandle extends EntityHandle {
    /** @See {@link HumanEntityClass} */
    public static final HumanEntityClass T = new HumanEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(HumanEntityHandle.class, "org.bukkit.entity.HumanEntity", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static HumanEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.entity.HumanEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class HumanEntityClass extends Template.Class<HumanEntityHandle> {
        @Template.Optional
        public final Template.Method.Converted<Object> getMainHand = new Template.Method.Converted<Object>();

    }

}

