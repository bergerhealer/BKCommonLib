package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonDisabledEntity;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherObjectHandle;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherRegistryHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

import java.util.List;
import java.util.logging.Level;

/**
 * This class is a wrapper of the DataWatcher class from CraftBukkit<br>
 * It is used to store data and to keep track of changes so they can be
 * synchronized
 */
public class DataWatcher extends BasicWrapper<DataWatcherHandle> {

    public DataWatcher(org.bukkit.entity.Entity entityOwner) {
        setHandle(DataWatcherHandle.createNew(entityOwner));
    }

    /**
     * Initializes a new Empty DataWatcher. Please avoid binding this
     * constructed DataWatcher to live entities. When doing so, instead use the
     * Entity-accepting constructor.
     */
    public DataWatcher() {
        setHandle(DataWatcherHandle.createNew(CommonDisabledEntity.INSTANCE));
    }

    public DataWatcher(Object handle) {
        setHandle(DataWatcherHandle.createHandle(handle));
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
            handle.set(key, value);
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
        Item<?> item = handle.read(key);
        if (item == null) {
            throw new IllegalArgumentException("This key is not watched in this DataWatcher");
        } else {
            return (V) item.getValue();
        }
    }

    /**
     * Watch an object
     *
     * @param key of the watched item
     * @param defaultValue of the watched item
     */
    public <T> void watch(Key<T> key, T defaultValue) {
        handle.register(key, defaultValue);
    }

    /**
     * Checks whether a particular key is registered for watching
     * 
     * @param key to check
     * @return True if watched, False if not
     */
    public boolean isWatched(Key<?> key) {
        return handle.read(key) != null;
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
        List<?> itemHandles;
        if (unwatch) {
            itemHandles = (List<?>) DataWatcherHandle.T.unwatchAndReturnAllWatched.raw.invoke(handle.getRaw());
        } else {
            itemHandles = (List<?>) DataWatcherHandle.T.returnAllWatched.raw.invoke(handle.getRaw());
        }
        return new ConvertingList<Item<?>>(itemHandles, DuplexConversion.dataWatcherItem);
    }

    /**
     * Gets whether this Data Watcher has changed since the last tick
     *
     * @return True if it had changed, False if not
     */
    public boolean isChanged() {
        return handle.isChanged();
    }

    /**
     * Gets whether this Data Watcher is empty or not. An empty Data Watcher
     * does not require any update messages to the players.
     *
     * @return True if empty, False if not
     */
    public boolean isEmpty() {
        return handle.isEmpty();
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
    public static class Key<V> extends BasicWrapper<DataWatcherObjectHandle> {

        public Key(Object handle) {
            setHandle(DataWatcherObjectHandle.createHandle(handle));
        }

        /**
         * Gets the unique global serializer Id of this key.
         * This id is unique for this data value type.
         * Always returns -1 on MC 1.8.8.
         * 
         * @return Serializer Id
         */
        public int getSerializerId() {
            if (DataWatcherObjectHandle.T.getSerializer.isAvailable() && DataWatcherRegistryHandle.T.isAvailable()) {
                Object s = DataWatcherObjectHandle.T.getSerializer.invoke(this.handle.getRaw());
                return DataWatcherRegistryHandle.getSerializerId(s);
            } else {
                return -1;
            }
        }

        /**
         * Gets the datawatcher object Id of this key
         * 
         * @return Id
         */
        public int getId() {
            return DataWatcherObjectHandle.T.getId.invoke(this.handle.getRaw());
        }

        /**
         * Reads a datawatcher key from a static field value declared in a (net.minecraft.server) class
         * 
         * @param template for the class where the field is defined
         * @param fieldname of the datawatcher key
         * @return datawatcher key
         */
        public static <T> Key<T> fromStaticField(ClassTemplate<?> template, String fieldname) {
            return new DataWatcher.Key<T>(template.getStaticFieldValue(fieldname, DataWatcherObjectHandle.T.getType()));
        }

        /**
         * Reads a datawatcher key from a template static field. For MC 1.8.8, an alternative method wrapping
         * the datawatcher field "ID" is included.
         * 
         * @param field
         * @param alternativeId alternative Id for MC 1.8.8
         * @return datawatcher key
         */
        public static <T> Key<T> fromTemplate(Template.StaticField.Converted<Key<T>> field, int alternativeId) {
            if (field.isAvailable()) {
                return field.get();
            } else if (Common.evaluateMCVersion("<=", "1.8.8")) {
                // For MC 1.8.8 we have a fallback wrapping our ID in a custom implementation
                Object dwo = new com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<T>(alternativeId);
                return new Key<T>(dwo);
            } else {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find datawatcher key constant", new RuntimeException());
                return null;
            }
        }
    }

    /**
     * References a single watched item, containing the key, value, and changed state
     * 
     * @param <V> value type of the item
     */
    public static class Item<V> extends BasicWrapper<DataWatcherHandle.ItemHandle> {

        public Item(Object handle) {
            setHandle(DataWatcherHandle.ItemHandle.createHandle(handle));
        }

        @SuppressWarnings("unchecked")
        public Key<V> getKey() {
            return (Key<V>) this.handle.getKey();
        }

        public boolean isChanged() {
            return this.handle.isChanged();
        }

        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) this.handle.getValue();
        }

        public void setValue(V value, boolean changed) {
            this.handle.setValue(value);
            this.handle.setChanged(true);
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
        private final Key<?> key;

        public EntityItem(ExtendedEntity<?> owner, Key<V> key) {
            this.owner = owner;
            this.key = key;
        }

        /**
         * Gets the value of this DataWatcher metadata property
         * 
         * @return current value
         */
        @SuppressWarnings("unchecked")
        public V get() {
            Object dataWatcher = EntityHandle.T.datawatcherField.raw.get(owner.getHandle());
            return (V) DataWatcherHandle.createHandle(dataWatcher).get(this.key);
        }

        /**
         * Sets a new value for this DataWatcher metadata property. Watchers will be notified.
         * 
         * @param value to set to
         */
        public void set(V value) {
            Object dataWatcher = EntityHandle.T.datawatcherField.raw.get(owner.getHandle());
            DataWatcherHandle.createHandle(dataWatcher).set(this.key, value);
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
