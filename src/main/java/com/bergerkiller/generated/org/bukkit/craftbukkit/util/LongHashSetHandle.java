package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Iterator;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.util.LongHashSet</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class LongHashSetHandle extends Template.Handle {
    /** @See {@link LongHashSetClass} */
    public static final LongHashSetClass T = new LongHashSetClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(LongHashSetHandle.class, "org.bukkit.craftbukkit.util.LongHashSet", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static LongHashSetHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static LongHashSetHandle createNew(int size) {
        return T.createNew.invoke(size);
    }

    public abstract Iterator<Long> iterator();
    public abstract int size();
    public abstract boolean isEmpty();
    public abstract void clear();
    public abstract boolean add(long value);
    public abstract boolean remove(long value);
    public abstract boolean contains(long value);
    public abstract long popFirstElement();
    public abstract long[] toArray();
    public abstract long[] popAll();
    public abstract void trim();

    public static LongHashSetHandle createNew() {
        return createNew(16);
    }
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.util.LongHashSet</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LongHashSetClass extends Template.Class<LongHashSetHandle> {
        public final Template.StaticMethod.Converted<LongHashSetHandle> createNew = new Template.StaticMethod.Converted<LongHashSetHandle>();

        public final Template.Method.Converted<Iterator<Long>> iterator = new Template.Method.Converted<Iterator<Long>>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method<Void> clear = new Template.Method<Void>();
        public final Template.Method<Boolean> add = new Template.Method<Boolean>();
        public final Template.Method<Boolean> remove = new Template.Method<Boolean>();
        public final Template.Method<Boolean> contains = new Template.Method<Boolean>();
        public final Template.Method<Long> popFirstElement = new Template.Method<Long>();
        public final Template.Method<long[]> toArray = new Template.Method<long[]>();
        public final Template.Method<long[]> popAll = new Template.Method<long[]>();
        public final Template.Method<Void> trim = new Template.Method<Void>();

    }

}

