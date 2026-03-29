package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap")
public abstract class CrudeIncrementalIntIdentityHashBiMapHandle extends Template.Handle {
    /** @see CrudeIncrementalIntIdentityHashBiMapClass */
    public static final CrudeIncrementalIntIdentityHashBiMapClass T = Template.Class.create(CrudeIncrementalIntIdentityHashBiMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CrudeIncrementalIntIdentityHashBiMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId(Object value);
    /**
     * Stores class members for <b>net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CrudeIncrementalIntIdentityHashBiMapClass extends Template.Class<CrudeIncrementalIntIdentityHashBiMapHandle> {
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

