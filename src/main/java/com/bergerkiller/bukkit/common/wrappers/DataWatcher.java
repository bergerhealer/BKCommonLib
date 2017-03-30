package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;
import com.bergerkiller.bukkit.common.internal.CommonDisabledEntity;
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
     * Write a new value to the watched objects
     *
     * @param key Object key
     * @param value Value to set to
     */
    public <V> void set(Key<V> key, V value) {
        NMSDataWatcher.set.invoke(handle, key.handle, value);
    }

    /**
     * Read an object from the watched objects
     *
     * @param ket Object key
     * @return Object value at the key
     */
    @SuppressWarnings("unchecked")
    public <V> V get(Key<V> key) {
        return (V) NMSDataWatcher.get.invoke(handle, key.handle);
    }

    /**
     * Watch an object
     *
     * @param index Object index
     * @param value Value
     */
    public void watch(Key<?> key, Object value) {
        NMSDataWatcher.watch.invoke(handle, key.handle, value);
    }

    /**
     * Get all watched objects
     *
     * @return Watched objects
     */
    public List<Object> getAllWatched() {
        return NMSDataWatcher.returnAllWatched.invoke(handle);
    }

    /**
     * Get all watched objects and unwatch them
     *
     * @return Watched objects
     */
    public List<Object> unwatchAndGetAllWatched() {
        return NMSDataWatcher.unwatchAndReturnAllWatched.invoke(handle);
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

    /**
     * Wrapper around a raw DataWatcher key object
     * 
     * @param <V> value type bound to the key
     */
    public static class Key<V> {
        private final Object handle;

        public Key(Object handle) {
            this.handle = handle;
        }
    }

    /**
     * References the value bound to a DataWatcher Key for a particular Entity
     *
     * @param <V> value type bound to the key
     */
    public static class Item<V> {
        private final ExtendedEntity<?> owner;
        private final Object keyHandle;

        public Item(ExtendedEntity<?> owner, Key<V> key) {
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
        public <C> Item<C> translate(ConverterPair<V, C> converterPair) {
            return new ConvertingItem<V, C>(this, converterPair);
        }
    }

    private static class ConvertingItem<A, B> extends Item<B> {
        private final Item<A> item;
        private final ConverterPair<A, B> pair;

        public ConvertingItem(Item<A> item, ConverterPair<A, B> pair) {
            super(null, null);
            this.item = item;
            this.pair = pair;
        }

        @Override
        public B get() {
            return pair.convertB(item.get());
        }

        @Override
        public void set(B value) {
            this.item.set(pair.convertA(value));
        }
        
    }
}
