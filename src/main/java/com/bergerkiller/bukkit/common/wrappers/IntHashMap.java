package com.bergerkiller.bukkit.common.wrappers;

import java.util.List;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.util.IntHashMapHandle;

/**
 * Wrapper class for the nms.IntHashMap implementation
 *
 * @param <T> - value type
 */
public class IntHashMap<T> extends BasicWrapper<IntHashMapHandle> implements Cloneable {

    public IntHashMap() {
        this.setHandle(IntHashMapHandle.createNew());
    }

    public IntHashMap(Object handle) {
        this.setHandle(IntHashMapHandle.createHandle(handle));
    }

    private IntHashMap(IntHashMapHandle wrappedHandle) {
        this.setHandle(wrappedHandle);
    }

    /**
     * Get a value
     *
     * @param key Key
     * @return Value
     */
    @SuppressWarnings("unchecked")
    public T get(int key) {
        return (T) this.handle.get(key);
    }

    /**
     * Checks whether a key is stored
     *
     * @param key to check
     * @return True if the key is stored, False if not
     */
    public boolean contains(int key) {
        return this.handle.containsKey(key);
    }

    /**
     * Remove a value
     *
     * @param key Key
     * @return Value
     */
    @SuppressWarnings("unchecked")
    public T remove(int key) {
        return (T) this.handle.remove(key);
    }

    /**
     * Put a value in the map
     *
     * @param key Key
     * @param value Value
     */
    public void put(int key, Object value) {
        this.handle.put(key, value);
    }

    /**
     * Clear the map
     */
    public void clear() {
        this.handle.clear();
    }

    /**
     * Gets a reference to a single entry in the IntHashMap.
     * The entry value can be modified.
     * 
     * @param key to get at
     * @return entry at the key, or null if not found
     */
    public Entry<T> getEntry(int key) {
        Object entryHandle = this.handle.getEntry(key);
        if (entryHandle == null) {
            return null;
        } else {
            return new Entry<T>(entryHandle);
        }
    }

    /**
     * Gets a list of all the entries in the hash map.
     * The entry values can be modified.
     * 
     * @return list of hashmap entries
     */
    public List<Entry<T>> entries() {
        return LogicUtil.unsafeCast(this.handle.getEntries());
    }

    /**
     * Gets a list of all the values stored in the hash map.
     * The values can not be modified.
     * 
     * @return list of values
     */
    @SuppressWarnings("unchecked")
    public List<T> values() {
        return (List<T>) this.handle.getValues();
    }

    /**
     * Gets the number of entries stored inside this hashmap
     * 
     * @return hashmap size
     */
    public int size() {
        return this.handle.size();
    }

    @Override
    public IntHashMap<T> clone() {
        return new IntHashMap<T>(this.handle.cloneMap());
    }

    /**
     * A single entry in the IntHashMap
     * 
     * @param <T> hashmap value type
     */
    public static final class Entry<T> extends BasicWrapper<IntHashMapHandle.IntHashMapEntryHandle> {

        public Entry(Object handle) {
            this.setHandle(IntHashMapHandle.IntHashMapEntryHandle.createHandle(handle));
        }

        /**
         * Gets the key of this IntHashMap entry
         * 
         * @return entry key
         */
        public int getKey() {
            return this.handle.getKey();
        }

        /**
         * Gets the value of this IntHashMap entry
         * 
         * @return entry value
         */
        @SuppressWarnings("unchecked")
        public T getValue() {
            return (T) this.handle.getValue();
        }

        /**
         * Sets the value of this IntHashMap entry
         * 
         * @param value to set to
         */
        public void setValue(T value) {
            this.handle.setValue(value);
        }
    }
}
