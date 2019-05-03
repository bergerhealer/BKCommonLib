package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap.Entry;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IntHashMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class IntHashMapHandle extends Template.Handle {
    /** @See {@link IntHashMapClass} */
    public static final IntHashMapClass T = new IntHashMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IntHashMapHandle.class, "net.minecraft.server.IntHashMap", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static IntHashMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static IntHashMapHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract Object get(int key);
    public abstract Object remove(int key);
    public abstract void put(int key, Object paramV);
    public abstract boolean containsKey(int key);
    public abstract void clear();
    public abstract Object getEntry(int key);
    public abstract List<Entry> getEntries();
    public abstract List<Object> getValues();
    /**
     * Stores class members for <b>net.minecraft.server.IntHashMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IntHashMapClass extends Template.Class<IntHashMapHandle> {
        public final Template.StaticMethod.Converted<IntHashMapHandle> createNew = new Template.StaticMethod.Converted<IntHashMapHandle>();

        public final Template.Method<Object> get = new Template.Method<Object>();
        public final Template.Method<Object> remove = new Template.Method<Object>();
        public final Template.Method.Converted<Void> put = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> containsKey = new Template.Method<Boolean>();
        public final Template.Method<Void> clear = new Template.Method<Void>();
        public final Template.Method.Converted<Object> getEntry = new Template.Method.Converted<Object>();
        public final Template.Method<List<Entry>> getEntries = new Template.Method<List<Entry>>();
        public final Template.Method<List<Object>> getValues = new Template.Method<List<Object>>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.IntHashMap.IntHashMapEntry</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public abstract static class IntHashMapEntryHandle extends Template.Handle {
        /** @See {@link IntHashMapEntryClass} */
        public static final IntHashMapEntryClass T = new IntHashMapEntryClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(IntHashMapEntryHandle.class, "net.minecraft.server.IntHashMap.IntHashMapEntry", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

        /* ============================================================================== */

        public static IntHashMapEntryHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public abstract int getKey();
        public abstract Object getValue();
        public abstract void setValue(Object value);
        /**
         * Stores class members for <b>net.minecraft.server.IntHashMap.IntHashMapEntry</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class IntHashMapEntryClass extends Template.Class<IntHashMapEntryHandle> {
            public final Template.Method<Integer> getKey = new Template.Method<Integer>();
            public final Template.Method<Object> getValue = new Template.Method<Object>();
            public final Template.Method<Void> setValue = new Template.Method<Void>();

        }

    }

}

