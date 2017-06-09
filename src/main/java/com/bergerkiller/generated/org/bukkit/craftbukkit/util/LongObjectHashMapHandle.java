package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.util.LongObjectHashMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class LongObjectHashMapHandle extends Template.Handle {
    /** @See {@link LongObjectHashMapClass} */
    public static final LongObjectHashMapClass T = new LongObjectHashMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(LongObjectHashMapHandle.class, "org.bukkit.craftbukkit.util.LongObjectHashMap");

    /* ============================================================================== */

    public static LongObjectHashMapHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        LongObjectHashMapHandle handle = new LongObjectHashMapHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Object get(long key) {
        return T.get.invoke(instance, key);
    }

    public Object remove(long key) {
        return T.remove.invoke(instance, key);
    }

    public Object put(long key, Object value) {
        return T.put.invoke(instance, key, value);
    }

    public Collection<Object> values() {
        return T.values.invoke(instance);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.util.LongObjectHashMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LongObjectHashMapClass extends Template.Class<LongObjectHashMapHandle> {
        public final Template.Method<Object> get = new Template.Method<Object>();
        public final Template.Method<Object> remove = new Template.Method<Object>();
        public final Template.Method<Object> put = new Template.Method<Object>();
        public final Template.Method<Collection<Object>> values = new Template.Method<Collection<Object>>();

    }

}

