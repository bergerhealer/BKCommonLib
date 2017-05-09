package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonDisabledEntity;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.reflection.net.minecraft.server.NMSDataWatcher;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;

import java.util.List;

/**
 * This class is a wrapper of the DataWatcher class from CraftBukkit<br>
 * It is used to store data and to keep track of changes so they can be
 * synchronized
 */
public class DataWatcher extends BasicWrapper {

    public DataWatcher(org.bukkit.entity.Entity entityOwner) {
        this(NMSDataWatcher.constructor1.newInstance(Conversion.toEntityHandle.convert(entityOwner)));
    }

    /**
     * Initializes a new Empty DataWatcher. Please avoid binding this
     * constructed DataWatcher to live entities. When doing so, instead use the
     * Entity-accepting constructor.
     */
    public DataWatcher() {
        this(NMSDataWatcher.constructor1.newInstance(CommonDisabledEntity.INSTANCE));
    }

    public DataWatcher(Object handle) {
        setHandle(handle);
    }

    /**
     * Write a new value to the watched objects.
     * If the key does not yet exist, the key is added with the default value specified.
     *
     * @param key Object key
     * @param value Value to set to
     */
    public <V> void set(Key<V> key, V value) {
        if (isWatched(key)) {
            NMSDataWatcher.set.invoke(handle, key.handle, value);
        } else {
            watch(key, value);
        }
    }

    /**
     * Read an object from the watched objects
     *
     * @param ket Object key
     * @return Object value at the key
     */
    @SuppressWarnings("unchecked")
    public <V> V get(Key<V> key) {
        Object item = NMSDataWatcher.read.invoke(handle, key.handle);
        if (item == null) {
            throw new IllegalArgumentException("This key is not watched in this DataWatcher");
        }
        return (V) NMSDataWatcher.Item.value.get(item);
    }

    /**
     * Watch an object
     *
     * @param key of the watched item
     * @param defaultValue of the watched item
     */
    public <T> void watch(Key<T> key, T defaultValue) {
        NMSDataWatcher.watch.invoke(handle, key.handle, defaultValue);
    }

    /**
     * Checks whether a particular key is registered for watching
     * 
     * @param key to check
     * @return True if watched, False if not
     */
    public boolean isWatched(Key<?> key) {
        return NMSDataWatcher.read.invoke(handle, key.handle) != null;
    }

    /**
     * Get all watched objects
     *
     * @return Watched objects
     */
    public List<Item<?>> getWatchedItems() {
        return getWatchedItems(false);
    }

    /**
     * Get all watched objects
     *
     * @param unwatch to unwatch all the items before returning them
     * @return Watched objects
     */
    public List<Item<?>> getWatchedItems(boolean unwatch) {
        List<Object> itemHandles;
        if (unwatch) {
            itemHandles = NMSDataWatcher.unwatchAndReturnAllWatched.invoke(handle);
        } else {
            itemHandles = NMSDataWatcher.returnAllWatched.invoke(handle);
        }
        return new ConvertingList<Item<?>>(itemHandles, DuplexConversion.dataWatcherItem);
    }

    /**
     * Gets whether this Data Watcher has changed since the last tick
     *
     * @return True if it had changed, False if not
     */
    public boolean isChanged() {
        return NMSDataWatcher.isChanged.invoke(handle);
    }

    /**
     * Gets whether this Data Watcher is empty or not. An empty Data Watcher
     * does not require any update messages to the players.
     *
     * @return True if empty, False if not
     */
    public boolean isEmpty() {
        return NMSDataWatcher.isEmpty.invoke(handle);
    }

    @Override
    public String toString() {
        String str = "DataWatcher Items[";
        boolean first = true;
        for (Item<?> item : getWatchedItems()) {
            if (first) {
                first = false;
            } else {
                str += ", ";
            }
            str += item.toString();
        }
        str += "]";
        return str;
    }

    /**
     * Wrapper around a raw DataWatcher key object
     * 
     * @param <V> value type bound to the key
     */
    public static class Key<V> extends BasicWrapper {

        public Key(Object handle) {
            setHandle(handle);
        }

        /**
         * Gets the unique global serializer Id of this key.
         * This id is unique for this data value type.
         * 
         * @return Serializer Id
         */
        public int getSerializerId() {
            Object s = NMSDataWatcher.Object2.getSerializer.invoke(this.handle);
            return NMSDataWatcher.Registry.getSerializerId.invoke(null, s);
        }

        /**
         * Gets the datawatcher object Id of this key
         * 
         * @return Id
         */
        public int getId() {
            return NMSDataWatcher.Object2.getId.invoke(this.handle);
        }

        /**
         * Reads a datawatcher key from a static field value declared in a (net.minecraft.server) class
         * 
         * @param template for the class where the field is defined
         * @param fieldname of the datawatcher key
         * @return datawatcher key
         */
        public static <T> Key<T> fromStaticField(ClassTemplate<?> template, String fieldname) {
            return new DataWatcher.Key<T>(template.getStaticFieldValue(fieldname, NMSDataWatcher.Object2.T.getType()));
        }
    }

    /**
     * References a single watched item, containing the key, value, and changed state
     * 
     * @param <V> value type of the item
     */
    public static class Item<V> extends BasicWrapper {

        public Item(Object handle) {
            setHandle(handle);
        }

        @SuppressWarnings("unchecked")
        public Key<V> getKey() {
            return (Key<V>) NMSDataWatcher.Item.key.get(this.handle);
        }

        public boolean isChanged() {
            return NMSDataWatcher.Item.changed.get(this.handle);
        }

        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) NMSDataWatcher.Item.value.get(this.handle);
        }

        public void setValue(V value, boolean changed) {
            NMSDataWatcher.Item.value.set(this.handle, value);
            NMSDataWatcher.Item.changed.set(this.handle, changed);
        }

        @Override
        public String toString() {
            return "{id=" + getKey().getId() + ",changed=" + isChanged() + ",value=" + getValue() + "}";
        }
    }
    
    /**
     * References the value bound to a DataWatcher Key for a particular Entity
     *
     * @param <V> value type bound to the key
     */
    public static class EntityItem<V> {
        private final ExtendedEntity<?> owner;
        private final Object keyHandle;

        public EntityItem(ExtendedEntity<?> owner, Key<V> key) {
            this.owner = owner;
            this.keyHandle = (key == null) ? null : key.handle;
        }

        /**
         * Gets the value of this DataWatcher metadata property
         * 
         * @return current value
         */
        @SuppressWarnings("unchecked")
        public V get() {
            Object watcher = NMSEntity.datawatcher.getInternal(owner.getHandle());
            return (V) NMSDataWatcher.get.invoke(watcher, keyHandle);
        }

        /**
         * Sets a new value for this DataWatcher metadata property. Watchers will be notified.
         * 
         * @param value to set to
         */
        public void set(V value) {
            Object watcher = NMSEntity.datawatcher.getInternal(owner.getHandle());
            NMSDataWatcher.set.invoke(watcher, keyHandle, value);
        }

        /**
         * Translates the internally stored value to another type using a converter pair
         * 
         * @param converterPair to use for translation
         * @return translated item
         */
        public <C> EntityItem<C> translate(DuplexConverter<V, C> converterPair) {
            return new ConvertingEntityItem<V, C>(this, converterPair);
        }
    }

    private static class ConvertingEntityItem<A, B> extends EntityItem<B> {
        private final EntityItem<A> item;
        private final DuplexConverter<A, B> pair;

        public ConvertingEntityItem(EntityItem<A> item, DuplexConverter<A, B> pair) {
            super(null, null);
            this.item = item;
            this.pair = pair;
        }

        @Override
        public B get() {
            return pair.convert(item.get());
        }

        @Override
        public void set(B value) {
            this.item.set(pair.convertReverse(value));
        }

    }

}
