package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.EntityPoseConversion;
import com.bergerkiller.bukkit.common.conversion.type.JOMLConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonDisabledEntity;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.core.Vector3fHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.DataWatcherObjectHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.DataWatcherRegistryHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * The DataWatcher tracks changes of Entity metadata and handles the creation of
 * metadata update packets. Entity metadata {@link Key} instances, which are stored
 * as static constants in {@link EntityHandle} and extended types of it, can be used
 * to get and set values assigned to them.<br>
 * <br>
 * You can create a new DataWatcher instance and begin setting up these (initial) values.
 * For better performance it is recommended to use the {@link Prototype} class builder
 * pattern, so that it minimizes the amount of times internal collections have to resize
 * and initialize.
 */
public class DataWatcher extends BasicWrapper<DataWatcherHandle> implements Cloneable {

    public DataWatcher(org.bukkit.entity.Entity entityOwner) {
        this(DataWatcherHandle.createNew(entityOwner));
    }

    /**
     * Initializes a new Empty DataWatcher. Please avoid binding this
     * constructed DataWatcher to live entities. When doing so, instead use the
     * Entity-accepting constructor.
     */
    public DataWatcher() {
        this(DataWatcherHandle.createNew(CommonDisabledEntity.INSTANCE));
    }

    /**
     * Creates a DataWatcher wrapping the NMS DataWatcher raw instance
     *
     * @param nmsDataWatcherHandle net.minecraft.network.syncher.DataWatcher
     * @return DataWatcher
     */
    public static DataWatcher createForHandle(Object nmsDataWatcherHandle) {
        return new DataWatcher(DataWatcherHandle.createHandle(nmsDataWatcherHandle));
    }

    private DataWatcher(DataWatcherHandle handle) {
        setHandle(handle);
    }

    /**
     * Sets a value bound to a DataWatcher key. If the key was not previously registered, it is
     * registered with a disabled default so this new value is always sent. If you want to omit
     * a value when it is the client default value, see {@link #setClientDefault(Key, Object)}.
     *
     * @param key DataWatcher Key
     * @param value Value to assign at this key
     */
    public <V> void set(Key<V> key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        } else if (key instanceof Key.Disabled) {
            // Pass. Do nothing.
        } else {
            handle.setRaw(key, key.getType().getConverter().convertReverse(value), false);
        }
    }

    /**
     * Write a new value to the watched objects.
     * If the key does not yet exist, the key is added with the value specified.
     * Forces a change to be sent with this value, even if the value has
     * not changed. Equivalent to doing:
     * <pre>
     *     data.set(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, 1);
     *     data.set(DisplayHandle.DATA_INTERPOLATION_START_DELTA_TICKS, 0);
     * </pre>
     *
     * @param key Object key
     * @param value Value to set to
     */
    public <V> void forceSet(Key<V> key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        } else if (key instanceof Key.Disabled) {
            // Pass. Do nothing.
        } else {
            handle.setRaw(key, key.getType().getConverter().convertReverse(value), true);
        }
    }

    /**
     * Sets a value bound to a DataWatcher key. If the key was not previously registered, it is
     * registered with a disabled default so this new value is always sent. If you want to omit
     * a value when it is the client default value, see {@link #setClientDefault(Key, Object)}.
     * This is a special overload for Byte keys that can be set using an int without a cast.
     *
     * @param key Object key
     * @param value Value to set to, is cast to a byte (0-255)
     */
    public void setByte(Key<Byte> key, int value) {
        set(key, Byte.valueOf((byte) (value & 0xFF)));
    }

    /**
     * Gets whether a flag is set
     * 
     * @param key of the flag to get
     * @param flag to get
     * @return True if the flag is set, False if not
     */
    public boolean getFlag(Key<Byte> key, int flag) {
        return (this.tryGetByte(key, 0) & flag) != 0;
    }

    /**
     * Toggles a metadata flag on or off
     * 
     * @param key of the flag to set
     * @param flag to set (can be multiple bits)
     * @param set True to set the flags, False to clear them
     * @see #setByte(Key, int)
     */
    public void setFlag(Key<Byte> key, int flag, boolean set) {
        int old_flags = this.tryGetByte(key, 0);
        int new_flags = old_flags;
        if (set) {
            new_flags |= flag; 
        } else {
            new_flags &= ~flag;
        }
        if (old_flags != new_flags) {
            this.set(key, Byte.valueOf((byte) (new_flags & 0xFF)));
        }
    }

    /**
     * Read an object from the watched objects
     *
     * @param key Object key
     * @return Object value at the key
     */
    public <V> V get(Key<V> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        } else if (key instanceof Key.Disabled) {
            return ((Key.Disabled<V>) key).getDefaultValue();
        }

        Object rawItem = getItemRawHandle(key);
        if (rawItem == null) {
            throw new IllegalArgumentException("This key is not watched in this DataWatcher");
        } else {
            Object rawValue = DataWatcherHandle.ItemHandle.T.getValue.invoke(rawItem);
            return key.getType().getConverter().convert(rawValue);
        }
    }

    /**
     * Read an object from the watched objects
     * 
     * @param key Object key
     * @return Object value at the key (0-255), -1 if the value stored is null
     */
    public int getByte(Key<Byte> key) {
        Byte result = get(key);
        return (result == null) ? -1 : result.intValue();
    }

    /**
     * Tries to get the value of a key, returns the default value if the key is not
     * registered inside this data watcher.
     * 
     * @param key to get
     * @param defaultValue
     * @return value of key, or defaultValue if the key isn't registered
     */
    public <V> V tryGet(Key<V> key, V defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        } else if (key instanceof Key.Disabled) {
            return ((Key.Disabled<V>) key).getDefaultValue();
        }

        Object rawItem = getItemRawHandle(key);
        if (rawItem == null) {
            return defaultValue;
        } else {
            Object rawValue = DataWatcherHandle.ItemHandle.T.getValue.invoke(rawItem);
            return key.getType().getConverter().convert(rawValue);
        }
    }

    /**
     * Tries to get the value of a key, returns the default value if the key is not
     * registered inside this data watcher.
     * This is a special overload for Byte keys that can be set using an int without a cast.
     * 
     * @param key to get
     * @param defaultValue, cast to a byte (0-255)
     * @return value of key, defaultValue if the key isn't registered, -1 if null
     */
    public int tryGetByte(Key<Byte> key, int defaultValue) {
        Byte result = tryGet(key, Byte.valueOf((byte) (defaultValue & 0xFF))).byteValue();
        return (result == null) ? -1 : result.byteValue();
    }

    /**
     * Gets the datawatcher item associated with a certain key
     * 
     * @param key Object key
     * @return Object item at the key, <i>null</i> if not registered
     */
    public <V> Item<V> getItem(Key<V> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }

        Object rawItem = getItemRawHandle(key);
        if (rawItem == null) {
            return null;
        } else {
            return new Item<V>(key, DataWatcherHandle.ItemHandle.createHandle(rawItem));
        }
    }

    /**
     * Sets the default value of a watched key. If the value is equal to this value, then the metadata
     * is not sent to clients when spawning the object. Call this before calling
     * {@link #set(Key, Object)} or {@link #forceSet(Key, Object)} to reduce the amount of data
     * sent in metadata packets that, to the client, are already set that way for new entities.
     *
     * @param key of the watched item
     * @param defaultValue of the watched item. If the value set is this one, then the
     *                     item is not included in synchronization packets for non-changes.
     * @see #packNonDefaults()
     */
    public <T> void setClientDefault(Key<T> key, T defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        } else if (key instanceof Key.Disabled) {
            return; // Ignore
        }

        Object defaultValueConv = key.getType().getConverter().convertReverse(defaultValue);
        handle.setRawDefault(key, defaultValueConv);
    }

    /**
     * Sets the default value of a watched key. If the value is equal to this value, then the metadata
     * is not sent to clients when spawning the object. Call this before calling
     * {@link #set(Key, Object)} or {@link #forceSet(Key, Object)} to reduce the amount of data
     * sent in metadata packets that, to the client, are already set that way for new entities.
     * This is a special overload for Byte keys that can be set using an int without a cast.
     *
     * @param key of the watched item
     * @param defaultValue of the watched item. If the value set is this one, then the
     *                     item is not included in synchronization packets for non-changes.
     * @see #packNonDefaults()
     */
    public void setClientByteDefault(Key<Byte> key, int defaultValue) {
        setClientDefault(key, Byte.valueOf((byte) (defaultValue & 0xFF)));
    }

    /**
     * Watches an object. Equivalent to just {@link #set(Key, Object)}.
     *
     * @param key of the watched item
     * @param defaultValue initial value of the watched item
     * @deprecated Use {@link #set(Key, Object)} instead for the original legacy behavior.
     *             If this was called for performance reasons, it is better to use the
     *             {@link Prototype} builder to efficiently construct new DataWatchers.
     */
    @Deprecated
    public <T> void watch(Key<T> key, T defaultValue) {
        this.set(key, defaultValue);
    }

    /**
     * Watches a single DataWatcher item. Equivalent to just {@link #set(Key, Object)}.
     * 
     * @param item to watch
     * @deprecated This method made no sense, use {@link #clone()} instead of you want to clone a DataWatcher
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void watch(Item<?> item) {
        this.set((Key<Object>) item.getKey(), item.getValue());
    }

    /**
     * Checks whether a particular key is registered for watching
     * 
     * @param key to check
     * @return True if watched, False if not
     */
    public boolean isWatched(Key<?> key) {
        return key != null && handle.read(key) != null;
    }

    /**
     * Gets a state copy of all watched items. The returned items can be modified but will
     * have no effect on this DataWatcher. To make meaningful changes, use
     * {@link #set(Key, Object)} with information derived from the items.<br>
     * <br>
     * This method is primarily useful for debugging purposes.
     *
     * @return Watched items (immutable)
     */
    public List<Item<?>> getWatchedItems() {
        List<?> itemHandles = (List<?>) DataWatcherHandle.T.getCopyOfAllItems.raw.invoke(handle.getRaw());
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
        }
        return new ConvertingList<Item<?>>(itemHandles, DuplexConversion.dataWatcherItem);
    }

    /**
     * Get all watched objects
     *
     * @param unwatch to unwatch all the items before returning them
     * @return Watched objects (immutable)
     * @deprecated Use {@link #packChanges()} instead to track changes
     */
    @Deprecated
    public List<Item<?>> getWatchedItems(boolean unwatch) {
        if (unwatch) {
            packChanges();
        }
        return getWatchedItems();
    }

    /**
     * Packs all metadata items into packed items, regardless of whether they changed or have non-default
     * values.
     *
     * @return List of all packed items
     */
    public List<PackedItem<?>> packAll() {
        List<?> itemHandles = (List<?>) DataWatcherHandle.T.packAll.raw.invoke(handle.getRaw());
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
        }
        return new ConvertingList<PackedItem<?>>(itemHandles, DuplexConversion.dataWatcherPackedItem);
    }

    /**
     * Detects all metadata items which have changed since the last time it was called, marks
     * them as not changed, and packs them into immutable PackedItem objects.
     *
     * @return List of packed items of all the changes
     */
    public List<PackedItem<?>> packChanges() {
        List<?> itemHandles = (List<?>) DataWatcherHandle.T.packChanges.raw.invoke(handle.getRaw());
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
        }
        return new ConvertingList<PackedItem<?>>(itemHandles, DuplexConversion.dataWatcherPackedItem);
    }

    /**
     * Detects all metadata items which have a non-default value. Does not mark them as changed.
     * Only reliably detects non-default values since Minecraft 1.19.3.
     * The first set/watch call sets the initial value of the metadata items.
     * So when setting the value of a new data watcher, this method will return an empty list.
     *
     * @return List of packed items of all the non-default item values
     */
    public List<PackedItem<?>> packNonDefaults() {
        List<?> itemHandles = (List<?>) DataWatcherHandle.T.packNonDefaults.raw.invoke(handle.getRaw());
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
        }
        return new ConvertingList<PackedItem<?>>(itemHandles, DuplexConversion.dataWatcherPackedItem);
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
     * Creates a clone of this DataWatcher with all data entries copied.
     * Changes to the cloned datawatcher do not result in changes in this one.
     * 
     * @return a clone of this instance
     */
    @Override
    public DataWatcher clone() {
        return new DataWatcher(handle.cloneWithOwner(CommonDisabledEntity.INSTANCE));
    }

    // Gets the raw item handle of a key
    private Object getItemRawHandle(Key<?> key) {
        if (CommonCapabilities.DATAWATCHER_OBJECTS) {
            return DataWatcherHandle.T.read.raw.invoke(this.handle.getRaw(), key.getRawHandle());
        } else {
            return DataWatcherHandle.T.read.raw.invoke(this.handle.getRaw(), key.getId());
        }
    }

    /**
     * Creates new DataWatcher instances based on a known configuration of keys and their default/initial values.
     *
     * @see #build() For creating a new Prototype
     */
    @FunctionalInterface
    public interface Prototype {
        /**
         * Creates a new DataWatcher instance with the previously set configured key defaults and values,
         * for an anonymous (none) entity. Can be used for sending metadata packets.
         *
         * @return new DataWatcher based on this prototype configuration
         */
        DataWatcher create();

        /**
         * Creates a new Builder with the initial DataWatcher configuration of this Prototype. This can be
         * used to make a new prototype with some alterations.
         *
         * @return Builder
         */
        default PrototypeBuilder modify() {
            return new PrototypeBuilder(this.create());
        }

        /**
         * Starts building a new DataWatcher Prototype configuration
         *
         * @return Builder
         */
        static PrototypeBuilder build() {
            return new PrototypeBuilder(new DataWatcher());
        }
    }

    /**
     * Builds a new DataWatcher Prototype based on keys, their client default values,
     * and initial value. After creating a Prototype with it, new DataWatchers can be
     * efficiently created that uses this configuration.
     */
    public static final class PrototypeBuilder {
        private final DataWatcher dataWatcher;
        private boolean created = false;

        private PrototypeBuilder(DataWatcher dataWatcher) {
            this.dataWatcher = dataWatcher;
        }

        /**
         * Sets the default value of a watched key. If the value is equal to this value, then the metadata
         * is not sent to clients when spawning the object. Call this before calling
         * {@link #set(Key, Object)} or {@link #forceSet(Key, Object)} to reduce the amount of data
         * sent in metadata packets that, to the client, are already set that way for new entities.
         *
         * @param key of the watched item
         * @param defaultValue of the watched item. If the value set is this one, then the
         *                     item is not included in synchronization packets for non-changes.
         * @see #packNonDefaults()
         * @return this Builder
         */
        public <T> PrototypeBuilder setClientDefault(Key<T> key, T defaultValue) {
            this.dataWatcher.setClientDefault(key, defaultValue);
            return this;
        }

        /**
         * Sets the default value of a watched key. If the value is equal to this value, then the metadata
         * is not sent to clients when spawning the object. Call this before calling
         * {@link #set(Key, Object)} or {@link #forceSet(Key, Object)} to reduce the amount of data
         * sent in metadata packets that, to the client, are already set that way for new entities.
         * This is a special overload for Byte keys that can be set using an int without a cast.
         *
         * @param key of the watched item
         * @param defaultValue of the watched item. If the value set is this one, then the
         *                     item is not included in synchronization packets for non-changes.
         * @see #packNonDefaults()
         * @return this Builder
         */
        public PrototypeBuilder setClientByteDefault(Key<Byte> key, int defaultValue) {
            this.dataWatcher.setClientByteDefault(key, defaultValue);
            return this;
        }

        /**
         * Sets a value bound to a DataWatcher key. If the key was not previously registered, it is
         * registered with a disabled default so this new value is always sent. If you want to omit
         * a value when it is the client default value, see {@link #setClientDefault(Key, Object)}.
         *
         * @param key DataWatcher Key
         * @param value Value to assign at this key
         * @return this Builder
         */
        public <T> PrototypeBuilder set(Key<T> key, T value) {
            this.dataWatcher.set(key, value);
            return this;
        }

        /**
         * Sets a value bound to a DataWatcher key. If the key was not previously registered, it is
         * registered with a disabled default so this new value is always sent. If you want to omit
         * a value when it is the client default value, see {@link #setClientDefault(Key, Object)}.
         * This is a special overload for Byte keys that can be set using an int without a cast.
         *
         * @param key Object key
         * @param value Value to set to, is cast to a byte (0-255)
         * @return this Builder
         */
        public PrototypeBuilder setByte(Key<Byte> key, int value) {
            this.dataWatcher.setByte(key, value);
            return this;
        }

        /**
         * Toggles a metadata flag on or off
         *
         * @param key of the flag to set
         * @param flag to set (can be multiple bits)
         * @param set True to set the flags, False to clear them
         * @return this Builder
         * @see #setByte(Key, int)
         */
        public PrototypeBuilder setFlag(Key<Byte> key, int flag, boolean set) {
            this.dataWatcher.setFlag(key, flag, set);
            return this;
        }

        /**
         * Creates a DataWatcher Prototype configuration based on the key-values set so
         * far. The prototype can then be used to create new DataWatcher instances.
         * This method cannot be called more than once!
         *
         * @return DataWatcher Prototype configuration
         */
        public Prototype create() {
            if (created) {
                throw new IllegalStateException("DataWatcher Prototype Builder was already used to create a new ProtoType!");
            } else {
                created = true;
            }

            // Ensure not initially changed
            dataWatcher.packChanges();

            // Use clone() to create identical copies of this prototype configuration
            return dataWatcher::clone;
        }
    }

    /**
     * References a single watched item, containing the key, value, and changed state
     * 
     * @param <V> value type of the item
     */
    public static class Item<V> extends BasicWrapper<DataWatcherHandle.ItemHandle> {
        private final Key<V> key;

        protected Item(Key<V> key, DataWatcherHandle.ItemHandle handle) {
            this.key = key;
            this.setHandle(handle);
        }

        public Item(DataWatcherHandle.ItemHandle handle) {
            this.key = null;
            this.setHandle(handle);
        }

        /**
         * Creates a new item referencing this item, but using a particular key
         * for value translation. If the key specified does not equal the key
         * of this Item, null is returned instead.
         * 
         * @param key of the item
         * @return translated item
         */
        public <W> Item<W> translate(Key<W> key) {
            if (!key.equals(this.getKey())) {
                return null;
            }
            return new Item<W>(key, this.handle);
        }

        /**
         * Gets the key associated with this DataWatcher Item.
         * Note that only internal types are available. It is recommended
         * to only use this key for comparison with existing Key constants.
         * 
         * @return Item Key
         */
        @SuppressWarnings("unchecked")
        public Key<V> getKey() {
            if (this.key != null) {
                return this.key;
            } else if (CommonCapabilities.DATAWATCHER_OBJECTS) {
                // This is for MC >= 1.9
                return (Key<V>) DataWatcherHandle.ItemHandle.T.key.get(this.handle.getRaw());
            } else {
                // This is for MC 1.8.8, where we use a proxy object storing typeId (serializer token) and keyId
                int typeId = DataWatcherHandle.ItemHandle.T.typeId.getInteger(this.handle.getRaw());
                int keyId = DataWatcherHandle.ItemHandle.T.keyId.getInteger(this.handle.getRaw());
                Object token = Integer.valueOf(typeId);
                Object handle = new com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<V>(keyId, token);
                return new Key<V>(handle);
            }
        }

        public boolean isChanged() {
            return this.handle.isChanged();
        }

        public V getValue() {
            return this.getKey().getType().getConverter().convert(this.handle.getValue());
        }

        public void setChanged(boolean changed) {
            this.handle.setChanged(changed);
        }

        public void setValue(V value, boolean changed) {
            if (this.key != null) {
                this.handle.setValue(key.getType().getConverter().convertReverse(value));
            } else {
                this.handle.setValue(value);
            }
            this.handle.setChanged(true);
        }

        @Override
        public String toString() {
            Key<V> key = getKey();
            return "{id=" + key.getId() + ",type=" + key.getType().getInternalType().getSimpleName() + 
                    ",changed=" + isChanged() + ",value=" + getValue() + "}";
        }

        /**
         * Reads the raw, internally stored value of an Item. Internal use only, it is not recommended
         * to use this in your own code because of constantly changing internal representations.
         * 
         * @param item to get the raw value of
         * @return raw value
         */
        public static Object getRawValue(Item<?> item) {
            return item.handle.getValue();
        }

        /**
         * Creates a snapshot of the current metadata value and packs it into a PackedItem.
         * The packed item can be used in metadata update packets.
         *
         * @return immutable packed item
         */
        public PackedItem<V> pack() {
            return new PackedItem<V>(handle.pack(), key);
        }
    }

    /**
     * A single data watcher metadata value that was packed for transport inside a metadata packet.
     * Contents are immutable.
     *
     * @param <V> Value type of the item
     */
    public static class PackedItem<V> extends BasicWrapper<DataWatcherHandle.PackedItemHandle> {
        private final Key<V> key;

        private PackedItem(DataWatcherHandle.PackedItemHandle handle, Key<V> key) {
            this.setHandle(handle);
            this.key = key;
        }

        /**
         * Gets a PackedItem from a NMS handle instance
         *
         * @param nmsPackedItemHandle Handle
         * @return PackedItem
         */
        public static <T> PackedItem<T> fromHandle(Object nmsPackedItemHandle) {
            return new PackedItem<T>(DataWatcherHandle.PackedItemHandle.createHandle(nmsPackedItemHandle), null);
        }

        /**
         * Gets the value of this packed item. If translated using {@link #translate(Key)}
         * with a matching key, the value is automatically converted to something legible.
         * Otherwise, the raw internal data type is returned.
         *
         * @return value
         */
        @SuppressWarnings("unchecked")
        public V value() {
            if (key != null) {
                return key.getType().getConverter().convert(handle.value());
            } else {
                return (V) handle.value();
            }
        }

        /**
         * Creates a new PackedItem instance with the value changed, but everything else kept the same.
         * Can be used to change the value of a metadata packet.
         *
         * @param value New value to set
         * @return cloned packed item with value changed
         */
        public PackedItem<V> cloneWithValue(V value) {
            if (key != null) {
                return new PackedItem<V>(handle.cloneWithValue(key.getType().getConverter().convertReverse(value)), key);
            } else {
                return new PackedItem<V>(handle.cloneWithValue(value), null);
            }
        }

        /**
         * Creates a new item referencing this item, but using a particular key
         * for value translation. If the key specified can not be used with this
         * packed item, null is returned instead.
         * 
         * @param key of the packed item
         * @return translated packed item, or null if incompatible
         * @see #isForKey(Key)
         */
        public <W> PackedItem<W> translate(Key<W> key) {
            return isForKey(key) ? new PackedItem<W>(this.handle, key) : null;
        }

        /**
         * Checks whether this packed item corresponds to something packed for the key specified.
         * If true, the key can be used with {@link #translate(Key)} to convert it to a
         * readable value.
         *
         * @param key Key to check
         * @return True if the packed item can be used with the key
         */
        public boolean isForKey(Key<?> key) {
            return handle.isForKey(key);
        }
    }

    /**
     * References the value bound to a DataWatcher Key for a particular Entity
     *
     * @param <V> value type bound to the key
     */
    public static class EntityItem<V> {
        private final Key<V> key;
        private final DataWatcherHandle datawatcher;

        public EntityItem(ExtendedEntity<?> owner, Key<V> key) {
            this.key = key;
            this.datawatcher = DataWatcherHandle.createHandle(EntityHandle.T.datawatcherField.raw.get(owner.getHandle()));
        }

        /**
         * Gets the value of this DataWatcher metadata property
         * 
         * @return current value
         */
        @SuppressWarnings("unchecked")
        public V get() {
            return (V) this.datawatcher.get(this.key);
        }

        /**
         * Sets a new value for this DataWatcher metadata property. Watchers will be notified.
         * 
         * @param value to set to
         */
        public void set(V value) {
            this.datawatcher.setRaw(key, key.getType().getConverter().convertReverse(value), false);
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

    /**
     * Wrapper around a raw DataWatcher key object
     * 
     * @param <V> value type bound to the key
     */
    public static class Key<V> extends BasicWrapper<DataWatcherObjectHandle> {
        private final Type<V> _serializer;

        protected Key(Type<V> serializer) {
            this._serializer = serializer;
        }

        public Key(Object handle) {
            this(handle, null);
        }

        public Key(Object handle, Type<V> serializer) {
            setHandle(DataWatcherObjectHandle.createHandle(handle));
            Object token = this.handle.getSerializer();
            DataWatcherSerializers.InternalType internalType = DataWatcherSerializers.getInternalTypeFromToken(token);

            if (internalType != null) {
                if (serializer == null) {
                    serializer = Type.getForType(LogicUtil.unsafeCast(internalType.type));
                }

                this._serializer = serializer.setInternalOptional(internalType.optional);
            } else if (serializer != null) {
                this._serializer = serializer;
            } else {
                this._serializer = new Type<V>(token, DuplexConverter.createNull(TypeDeclaration.OBJECT));
            }
        }

        /**
         * Gets the Serializer used when storing/restoring values bound to this key from the DataWatcher
         *
         * @return serializer
         */
        public Type<V> getType() {
            return this._serializer;
        }

        /**
         * Gets the value type that is internally stored under this Key
         *
         * @return internal key type
         */
        public Class<?> getInternalType() {
            return this._serializer.getInternalType();
        }

        /**
         * Gets the unique global serializer Id of this key.
         * This id is unique for this data value type.
         *
         * @return Serializer Id
         */
        public int getSerializerId() {
            Object rawSerializer = this.handle.getSerializer();
            return DataWatcherRegistryHandle.T.getSerializerId.invoke(rawSerializer);
        }

        /**
         * Gets the datawatcher object Id of this key
         *
         * @return Id
         */
        public int getId() {
            return DataWatcherObjectHandle.T.getId.invoke(this.handle.getRaw());
        }

        @Override
        public int hashCode() {
            return this.getId();
        }

        @Override
        public String toString() {
            return "{id=" + getId() + ", type=" + getType().getInternalType() + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof Key)) {
                return false;
            } else {
                Key<?> other = (Key<?>) o;
                if (this.getId() != other.getId()) {
                    return false;
                }
                if (this.getInternalType() != other.getInternalType()) {
                    return false;
                }
                return true;
            }
        }

        /**
         * Stores Key Type Serialization information to enable conversion from/to the internally stored type.
         */
        public static class Type<T> {
            private static final HashMap<Class<?>, Type<?>> byTypeMapping = new HashMap<Class<?>, Type<?>>();
            private final Object _token;
            private final DuplexConverter<Object, T> _converter;
            private Type<T> _optional_opposite;

            // Placeholder for Types that are missing (on this version of the server)
            // Keys created using this type behave like a no-op and always return null.
            private static final Type<Object> MISSING_TYPE = new Type<Object>() {
                private final Key<Object> disabled_key = new Key.Disabled<>(this);

                @Override
                public Key<Object> createKey(Template.StaticField.Converted<? extends Key<?>> tokenField, int alternativeId) {
                    return disabled_key;
                }

                @Override
                public <C> Type<C> translate(DuplexConverter<Object, C> converter) {
                    return LogicUtil.unsafeCast(this);
                }
            };

            // All below serializers are guaranteed to always be available
            public static final Type<Boolean> BOOLEAN = getForType(Boolean.class);
            public static final Type<Byte> BYTE = getForType(Byte.class);
            public static final Type<Integer> INTEGER = getForType(Integer.class);
            public static final Type<Float> FLOAT = getForType(Float.class);
            public static final Type<String> STRING = getForType(String.class);
            public static final Type<Vector> ROTATION_VECTOR = new Type<>(Vector3fHandle.T.getType(), Vector.class);
            public static final Type<Vector> JOML_VECTOR3F = CommonBootstrap.evaluateMCVersion(">=", "1.21.11")
                    ? new Type<>(JOMLConversion.JOML_VECTOR3F_CONSTANT_TYPE, Vector.class)
                    : (CommonBootstrap.evaluateMCVersion(">=", "1.19.4")
                        ? new Type<>(JOMLConversion.JOML_VECTOR3F_TYPE, Vector.class)
                        : missing());
            public static final Type<Quaternion> JOML_QUATERNIONF = CommonBootstrap.evaluateMCVersion(">=", "1.21.11")
                    ? new Type<>(JOMLConversion.JOML_QUATERNIONF_CONSTANT_TYPE, Quaternion.class)
                    : (CommonBootstrap.evaluateMCVersion(">=", "1.19.4")
                        ? new Type<>(JOMLConversion.JOML_QUATERNIONF_TYPE, Quaternion.class)
                        : missing());
            public static final Type<EntityPose> ENTITY_POSE = CommonBootstrap.evaluateMCVersion(">=", "1.14")
                    ? new Type<>(EntityPoseConversion.NMS_ENTITY_POSE_TYPE, EntityPose.class) : missing();
            public static final Type<IntVector3> BLOCK_POSITION = getForType(IntVector3.class);
            public static final Type<ChatText> CHAT_TEXT = getForType(ChatText.class);
            public static final Type<ItemStack> ITEMSTACK = getForType(ItemStack.class);
            public static final Type<BlockFace> DIRECTION = getForType(BlockFace.class);
            public static final Type<java.util.OptionalInt> ENTITY_ID = new Type<java.util.OptionalInt>(INTEGER._token, DataWatcherSerializers.ENTITY_ID_TYPE_CONVERTER);
            public static final Type<BoatWoodType> BOAT_WOOD_TYPE = new Type<BoatWoodType>(INTEGER._token, DataWatcherSerializers.BOAT_WOOD_TYPE_CONVERTER);
            public static final Type<Integer> SLIME_SIZE_TYPE = CommonCapabilities.DATAWATCHER_OBJECTS
                    ? INTEGER : new Type<Integer>(BYTE._token, DataWatcherSerializers.SLIME_SIZE_CONVERTER);
            public static final Type<BlockData> BLOCK_DATA = CommonCapabilities.HAS_BLOCKDATA_METADATA
                    ? getForType(BlockData.class) : missing();
            public static final Type<ItemDisplayMode> ITEM_DISPLAY_MODE = BYTE.translate(ItemDisplayMode.class);
            public static final Type<Brightness> DISPLAY_BRIGHTNESS = INTEGER.translate(Brightness.class);

            // Used by missing() only
            private Type() {
                this._token = null;
                this._converter = new DuplexConverter<Object, T>(Object.class, Object.class) {
                    @Override
                    public T convertInput(Object value) {
                        return null;
                    }

                    @Override
                    public Object convertOutput(Object value) {
                        return null;
                    }
                };
            }

            private Type(Object token, DuplexConverter<Object, T> converter) {
                if (!CommonCapabilities.DATAWATCHER_OBJECTS && !(token instanceof Integer)) {
                    throw new IllegalArgumentException("Legacy type serializer tokens must be Integers!");
                }
                this._token = token;
                this._converter = converter;
            }

            private Type(Class<?> internalType, Class<T> externalType) {
                DataWatcherSerializers.ConvertedToken<T> convToken = DataWatcherSerializers.getConvertedSerializerToken(internalType, externalType);
                this._token = convToken.token;
                this._converter = convToken.converter;
            }

            /**
             * Searches and then uses a duplex converter to translate the type exposed to the API.
             *
             * @param exposedType that is used
             * @return translated type
             */
            @SuppressWarnings("unchecked")
            public <C> Type<C> translate(Class<C> exposedType) {
                return (Type<C>) translate(TypeDeclaration.fromClass(exposedType));
            }

            /**
             * Searches and then uses a duplex converter to translate the type exposed to the API.
             *
             * @param exposedType that is used
             * @return translated type
             */
            @SuppressWarnings("unchecked")
            public Type<?> translate(TypeDeclaration exposedType) {
                return translate((DuplexConverter<T, ?>) Conversion.findDuplex(this._converter.output, exposedType));
            }

            /**
             * Uses a duplex converter to translate the type exposed to the API.
             *
             * @param converter to use
             * @return translated Type
             */
            public <C> Type<C> translate(DuplexConverter<T, C> converter) {
                // Merge this converter with the one specified, completing the chain
                final DuplexConverter<Object, T> ca = this._converter;
                final DuplexConverter<T, C> cb = converter;
                return new Type<C>(this._token, new DuplexConverter<Object, C>(ca.input, cb.output) {
                    @Override
                    public C convertInput(Object value) {
                        T ca_output = ca.convertInput(value);
                        return (ca_output == null) ? null : cb.convertInput(ca_output);
                    }

                    @Override
                    public Object convertOutput(C value) {
                        T cb_input = cb.convertOutput(value);
                        return (cb_input == null) ? null : ca.convertOutput(cb_input);
                    }
                });
            }

            /**
             * Sets whether the internal type representation is Optional
             *
             * @param optional
             * @return modified type, if needed
             */
            private Type<T> setInternalOptional(boolean optional) {
                boolean selfIsOptional = (this._converter instanceof DataWatcherSerializers.OptionalDuplexConverter);
                if (selfIsOptional == optional) {
                    return this; // Already correct
                }

                // Cache this to better deal with repeated calls
                if (this._optional_opposite == null) {
                    if (selfIsOptional) {
                        this._optional_opposite = new Type<T>(this._token, ((DataWatcherSerializers.OptionalDuplexConverter<T>) this._converter).getBase());
                    } else {
                        this._optional_opposite = new Type<T>(this._token, new DataWatcherSerializers.OptionalDuplexConverter<T>(this._converter));
                    }
                }
                return this._optional_opposite;
            }

            /**
             * Creates a new Serializer Key. Only for internal use.
             *
             * @param tokenField    the field where the internal Key Handle can be read from
             * @param alternativeId to use when the field is unavailable (MC 1.8.8)
             * @return Key for accessing the item in the DataWatcher
             */
            public Key<T> createKey(Template.StaticField.Converted<? extends Key<?>> tokenField, int alternativeId) {
                if (CommonCapabilities.DATAWATCHER_OBJECTS) {
                    if (!tokenField.isAvailable()) {
                        if (alternativeId != -1) {
                            // This can not be! It should really exist if it also existed on 1.8.x...
                            Logging.LOGGER_REGISTRY.warning("DataWatcher key not found: " + tokenField.getElementName());
                        }
                        return new Key.Disabled<T>(this);
                    } else {
                        return new Key<T>(tokenField.raw.get(), this);
                    }
                } else if (alternativeId != -1) {
                    Object handle;
                    handle = new com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<T>(alternativeId, this._token);
                    return new Key<T>(handle, this);
                } else {
                    return new Key.Disabled<T>(this);
                }
            }

            /**
             * Gets the Serializer Token Object that is used as a key to identify this Serializer internal type.
             * On >= MC 1.10.2 this points to an internal Serializer type, on MC 1.8.8 it is the Type Id Integer.
             *
             * @return serializer key object
             */
            public Object getToken() {
                return this._token;
            }

            /**
             * Retrieves the duplex converter used to convert from/to the internally stored type
             *
             * @return duplex converter
             */
            public DuplexConverter<Object, T> getConverter() {
                return this._converter;
            }

            /**
             * Gets the type that is internally stored
             *
             * @return internal type
             */
            public Class<?> getInternalType() {
                return this._converter.input.type;
            }

            /**
             * Gets the type that is externally used (as input / output of getters / setters)
             *
             * @return external type
             */
            public Class<?> getExternalType() {
                return this._converter.output.type;
            }

            @Override
            public String toString() {
                return "Type{internal=" + this._converter.input.toString(true) + ", " +
                        "external=" + this._converter.output + "}";
            }

            /**
             * Retrieves the Serializer that is used to externally expose a particular type.
             *
             * @param externalType that is being serialized/deserialized from/to.
             * @return serializer, or null if not found
             */
            @SuppressWarnings("unchecked")
            public static <V> Type<V> getForType(Class<V> externalType) {
                Type<?> result = byTypeMapping.get(externalType);
                if (result == null) {
                    Class<?> internalType = DataWatcherSerializers.getInternalType(externalType);
                    if (internalType == null) {
                        throw new IllegalArgumentException("Object of type " + externalType.getName() + " can not be stored in a DataWatcher");
                    }
                    result = new Type<V>(internalType, externalType);
                    byTypeMapping.put(externalType, result);
                }
                return (Type<V>) result;
            }

            /**
             * Gets the MISSING type. This is for types that don't exist at runtime.
             * Keys created using this type don't do anything, and always return null.
             *
             * @param <T> Type of value (unused)
             * @return Missing Type
             */
            public static <T> Type<T> missing() {
                return LogicUtil.unsafeCast(MISSING_TYPE);
            }
        }

        /**
         * A disabled key for a datawatcher object that doesn't actually exist anymore.
         * Attempts to set a datawatcher value of this key will result in a No-Op.
         * Getting will return the default value for the value type (0, false, null).
         *
         * @param <T> key type
         */
        public static final class Disabled<T> extends Key<T> {
            private final T _defaultValue;

            public Disabled(com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key.Type<T> keyType) {
                super(keyType);

                Object internalValue = null;
                Class<?> type = LogicUtil.getUnboxedType(this.getType().getInternalType());
                if (type != null) {
                    internalValue = BoxedType.getDefaultValue(type);
                }
                this._defaultValue = this.getType().getConverter().convert(internalValue);
            }

            /**
             * Gets the default value that will be returned by get calls
             *
             * @return default value
             */
            public T getDefaultValue() {
                return this._defaultValue;
            }
        }
    }
}
