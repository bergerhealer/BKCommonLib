package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * A map implementation that wraps a standard HashMap, but also maps by identity and
 * the last-obtained value. This avoids the overhead of hashing the key.<br>
 * <br>
 * Absolutely do not use this if the key isn't re-used very often. This will cause
 * the by-instance map to quickly explode. Also do not use this if the key input
 * can vary wildly from one call to the next. This should also not be used if
 * keys need to be removed frequently, as this operation is slow.
 * Null keys cannot be used.<br>
 * <br>
 * This class is not thread-safe.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class FastIdentityHashMap<K, V> {
    private K getLastKey = null;
    private V getLastValue = null;
    private final IdentityHashMap<K, V> valueByIdentityKey = new IdentityHashMap<K, V>();
    private final HashMap<K, Bin<K>> binByKey = new HashMap<K, Bin<K>>();
    private final ArrayList<V> values = new ArrayList<V>();
    private final Collection<V> valuesReadOnly = Collections.unmodifiableCollection(values);

    public V get(K key) {
        // Efficient lookup of the same key in a tight loop
        if (getLastKey == key) {
            return getLastValue;
        }

        // Efficient by-identity lookup
        V byIdentityKeyResult = valueByIdentityKey.get(key);
        if (byIdentityKeyResult != null) {
            getLastKey = key;
            getLastValue = byIdentityKeyResult;
            return byIdentityKeyResult;
        }

        // Check if another key equal (but not in identity) exists
        // If so, add this key to the identity cache
        Bin<K> bin = binByKey.get(key);
        if (bin != null) {
            // Make mutable (singleton list)
            if (bin.keyIdentities.size() <= 1) {
                bin.keyIdentities = new ArrayList<K>(bin.keyIdentities);
            }

            // Add the key
            bin.keyIdentities.add(key);

            // Add value to by-identity-key mapping
            V value = values.get(bin.valueIndex);
            valueByIdentityKey.put(key, value);

            // Done
            getLastKey = key;
            getLastValue = value;
            return value;
        }

        return null;
    }

    public V put(K key, V value) {
        // Check if a bin already exists. If so, simply set the value.
        Bin<K> bin = binByKey.get(key);
        if (bin != null) {
            V oldValue = values.get(bin.valueIndex);
            values.set(bin.valueIndex, value);
            for (K keyIdentity : bin.keyIdentities) {
                valueByIdentityKey.put(keyIdentity, value);
            }
            if (key == getLastKey) {
                getLastValue = value;
            }
            return oldValue;
        }

        // Create a new bin, add to values array
        bin = new Bin<K>(key, values.size());
        values.add(value);
        binByKey.put(key, bin);
        valueByIdentityKey.put(key, value);
        return null;
    }

    public V remove(K key) {
        Bin<K> bin = binByKey.remove(key);
        if (bin != null) {
            int removedValueIndex = bin.valueIndex;
            V removedValue = values.remove(removedValueIndex);
            for (K keyIdentity : bin.keyIdentities) {
                valueByIdentityKey.remove(keyIdentity);
            }

            // Update all bins with value index beyond the value index of this bin.
            // We need to subtract one from it, since the value list shrank.
            for (Bin<K> otherBin : binByKey.values()) {
                int index = otherBin.valueIndex;
                if (index > removedValueIndex) {
                    otherBin.valueIndex = index - 1;
                }
            }

            // Reset these to avoid getting a removed value
            getLastKey = null;
            getLastValue = null;

            return removedValue;
        }

        // Not found
        return null;
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Removes all keys and values and clears the cache
     */
    public void clear() {
        getLastKey = null;
        getLastValue = null;
        valueByIdentityKey.clear();
        binByKey.clear();
        values.clear();
    }

    /**
     * Removes all cached key instances except the last one that matches
     * for equality. This makes sure every unique key only has one unique
     * key instance assigned, potentially avoiding memory leaks.
     */
    public void optimize() {
        for (Bin<K> bin : binByKey.values()) {
            int numIdentities = bin.keyIdentities.size();
            if (numIdentities > 1) {
                // Discard all key identities except the last
                numIdentities--;
                for (int i = 0; i < numIdentities; i++) {
                    valueByIdentityKey.remove(bin.keyIdentities.get(i));
                }
                bin.keyIdentities = Collections.singletonList(bin.keyIdentities.get(numIdentities));
            }
        }
    }

    /**
     * Gets an unmodifiable set of keys stored in this map
     *
     * @return unmodifiable key set
     */
    public Set<K> keySet() {
        return Collections.unmodifiableSet(binByKey.keySet());
    }

    /**
     * Gets an unmodifiable collection of values stored in this map
     *
     * @return unmodifiable value collection
     */
    public Collection<V> values() {
        return valuesReadOnly;
    }

    /**
     * Groups the many unique identities of a key and where the
     * value is stored in the 'values' list.
     * 
     * @param <K> Key type
     */
    private static class Bin<K> {
        public List<K> keyIdentities;
        public int valueIndex;

        public Bin(K key, int valueIndex) {
            this.keyIdentities = Collections.singletonList(key);
            this.valueIndex = valueIndex;
        }
    }
}
