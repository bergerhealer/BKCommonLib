package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.PackedItem;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.syncher.SynchedEntityData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.syncher.SynchedEntityData")
public abstract class SynchedEntityDataHandle extends Template.Handle {
    /** @see SynchedEntityDataClass */
    public static final SynchedEntityDataClass T = Template.Class.create(SynchedEntityDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static SynchedEntityDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static SynchedEntityDataHandle createNew(EntityHandle owner) {
        return T.createNew.invoke(owner);
    }

    public abstract EntityHandle getOwner();
    public abstract void setOwner(EntityHandle owner);
    public abstract SynchedEntityDataHandle cloneWithOwner(EntityHandle owner);
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

    public static SynchedEntityDataHandle createNew(org.bukkit.entity.Entity owner) {
        return createHandle(T.createNew.raw.invoke(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(owner)));
    }
    /**
     * Stores class members for <b>net.minecraft.network.syncher.SynchedEntityData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SynchedEntityDataClass extends Template.Class<SynchedEntityDataHandle> {
        public final Template.StaticMethod.Converted<SynchedEntityDataHandle> createNew = new Template.StaticMethod.Converted<SynchedEntityDataHandle>();

        public final Template.Method.Converted<EntityHandle> getOwner = new Template.Method.Converted<EntityHandle>();
        public final Template.Method.Converted<Void> setOwner = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<SynchedEntityDataHandle> cloneWithOwner = new Template.Method.Converted<SynchedEntityDataHandle>();
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
     * Instance wrapper handle for type <b>net.minecraft.network.syncher.SynchedEntityData.DataItem</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.syncher.SynchedEntityData.DataItem")
    public abstract static class DataItemHandle extends Template.Handle {
        /** @see DataItemClass */
        public static final DataItemClass T = Template.Class.create(DataItemClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static DataItemHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public abstract void setChanged(boolean changed);
        public abstract boolean isChanged();
        public abstract Object getValue();
        public abstract void setValue(Object value);
        public abstract DataValueHandle pack();
        /**
         * Stores class members for <b>net.minecraft.network.syncher.SynchedEntityData.DataItem</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class DataItemClass extends Template.Class<DataItemHandle> {
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
            public final Template.Method.Converted<DataValueHandle> pack = new Template.Method.Converted<DataValueHandle>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.syncher.SynchedEntityData.DataValue</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.syncher.SynchedEntityData.DataValue")
    public abstract static class DataValueHandle extends Template.Handle {
        /** @see DataValueClass */
        public static final DataValueClass T = Template.Class.create(DataValueClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static DataValueHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public abstract Object value();
        public abstract DataValueHandle cloneWithValue(Object value);
        public abstract boolean isForKey(Key<?> key);
        /**
         * Stores class members for <b>net.minecraft.network.syncher.SynchedEntityData.DataValue</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class DataValueClass extends Template.Class<DataValueHandle> {
            public final Template.Method<Object> value = new Template.Method<Object>();
            public final Template.Method.Converted<DataValueHandle> cloneWithValue = new Template.Method.Converted<DataValueHandle>();
            public final Template.Method.Converted<Boolean> isForKey = new Template.Method.Converted<Boolean>();

        }

    }

}

