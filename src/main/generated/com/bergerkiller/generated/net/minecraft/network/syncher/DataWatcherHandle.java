package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.PackedItem;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.syncher.DataWatcher</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.syncher.DataWatcher")
public abstract class DataWatcherHandle extends Template.Handle {
    /** @see DataWatcherClass */
    public static final DataWatcherClass T = Template.Class.create(DataWatcherClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DataWatcherHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static DataWatcherHandle createNew(EntityHandle owner) {
        return T.createNew.invoke(owner);
    }

    public abstract EntityHandle getOwner();
    public abstract void setOwner(EntityHandle owner);
    public abstract DataWatcherHandle cloneWithOwner(EntityHandle owner);
    public abstract List<PackedItem<?>> packChanges();
    public abstract List<PackedItem<?>> packNonDefaults();
    public abstract List<PackedItem<?>> packAll();
    public abstract List<Item<?>> getCopyOfAllItems();
    public abstract Item<Object> read(Key<?> key);
    public abstract void setRawDefault(Key<?> dwo, Object rawDefaultValue);
    public abstract void setRaw(Key<?> dwo, Object rawValue, boolean force);
    public abstract Object get(Key<?> key);
    public abstract boolean isChanged();
    public abstract boolean isEmpty();
    public static final Object UNSET_MARKER_VALUE = com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItemInit.UNSET_MARKER_VALUE;

    public static DataWatcherHandle createNew(org.bukkit.entity.Entity owner) {
        return createHandle(T.createNew.raw.invoke(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(owner)));
    }
    /**
     * Stores class members for <b>net.minecraft.network.syncher.DataWatcher</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataWatcherClass extends Template.Class<DataWatcherHandle> {
        public final Template.StaticMethod.Converted<DataWatcherHandle> createNew = new Template.StaticMethod.Converted<DataWatcherHandle>();

        public final Template.Method.Converted<EntityHandle> getOwner = new Template.Method.Converted<EntityHandle>();
        public final Template.Method.Converted<Void> setOwner = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<DataWatcherHandle> cloneWithOwner = new Template.Method.Converted<DataWatcherHandle>();
        public final Template.Method.Converted<List<PackedItem<?>>> packChanges = new Template.Method.Converted<List<PackedItem<?>>>();
        public final Template.Method.Converted<List<PackedItem<?>>> packNonDefaults = new Template.Method.Converted<List<PackedItem<?>>>();
        public final Template.Method.Converted<List<PackedItem<?>>> packAll = new Template.Method.Converted<List<PackedItem<?>>>();
        public final Template.Method.Converted<List<Item<?>>> getCopyOfAllItems = new Template.Method.Converted<List<Item<?>>>();
        public final Template.Method.Converted<Item<Object>> read = new Template.Method.Converted<Item<Object>>();
        public final Template.Method.Converted<Void> setRawDefault = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> setRaw = new Template.Method.Converted<Void>();
        public final Template.Method<Object> get = new Template.Method<Object>();
        public final Template.Method<Boolean> isChanged = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.syncher.DataWatcher.Item</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.syncher.DataWatcher.Item")
    public abstract static class ItemHandle extends Template.Handle {
        /** @see ItemClass */
        public static final ItemClass T = Template.Class.create(ItemClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static ItemHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public abstract void setChanged(boolean changed);
        public abstract boolean isChanged();
        public abstract Object getValue();
        public abstract void setValue(Object value);
        public abstract PackedItemHandle pack();
        /**
         * Stores class members for <b>net.minecraft.network.syncher.DataWatcher.Item</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ItemClass extends Template.Class<ItemHandle> {
            @Template.Optional
            public final Template.Field.Integer typeId = new Template.Field.Integer();
            @Template.Optional
            public final Template.Field.Integer keyId = new Template.Field.Integer();
            @Template.Optional
            public final Template.Field.Converted<Key<?>> key = new Template.Field.Converted<Key<?>>();

            public final Template.Method<Void> setChanged = new Template.Method<Void>();
            public final Template.Method<Boolean> isChanged = new Template.Method<Boolean>();
            public final Template.Method<Object> getValue = new Template.Method<Object>();
            public final Template.Method<Void> setValue = new Template.Method<Void>();
            public final Template.Method.Converted<PackedItemHandle> pack = new Template.Method.Converted<PackedItemHandle>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.syncher.DataWatcher.PackedItem</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.syncher.DataWatcher.PackedItem")
    public abstract static class PackedItemHandle extends Template.Handle {
        /** @see PackedItemClass */
        public static final PackedItemClass T = Template.Class.create(PackedItemClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static PackedItemHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public abstract Object value();
        public abstract PackedItemHandle cloneWithValue(Object value);
        public abstract boolean isForKey(Key<?> key);
        /**
         * Stores class members for <b>net.minecraft.network.syncher.DataWatcher.PackedItem</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class PackedItemClass extends Template.Class<PackedItemHandle> {
            public final Template.Method<Object> value = new Template.Method<Object>();
            public final Template.Method.Converted<PackedItemHandle> cloneWithValue = new Template.Method.Converted<PackedItemHandle>();
            public final Template.Method.Converted<Boolean> isForKey = new Template.Method.Converted<Boolean>();

        }

    }

}

