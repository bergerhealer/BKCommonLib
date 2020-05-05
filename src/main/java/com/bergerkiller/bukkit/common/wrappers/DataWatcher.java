package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonDisabledEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherObjectHandle;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherRegistryHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumDirectionHandle;
import com.bergerkiller.generated.net.minecraft.server.IChatBaseComponentHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * This class is a wrapper of the DataWatcher class from CraftBukkit<br>
 * It is used to store data and to keep track of changes so they can be
 * synchronized
 */
public class DataWatcher extends BasicWrapper<DataWatcherHandle> implements Cloneable {

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
     * If the key does not yet exist, the key is added with the value specified.
     *
     * @param key Object key
     * @param value Value to set to
     */
    public <V> void set(Key<V> key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        } else if (key instanceof Key.Disabled) {
            // Pass. Do nothing.
        } else {
            // Note: throws a NPE when the key is not watched inside the datawatcher
            // When this occurs, and after verifying it is indeed not watched, watch() it instead.
            // This preserves performance of the most common set case
            try {
                handle.set(key, value);
            } catch (NullPointerException ex) {
                if (isWatched(key)) {
                    throw ex;
                } else {
                    watch(key, value);
                }
            }
        }
    }

    /**
     * Write a new value to the watched objects.
     * If the key does not yet exist, the key is added with the value specified.
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
     * Watches an object
     *
     * @param key of the watched item
     * @param defaultValue of the watched item
     */
    public <T> void watch(Key<T> key, T defaultValue) {
        handle.register(key, defaultValue);
    }

    /**
     * Watches a single DataWatcher item
     * 
     * @param item to watch
     */
    @SuppressWarnings("unchecked")
    public void watch(Item<?> item) {
        this.watch((Key<Object>) item.getKey(), item.getValue());
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
     * Get all watched items
     *
     * @return Watched items (immutable)
     */
    public List<Item<?>> getWatchedItems() {
        return getWatchedItems(false);
    }

    /**
     * Get all watched objects
     *
     * @param unwatch to unwatch all the items before returning them
     * @return Watched objects (immutable)
     */
    public List<Item<?>> getWatchedItems(boolean unwatch) {
        List<?> itemHandles;
        if (unwatch) {
            itemHandles = (List<?>) DataWatcherHandle.T.unwatchAndReturnAllWatched.raw.invoke(handle.getRaw());
        } else {
            itemHandles = (List<?>) DataWatcherHandle.T.returnAllWatched.raw.invoke(handle.getRaw());
        }
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
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
     * Creates a clone of this DataWatcher with all data entries copied.
     * Changes to the cloned datawatcher do not result in changes in this one.
     * 
     * @return a clone of this instance
     */
    @Override
    public DataWatcher clone() {
        DataWatcher clone = new DataWatcher();
        for (Item<?> item : this.getWatchedItems()) {
            clone.watch(item);
        }
        return clone;
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
     * References a single watched item, containing the key, value, and changed state
     * 
     * @param <V> value type of the item
     */
    public static class Item<V> extends BasicWrapper<DataWatcherHandle.ItemHandle> {
        private Key<V> key;

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

        /**
         * Clones this DataWatcher Item, making sure changes to it does not affect the DataWatcher
         */
        public Item<V> clone() {
            return new Item<V>(this.key, this.handle.cloneHandle());
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
    }

    /**
     * References the value bound to a DataWatcher Key for a particular Entity
     *
     * @param <V> value type bound to the key
     */
    public static class EntityItem<V> {
        private final ExtendedEntity<?> owner;
        private final Key<V> key;

        public EntityItem(ExtendedEntity<?> owner, Key<V> key) {
            this.owner = owner;
            this.key = key;
        }

        /**
         * Gets the value of this DataWatcher metadata property
         * 
         * @return current value
         */
        public V get() {
            Object dataWatcher = EntityHandle.T.datawatcherField.raw.get(owner.getHandle());
            return DataWatcherHandle.createHandle(dataWatcher).get(this.key);
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
            DataSerializerRegistry.InternalType internalType = DataSerializerRegistry.getInternalTypeFromToken(token);

            if (internalType == null) {
                throw new RuntimeException("Token serializer not found: " + token);
            }
            if (serializer == null) {
                serializer = Type.getForType(CommonUtil.unsafeCast(internalType.type));
            }

            this._serializer = serializer.setInternalOptional(internalType.optional);
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

            // All below serializers are guaranteed to always be available
            public static final Type<Boolean> BOOLEAN = getForType(Boolean.class);
            public static final Type<Byte> BYTE = getForType(Byte.class);
            public static final Type<Integer> INTEGER = getForType(Integer.class);
            public static final Type<Float> FLOAT = getForType(Float.class);
            public static final Type<String> STRING = getForType(String.class);
            public static final Type<Vector> VECTOR = getForType(Vector.class);
            public static final Type<IntVector3> BLOCK_POSITION = getForType(IntVector3.class);
            public static final Type<ChatText> CHAT_TEXT = getForType(ChatText.class);
            public static final Type<ItemStack> ITEMSTACK = getForType(ItemStack.class);
            public static final Type<BlockFace> DIRECTION = getForType(BlockFace.class);
            public static final Type<java.util.OptionalInt> ENTITY_ID = new Type<java.util.OptionalInt>(INTEGER._token, new EntityIdTypeConverter());

            private Type(Object token, DuplexConverter<Object, T> converter) {
                if (!CommonCapabilities.DATAWATCHER_OBJECTS && !(token instanceof Integer)) {
                    throw new IllegalArgumentException("Legacy type serializer tokens must be Integers!");
                }
                this._token = token;
                this._converter = converter;
            }

            @SuppressWarnings("unchecked")
            private Type(Class<?> internalType, Class<T> externalType) {
                boolean optional = false;
                Object token = DataSerializerRegistry.getSerializerToken(internalType, optional);
                if (token == null) {
                    // Try optional
                    optional = true;
                    token = DataSerializerRegistry.getSerializerToken(internalType, optional);
                }
                if (token == null) {
                    throw new RuntimeException("No token found for internal type " + internalType.getName());
                }
                if (!CommonCapabilities.DATAWATCHER_OBJECTS && !(token instanceof Integer)) {
                    throw new RuntimeException("Legacy type serializer tokens must be Integers!");
                }

                this._token = token;

                DuplexConverter<Object, T> converter = Conversion.findDuplex((Class<Object>) internalType, externalType);
                if (converter == null) {
                    throw new RuntimeException("Failed to find converter from internal type " + 
                            internalType.getName() + " to " + externalType.getName());
                }

                if (optional) {
                    converter = new OptionalDuplexConverter<T>(converter);
                }

                this._converter = converter;
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
            public Type<T> setInternalOptional(boolean optional) {
                boolean selfIsOptional = (this._converter instanceof OptionalDuplexConverter);
                if (selfIsOptional == optional) {
                    return this; // Already correct
                }

                // Cache this to better deal with repeated calls
                if (this._optional_opposite == null) {
                    if (selfIsOptional) {
                        this._optional_opposite = new Type<T>(this._token, ((OptionalDuplexConverter<T>) this._converter)._baseConverter);
                    } else {
                        this._optional_opposite = new Type<T>(this._token, new OptionalDuplexConverter<T>(this._converter));
                    }
                }
                return this._optional_opposite;
            }

            /**
             * Creates a new Serializer Key. Only for internal use.
             * 
             * @param tokenField the field where the internal Key Handle can be read from
             * @param alternativeId to use when the field is unavailable (MC 1.8.8)
             * @return Key for accessing the item in the DataWatcher
             */
            public Key<T> createKey(Template.StaticField.Converted<? extends Key<?>> tokenField, int alternativeId) {
                if (CommonCapabilities.DATAWATCHER_OBJECTS) {
                    if (!tokenField.isAvailable()) {
                        if (alternativeId != -1) {
                            // This can not be! It should really exist if it also existed on 1.8.x...
                            System.err.println("DataWatcher key not found: " + tokenField.getElementName());
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
                    Class<?> internalType = DataSerializerRegistry.getInternalType(externalType);
                    if (internalType == null) {
                        throw new IllegalArgumentException("Object of type " + externalType.getName() + " can not be stored in a DataWatcher");
                    }
                    result = new Type<V>(internalType, externalType);
                    byTypeMapping.put(externalType, result);
                }
                return (Type<V>) result;
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

        /**
         * Used to convert between Integer and Optional<Integer>.
         * Internally it stores the int value incremented or decremented by one, to allow for 0 as 'not set'
         */
        private static final class EntityIdTypeConverter extends DuplexConverter<Object, java.util.OptionalInt> {

            public EntityIdTypeConverter() {
                super(Integer.class, java.util.OptionalInt.class);
            }

            @Override
            public java.util.OptionalInt convertInput(Object value) {
                if (value instanceof Integer) {
                    int intValue = ((Integer) value).intValue();
                    if (intValue > 0) {
                        return java.util.OptionalInt.of(intValue - 1);
                    }
                }
                return java.util.OptionalInt.empty();
            }

            @Override
            public Object convertOutput(java.util.OptionalInt value) {
                if (value != null && value.isPresent()) {
                    return Integer.valueOf(value.getAsInt() + 1);
                } else {
                    return Integer.valueOf(0);
                }
            }

            @Override
            public boolean acceptsNullInput() {
                return true;
            }

            @Override
            public boolean acceptsNullOutput() {
                return true;
            }
        }
    }

    // Stores the internal type Serializer mapping, and how exposed types (IntVector3) are internally stored (BlockPosition)
    private static class DataSerializerRegistry {
        private static final HashMap<Object, InternalType> tokenRegistryRev = new HashMap<Object, InternalType>();
        private static final HashMap<Class<?>, Object> tokenRegistry = new HashMap<Class<?>, Object>();
        private static final HashMap<Class<?>, Object> tokenRegistry_optional = new HashMap<Class<?>, Object>();
        private static final HashMap<Class<?>, Class<?>> typeMapping = new HashMap<Class<?>, Class<?>>();

        static {
            Class<?> registryClass = CommonUtil.getNMSClass("DataWatcherRegistry");
            Class<?> serializerClass = CommonUtil.getNMSClass("DataWatcherSerializer");
            if (registryClass != null && serializerClass != null) {
                // Since MC 1.9
                for (Field f : registryClass.getDeclaredFields()) {
                    if (f.getType().equals(serializerClass) && Modifier.isStatic(f.getModifiers())) {
                        try {
                            if (!(Modifier.isPublic(f.getModifiers()))) {
                                f.setAccessible(true);
                            }
                            TypeDeclaration typeDec = TypeDeclaration.fromType(f.getGenericType());
                            if (typeDec.genericTypes.length == 1) {
                                TypeDeclaration dataType = typeDec.genericTypes[0];

                                // Sometimes google Optional is used to wrap null values. We aren't interested in that ourselves.
                                boolean isOptional = CommonNMS.isDWROptionalType(dataType.type) && (dataType.genericTypes.length == 1);
                                if (isOptional) {
                                    dataType = dataType.genericTypes[0];
                                }

                                // Store in map for future use, mapped to the serializer instance
                                register(dataType.type, f.get(null), isOptional);
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }

                // ChatText -> IChatbaseComponent
                typeMapping.put(ChatText.class, IChatBaseComponentHandle.T.getType());

                // Bukkit BlockFace -> nms EnumDirection
                typeMapping.put(BlockFace.class, EnumDirectionHandle.T.getType());
            } else {
                // Use our own kind of tokens on MC 1.8.8 and before
                register(Byte.class, 0);
                register(Short.class, 1);
                register(Integer.class, 2);
                register(Float.class, 3);
                register(String.class, 4);
                register(ItemStackHandle.T.getType(), 5);
                register(BlockPositionHandle.T.getType(), 6);
                register(CommonUtil.getNMSClass("Vector3f"), 7);

                // Booleans are stored as Byte
                typeMapping.put(Boolean.class, Byte.class);

                // IChatBaseComponent -> String
                typeMapping.put(IChatBaseComponentHandle.T.getType(), String.class);

                // ChatText -> String
                typeMapping.put(ChatText.class, String.class);

                // Bukkit BlockFace -> int (not really used anywhere)
                typeMapping.put(BlockFace.class, Integer.class);
            }

            // Add all type mappings to self
            for (Class<?> type : tokenRegistry.keySet()) {
                typeMapping.put(type, type);
            }
            for (Class<?> type : tokenRegistry_optional.keySet()) {
                typeMapping.put(type, type);
            }

            // Vector -> Vector3f
            typeMapping.put(Vector.class, CommonUtil.getNMSClass("Vector3f"));
            // IntVector3 -> BlockPosition
            typeMapping.put(IntVector3.class, BlockPositionHandle.T.getType());
            // Bukkit ItemStack -> nms ItemStack
            typeMapping.put(ItemStack.class, ItemStackHandle.T.getType());
        }

        private static void register(Class<?> type, Object token) {
            register(type, token, false);
        }

        private static void register(Class<?> type, Object token, boolean optional) {
            register(new InternalType(token, type, optional));
        }

        private static void register(InternalType type) {
            if (type.optional) {
                tokenRegistry_optional.put(type.type, type.token);
            } else {
                tokenRegistry.put(type.type, type.token);
            }
            tokenRegistryRev.put(type.token, type);
        }

        public static Class<?> getInternalType(Class<?> exposedType) {
            return typeMapping.get(exposedType);
        }

        public static Object getSerializerToken(Class<?> type, boolean optional) {
            return (optional ? tokenRegistry_optional : tokenRegistry).get(type);
        }

        public static InternalType getInternalTypeFromToken(Object token) {
            return tokenRegistryRev.get(token);
        }

        public static final class InternalType {
            public final Object token;
            public final Class<?> type;
            public final boolean optional;

            private InternalType(Object token, Class<?> type, boolean optional) {
                this.token = token;
                this.type = type;
                this.optional = optional;
            }

            @Override
            public String toString() {
                String s = this.type.getName();
                if (this.optional) {
                    s = "Optional<" + s + ">";
                }
                return s + ":" + this.token;
            }
        }
    }

    private static final class OptionalDuplexConverter<T> extends DuplexConverter<Object, T> {
        private final DuplexConverter<Object, T> _baseConverter;

        public OptionalDuplexConverter(DuplexConverter<Object, T> baseConverter) {
            super(makeOptional(baseConverter.input), baseConverter.output);
            this._baseConverter = baseConverter;
        }

        @Override
        public T convertInput(Object value) {
            value = CommonNMS.unwrapDWROptional(value);
            if (value == null && !this._baseConverter.acceptsNullInput()) {
                return null;
            } else {
                return this._baseConverter.convertInput(value);
            }
        }

        @Override
        public Object convertOutput(T value) {
            Object result;
            if (value != null || this._baseConverter.acceptsNullOutput()) {
                result = this._baseConverter.convertOutput(value);
            } else {
                result = null;
            }
            result = CommonNMS.wrapDWROptional(result);
            return result;
        }

        @Override
        public boolean acceptsNullInput() {
            return true;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }

        private static TypeDeclaration makeOptional(TypeDeclaration type) {
            return TypeDeclaration.createGeneric(CommonNMS.DWR_OPTIONAL_TYPE, type);
        }
    }
}
