package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.LongObjectHashMapHandle;

import java.util.Collection;
import java.util.Set;
import java.util.function.LongFunction;

/**
 * A wrapper around the internal LongHashMap implementation. This type of
 * HashMap allows storing values at long keys. Ideally, two int values are
 * merged into one long to map against 2D-coordinates. This type of class is
 * used by, for example, the internal chunk storage.
 *
 * @param <V> - Value type
 */
@SuppressWarnings("unchecked")
public class LongHashMap<V> extends BasicWrapper<LongObjectHashMapHandle> implements Cloneable {

    /**
     * Constructs a new LongHashMap
     */
    public LongHashMap() {
        this.setHandle(LongObjectHashMapHandle.createNew());
    }

    /**
     * Constructs a new LongHashMap with an initial capacity as specified
     *
     * @param initialCapacity for the new LongHashMap
     */
    public LongHashMap(int initialCapacity) {
        this.setHandle(LongObjectHashMapHandle.createNew());
    }

    public LongHashMap(Object handle) {
        this.setHandle(LongObjectHashMapHandle.createHandle(handle));
    }

    private LongHashMap(LongObjectHashMapHandle handle) {
        this.setHandle(handle);
    }

    /**
     * Gets the amount of Long:Value pairs stored in this LongHashMap
     *
     * @return size
     */
    public int size() {
        return handle.size();
    }

    /**
     * Clears all stored entries from this long hash map
     */
    public void clear() {
        handle.clear();
    }

    /**
     * Checks whether this Long HashMap contains the coordinates specified.<br>
     * <b>Warning: this method was added in v1.54 and is not compatible with MC
     * 1.5.2.</b>
     *
     * @param msw - most significant part of the key
     * @param lsw - least signfificant part of the key
     * @return True if contained, False if not
     */
    public boolean contains(int msw, int lsw) {
        return contains(MathUtil.longHashToLong(msw, lsw));
    }

    public boolean contains(long key) {
        return handle.containsKey(key);
    }

    /**
     * Gets the value stored at the coordinates specified.<br>
     * <b>Warning: this method was added in v1.54 and is not compatible with MC
     * 1.5.2.</b>
     *
     * @param msw - most significant part of the key
     * @param lsw - least signfificant part of the key
     * @return The value stored at the key, or null if none stored
     */
    public V get(int msw, int lsw) {
        return get(MathUtil.longHashToLong(msw, lsw));
    }

    public V get(long key) {
        return (V) handle.get(key);
    }

    /**
     * Removes and obtains the value stored at the coordinates specified.<br>
     * <b>Warning: this method was added in v1.54 and is not compatible with MC
     * 1.5.2.</b>
     *
     * @param msw - most significant part of the key
     * @param lsw - least signfificant part of the key
     * @return The removed value previously stored at the key, or null if none
     * was stored
     */
    public V remove(int msw, int lsw) {
        return remove(MathUtil.longHashToLong(msw, lsw));
    }

    public V remove(long key) {
        return (V) handle.remove(key);
    }

    /**
     * Puts a value at the coordinates specified
     *
     * @param msw - most significant part of the key
     * @param lsw - least signfificant part of the key
     * @param value to put at the coordinates
     */
    public void put(int msw, int lsw, V value) {
        put(MathUtil.longHashToLong(msw, lsw), value);
    }

    public void put(long key, V value) {
        handle.put(key, value);
    }

    /**
     * Puts a value at the coordinates specified and returns the previous value
     *
     * @param msw - most significant part of the key
     * @param lsw - least signfificant part of the key
     * @param value to put at the coordinates
     * @return Previous value, or null if none existed
     */
    public V getAndPut(int msw, int lsw, V value) {
        return (V) handle.put(MathUtil.longHashToLong(msw, lsw), value);
    }

    /**
     * Puts a value at the key specified and returns the previous value
     *
     * @param key Key
     * @param value to put at the coordinates
     * @return Previous value, or null if none existed
     */
    public V getAndPut(long key, V value) {
        return (V) handle.put(key, value);
    }

    /**
     * Puts a new value at the key specified. If a previous value existed, the remapping function
     * is called with the old value and new value, and the output of that function is put instead.
     *
     * @param key Key
     * @param value Value to put
     * @param remappingFunction Function to call to merge the old and new values
     * @return The put value, which is the input value if no previous value existed, or the merge result otherwise
     */
    public V merge(long key, V value, java.util.function.BiFunction<? super V,? super V,? extends V> remappingFunction) {
        return (V) handle.merge(key, value, remappingFunction);
    }

    /**
     * Gets the value that is stored for a key. If none is mapped, calls the mapping function
     * to create a new value, and then stores and returns it instead.
     *
     * @param key Key at which to get or store a value
     * @param mappingFunction Mapper from key to the value to store
     * @return Stored or computed value
     */
    public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
        return (V) handle.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Gets the value that exists at a key. If none is mapped, returns the default value instead.
     *
     * @param key Key
     * @param defaultValue Value to return if absent
     * @return Value at the key, or the Default value otherwise
     */
    public V getOrDefault(long key, V defaultValue) {
        return (V) handle.getOrDefault(key, defaultValue);
    }

    public Collection<V> getValues() {
        return values();
    }

    public Collection<V> values() {
        return (Collection<V>) handle.values();
    }

    public long[] getKeys() {
        Set<Long> keys = handle.keySet();
        long[] result = new long[keys.size()];
        int i = 0;
        for (Long key : keys) {
            result[i++] = key.longValue();
        }
        return result;
        //return ((TLongObjectHashMap<V>) getRawHandle()).keySet().toArray(new long[0]);
    }

    /**
     * Clones this LongHashMap to create a new instance of the same backing map with the same
     * keys and values as this map. Changes to the returned map do not affect this one.
     */
    @Override
    public LongHashMap<V> clone() {
        return new LongHashMap<V>(handle.cloneMap());
    }
}
