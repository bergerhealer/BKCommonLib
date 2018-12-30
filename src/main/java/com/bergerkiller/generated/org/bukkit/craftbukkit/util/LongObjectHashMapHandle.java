package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.util.LongObjectHashMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class LongObjectHashMapHandle extends Template.Handle {
    /** @See {@link LongObjectHashMapClass} */
    public static final LongObjectHashMapClass T = new LongObjectHashMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(LongObjectHashMapHandle.class, "org.bukkit.craftbukkit.util.LongObjectHashMap", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static LongObjectHashMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final LongObjectHashMapHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */

    public abstract void clear();
    public abstract int size();
    public abstract boolean containsKey(long key);
    public abstract Object get(long key);
    public abstract Object remove(long key);
    public abstract Object put(long key, Object value);
    public abstract Collection<Object> values();
    public abstract Set<Long> keySet();
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.util.LongObjectHashMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LongObjectHashMapClass extends Template.Class<LongObjectHashMapHandle> {
        public final Template.Constructor.Converted<LongObjectHashMapHandle> constr = new Template.Constructor.Converted<LongObjectHashMapHandle>();

        public final Template.Method<Void> clear = new Template.Method<Void>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Boolean> containsKey = new Template.Method<Boolean>();
        public final Template.Method<Object> get = new Template.Method<Object>();
        public final Template.Method<Object> remove = new Template.Method<Object>();
        public final Template.Method<Object> put = new Template.Method<Object>();
        public final Template.Method<Collection<Object>> values = new Template.Method<Collection<Object>>();
        public final Template.Method<Set<Long>> keySet = new Template.Method<Set<Long>>();

    }

}

