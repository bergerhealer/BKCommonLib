package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Iterator;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.util.LongHashSet</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class LongHashSetHandle extends Template.Handle {
    /** @See {@link LongHashSetClass} */
    public static final LongHashSetClass T = new LongHashSetClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(LongHashSetHandle.class, "org.bukkit.craftbukkit.util.LongHashSet");

    /* ============================================================================== */

    public static LongHashSetHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        LongHashSetHandle handle = new LongHashSetHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final LongHashSetHandle createNew() {
        return T.constr.newInstance();
    }

    public static final LongHashSetHandle createNew(int size) {
        return T.constr_size.newInstance(size);
    }

    /* ============================================================================== */

    public Iterator<Long> iterator() {
        return T.iterator.invoke(instance);
    }

    public int size() {
        return T.size.invoke(instance);
    }

    public boolean isEmpty() {
        return T.isEmpty.invoke(instance);
    }

    public boolean addPair(int msw, int lsw) {
        return T.addPair.invoke(instance, msw, lsw);
    }

    public boolean add(long value) {
        return T.add.invoke(instance, value);
    }

    public void removePair(int msw, int lsw) {
        T.removePair.invoke(instance, msw, lsw);
    }

    public boolean remove(long value) {
        return T.remove.invoke(instance, value);
    }

    public boolean containsPair(int msw, int lsw) {
        return T.containsPair.invoke(instance, msw, lsw);
    }

    public boolean contains(long value) {
        return T.contains.invoke(instance, value);
    }

    public void clear() {
        T.clear.invoke(instance);
    }

    public long[] toArray() {
        return T.toArray.invoke(instance);
    }

    public long popFirst() {
        return T.popFirst.invoke(instance);
    }

    public long[] popAll() {
        return T.popAll.invoke(instance);
    }

    public int hash(long value) {
        return T.hash.invoke(instance, value);
    }

    public void rehash() {
        T.rehash.invoke(instance);
    }

    public void rehashResize(int newCapacity) {
        T.rehashResize.invoke(instance, newCapacity);
    }

    public int getElementsCountField() {
        return T.elementsCountField.getInteger(instance);
    }

    public void setElementsCountField(int value) {
        T.elementsCountField.setInteger(instance, value);
    }

    public long[] getValuesField() {
        return T.valuesField.get(instance);
    }

    public void setValuesField(long[] value) {
        T.valuesField.set(instance, value);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.util.LongHashSet</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LongHashSetClass extends Template.Class<LongHashSetHandle> {
        public final Template.Constructor.Converted<LongHashSetHandle> constr = new Template.Constructor.Converted<LongHashSetHandle>();
        public final Template.Constructor.Converted<LongHashSetHandle> constr_size = new Template.Constructor.Converted<LongHashSetHandle>();

        public final Template.Field.Integer elementsCountField = new Template.Field.Integer();
        public final Template.Field<long[]> valuesField = new Template.Field<long[]>();

        public final Template.Method.Converted<Iterator<Long>> iterator = new Template.Method.Converted<Iterator<Long>>();
        public final Template.Method<Integer> size = new Template.Method<Integer>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method<Boolean> addPair = new Template.Method<Boolean>();
        public final Template.Method<Boolean> add = new Template.Method<Boolean>();
        public final Template.Method<Void> removePair = new Template.Method<Void>();
        public final Template.Method<Boolean> remove = new Template.Method<Boolean>();
        public final Template.Method<Boolean> containsPair = new Template.Method<Boolean>();
        public final Template.Method<Boolean> contains = new Template.Method<Boolean>();
        public final Template.Method<Void> clear = new Template.Method<Void>();
        public final Template.Method<long[]> toArray = new Template.Method<long[]>();
        public final Template.Method<Long> popFirst = new Template.Method<Long>();
        public final Template.Method<long[]> popAll = new Template.Method<long[]>();
        public final Template.Method<Integer> hash = new Template.Method<Integer>();
        public final Template.Method<Void> rehash = new Template.Method<Void>();
        public final Template.Method<Void> rehashResize = new Template.Method<Void>();

    }

}

