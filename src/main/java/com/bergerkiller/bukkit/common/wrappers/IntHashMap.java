package com.bergerkiller.bukkit.common.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bergerkiller.reflection.net.minecraft.server.NMSIntHashMap;

/**
 * Wrapper class for the nms.IntHashMap implementation
 *
 * @param <T> - value type
 */
public class IntHashMap<T> extends BasicWrapper {

    public IntHashMap() {
        this.setHandle(NMSIntHashMap.constructor.newInstance());
    }

    public IntHashMap(Object handle) {
        this.setHandle(handle);
    }

    /**
     * Get a value
     *
     * @param key Key
     * @return Value
     */
    @SuppressWarnings("unchecked")
    public T get(int key) {
        return (T) NMSIntHashMap.get.invoke(handle, key);
    }

    /**
     * Checks whether a key is stored
     *
     * @param key to check
     * @return True if the key is stored, False if not
     */
    public boolean contains(int key) {
        return NMSIntHashMap.contains.invoke(handle, key);
    }

    /**
     * Remove a value
     *
     * @param key Key
     * @return Value
     */
    @SuppressWarnings("unchecked")
    public T remove(int key) {
        return (T) NMSIntHashMap.remove.invoke(handle, key);
    }

    /**
     * Put a value in the map
     *
     * @param key Key
     * @param value Value
     */
    public void put(int key, Object value) {
        NMSIntHashMap.put.invoke(handle, key, value);
    }

    /**
     * Clear the map
     */
    public void clear() {
        NMSIntHashMap.clear.invoke(handle);
    }

    /**
     * Gets a reference to a single entry in the IntHashMap.
     * The entry value can be modified.
     * 
     * @param key to get at
     * @return entry at the key, or null if not found
     */
    public Entry<T> getEntry(int key) {
        Object entryHandle = NMSIntHashMap.getEntry.invoke(handle, key);
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
    public List<Entry<T>> values() {
        Object[] handles = NMSIntHashMap.entries.get(handle);
        ArrayList<Entry<T>> result = new ArrayList<Entry<T>>(handles.length);
        for (Object entryHandle : handles) {
            if (entryHandle != null) {
                result.add(new Entry<T>(entryHandle));
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * A single entry in the IntHashMap
     * 
     * @param <T> hashmap value type
     */
    public static final class Entry<T> {
        private final Object handle;

        public Entry(Object handle) {
            this.handle = handle;
        }

        /**
         * Gets the key of this IntHashMap entry
         * 
         * @return entry key
         */
        public int getKey() {
            return NMSIntHashMap.Entry.key.get(this.handle);
        }

        /**
         * Gets the value of this IntHashMap entry
         * 
         * @return entry value
         */
        @SuppressWarnings("unchecked")
        public T getValue() {
            return (T) NMSIntHashMap.Entry.value.get(this.handle);
        }

        /**
         * Sets the value of this IntHashMap entry
         * 
         * @param value to set to
         */
        public void setValue(T value) {
            NMSIntHashMap.Entry.value.set(this.handle, value);
        }
    }
}
