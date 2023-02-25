package com.bergerkiller.generated.org.bukkit.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;

/**
 * Instance wrapper handle for type <b>org.bukkit.entity.HumanEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.entity.HumanEntity")
public abstract class HumanEntityHandle extends EntityHandle {
    /** @see HumanEntityClass */
    public static final HumanEntityClass T = Template.Class.create(HumanEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static HumanEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract HumanHand getMainHumanHand();
    /**
     * Stores class members for <b>org.bukkit.entity.HumanEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class HumanEntityClass extends Template.Class<HumanEntityHandle> {
        public final Template.Method<HumanHand> getMainHumanHand = new Template.Method<HumanHand>();

    }

}

