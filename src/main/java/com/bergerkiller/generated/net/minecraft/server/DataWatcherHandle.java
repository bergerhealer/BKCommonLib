package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DataWatcher</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class DataWatcherHandle extends Template.Handle {
    /** @See {@link DataWatcherClass} */
    public static final DataWatcherClass T = new DataWatcherClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DataWatcherHandle.class, "net.minecraft.server.DataWatcher");

    /* ============================================================================== */

    public static DataWatcherHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        DataWatcherHandle handle = new DataWatcherHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final DataWatcherHandle createNew(EntityHandle owner) {
        return T.constr_owner.newInstance(owner);
    }

    /* ============================================================================== */

    public List<Item<?>> unwatchAndReturnAllWatched() {
        return T.unwatchAndReturnAllWatched.invoke(instance);
    }

    public List<Item<?>> returnAllWatched() {
        return T.returnAllWatched.invoke(instance);
    }

    public Item<?> read(Key<?> key) {
        return T.read.invoke(instance, key);
    }

    public boolean isChanged() {
        return T.isChanged.invoke(instance);
    }

    public boolean isEmpty() {
        return T.isEmpty.invoke(instance);
    }


    public static DataWatcherHandle createNew(org.bukkit.entity.Entity owner) {
        return createHandle(T.constr_owner.raw.newInstance(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(owner)));
    }


    public <T> void register(Key<T> key, T defaultValue) {
        T.register.invoke(instance, key, key.getType().getConverter().convertReverse(defaultValue));
    }

    public <T> void set(com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T> key, T value) {
        T.set.invoke(instance, key, key.getType().getConverter().convertReverse(value));
    }

    public <T> T get(com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T> key) {
        Object rawValue;
        if (T.get.isAvailable()) {
            rawValue = T.get.invoke(instance, key);
        } else {
            rawValue = this.read(key).getValue();
        }
        return key.getType().getConverter().convert(rawValue);
    }
    public EntityHandle getOwner() {
        return T.owner.get(instance);
    }

    public void setOwner(EntityHandle value) {
        T.owner.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.DataWatcher</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataWatcherClass extends Template.Class<DataWatcherHandle> {
        public final Template.Constructor.Converted<DataWatcherHandle> constr_owner = new Template.Constructor.Converted<DataWatcherHandle>();

        public final Template.Field.Converted<EntityHandle> owner = new Template.Field.Converted<EntityHandle>();

        public final Template.Method.Converted<List<Item<?>>> unwatchAndReturnAllWatched = new Template.Method.Converted<List<Item<?>>>();
        public final Template.Method.Converted<List<Item<?>>> returnAllWatched = new Template.Method.Converted<List<Item<?>>>();
        @Template.Optional
        public final Template.Method.Converted<Void> register = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Item<?>> read = new Template.Method.Converted<Item<?>>();
        @Template.Optional
        public final Template.Method.Converted<Object> get = new Template.Method.Converted<Object>();
        @Template.Optional
        public final Template.Method.Converted<Void> set = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> isChanged = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.DataWatcher.Item</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    public static class ItemHandle extends Template.Handle {
        /** @See {@link ItemClass} */
        public static final ItemClass T = new ItemClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(ItemHandle.class, "net.minecraft.server.DataWatcher.Item");

        /* ============================================================================== */

        public static ItemHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            ItemHandle handle = new ItemHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */

        public Object getValue() {
            return T.value.get(instance);
        }

        public void setValue(Object value) {
            T.value.set(instance, value);
        }

        public boolean isChanged() {
            return T.changed.getBoolean(instance);
        }

        public void setChanged(boolean value) {
            T.changed.setBoolean(instance, value);
        }

        /**
         * Stores class members for <b>net.minecraft.server.DataWatcher.Item</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ItemClass extends Template.Class<ItemHandle> {
            @Template.Optional
            public final Template.Field.Integer typeId = new Template.Field.Integer();
            @Template.Optional
            public final Template.Field.Integer keyId = new Template.Field.Integer();
            @Template.Optional
            public final Template.Field.Converted<Key<?>> key = new Template.Field.Converted<Key<?>>();
            public final Template.Field<Object> value = new Template.Field<Object>();
            public final Template.Field.Boolean changed = new Template.Field.Boolean();

        }

    }

}

