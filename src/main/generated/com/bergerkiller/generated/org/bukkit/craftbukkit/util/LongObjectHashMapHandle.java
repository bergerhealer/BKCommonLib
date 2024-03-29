package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.LongFunction;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.util.LongObjectHashMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.util.LongObjectHashMap")
public abstract class LongObjectHashMapHandle extends Template.Handle {
    /** @see LongObjectHashMapClass */
    public static final LongObjectHashMapClass T = Template.Class.create(LongObjectHashMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static LongObjectHashMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static LongObjectHashMapHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract void clear();
    public abstract int size();
    public abstract boolean containsKey(long key);
    public abstract Object get(long key);
    public abstract Object remove(long key);
    public abstract Object put(long key, Object value);
    public abstract Collection<Object> values();
    public abstract Set<Long> keySet();
    public abstract Object merge(long key, Object value, BiFunction<?, ?, ?> remappingFunction);
    public abstract Object computeIfAbsent(long key, LongFunction<?> mappingFunction);
    public abstract Object getOrDefault(long key, Object defaultValue);
    public abstract LongObjectHashMapHandle cloneMap();
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.util.LongObjectHashMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LongObjectHashMapClass extends Template.Class<LongObjectHashMapHandle> {
        public final Template.StaticMethod.Converted<LongObjectHashMapHandle> createNew = new Template.StaticMethod.Converted<LongObjectHashMapHandle>();

        public final Template.Method<Void> clear = new Template.Method<Void>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Boolean> containsKey = new Template.Method<Boolean>();
        public final Template.Method<Object> get = new Template.Method<Object>();
        public final Template.Method<Object> remove = new Template.Method<Object>();
        public final Template.Method<Object> put = new Template.Method<Object>();
        public final Template.Method.Converted<Collection<Object>> values = new Template.Method.Converted<Collection<Object>>();
        public final Template.Method.Converted<Set<Long>> keySet = new Template.Method.Converted<Set<Long>>();
        public final Template.Method<Object> merge = new Template.Method<Object>();
        public final Template.Method<Object> computeIfAbsent = new Template.Method<Object>();
        public final Template.Method<Object> getOrDefault = new Template.Method<Object>();
        public final Template.Method.Converted<LongObjectHashMapHandle> cloneMap = new Template.Method.Converted<LongObjectHashMapHandle>();

    }

}

