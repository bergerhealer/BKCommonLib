package com.bergerkiller.bukkit.common.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.collections.ImmutablePlayerSet;
import com.google.common.collect.MapMaker;

/**
 * Tracks and handles the memory management for {@link ImmutablePlayerSet}.
 * Uses XOR of the player's hashcodes for simplicity, as it handles adding and removing identically
 * this way.
 */
public class CommonImmutablePlayerSetManager {
    private final Object _lock = new Object();
    private final CommonImmutablePlayerSetData _tmp = new CommonImmutablePlayerSetData();
    private final ConcurrentMap<CommonImmutablePlayerSetData, CommonImmutablePlayerSet> _cache = new MapMaker()
            .concurrencyLevel(1)
            .weakValues()
            .makeMap();

    /**
     * Gets the number of player sets stored in cache
     * 
     * @return cache size
     */
    public int cacheSize() {
        return this._cache.size();
    }

    /**
     * Removes all sets from the cache that store the given Player object in it.
     * This helps the garbage collector in freeing unused sets more quickly.
     * 
     * @param p
     */
    public void clearCache(Player p) {
        synchronized (this._lock) {
            Iterator<CommonImmutablePlayerSetData> keysIter = _cache.keySet().iterator();
            while (keysIter.hasNext()) {
                if (keysIter.next().set.contains(p)) {
                    keysIter.remove();
                }
            }
        }
    }

    /**
     * Gets or creates an unique immutable player set from the cache
     * 
     * @param players in the set
     * @return immutable player set
     */
    public ImmutablePlayerSet get(Iterable<Player> players) {
        // Check first whether the set is empty
        Iterator<Player> iter = players.iterator();
        if (!iter.hasNext()) {
            return ImmutablePlayerSet.EMPTY;
        }

        // Access the cache under a lock. Guava allows for concurrency,
        // but we are also using a temporary set, which renders that useless.
        synchronized (this._lock) {
            try {
                // Fill temporary buffer with data in the collection
                this._tmp.addAll(players);

                // Retrieve from cache or create value
                return this.find();
            } finally {
                // Make sure to clear our temporary set when exiting!
                this._tmp.reset();
            }
        }
    }

    /**
     * Uses the cached and re-used HashSet instance to find or create a new immutable player set
     * 
     * @return immutable player set
     */
    private final ImmutablePlayerSet find() {
        if (this._tmp.set.isEmpty()) {
            return ImmutablePlayerSet.EMPTY;
        }
        CommonImmutablePlayerSet result = this._cache.get(this._tmp);
        if (result == null) {
            // Create a new copy of the data
            CommonImmutablePlayerSetData data = new CommonImmutablePlayerSetData(this._tmp);
            // Create a new player set using it
            result = new CommonImmutablePlayerSet(this, data);
            // Store both in the cache
            this._cache.put(data, result);
        }
        return result;
    }

    private static final class CommonImmutablePlayerSet extends ImmutablePlayerSet {
        private final CommonImmutablePlayerSetManager _manager;
        private final CommonImmutablePlayerSetData _data;

        public CommonImmutablePlayerSet(CommonImmutablePlayerSetManager manager, CommonImmutablePlayerSetData data) {
            this._manager = manager;
            this._data = data;
        }

        @Override
        public int hashCode() {
            return this._data.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CommonImmutablePlayerSet) {
                CommonImmutablePlayerSet other = (CommonImmutablePlayerSet) o;
                return other._data.equals(this._data);
            } else if (o instanceof ImmutablePlayerSet) {
                ImmutablePlayerSet other = (ImmutablePlayerSet) o;
                if (other.size() != this.size()) {
                    return false;
                }
                for (Player p : other) {
                    if (!this.contains(p)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int size() {
            return this._data.set.size();
        }

        @Override
        public Iterator<Player> iterator() {
            return this._data.set.iterator();
        }

        @Override
        public boolean contains(Player player) {
            return this._data.set.contains(player);
        }

        @Override
        public boolean containsAll(Collection<Player> players) {
            return this._data.set.containsAll(players);
        }

        @Override
        public ImmutablePlayerSet add(Player player) {
            if (this.contains(player)) {
                return this;
            } else {
                synchronized (this._manager._lock) {
                    try {
                        this._manager._tmp.setTo(this._data);
                        this._manager._tmp.quickAdd(player);
                        return this._manager.find();
                    } finally {
                        this._manager._tmp.reset();
                    }
                }
            }
        }

        @Override
        public ImmutablePlayerSet addAll(Iterable<Player> players) {
            synchronized (this._manager._lock) {
                try {
                    this._manager._tmp.setTo(this._data);
                    if (this._manager._tmp.addAll(players)) {
                        return this._manager.find();
                    } else {
                        return this; // No changes
                    }
                } finally {
                    this._manager._tmp.reset();
                }
            }
        }

        @Override
        public ImmutablePlayerSet remove(Player player) {
            if (!this.contains(player)) {
                return this;
            } else {
                try {
                    this._manager._tmp.setTo(this._data);
                    this._manager._tmp.quickRemove(player);
                    return this._manager.find();
                } finally {
                    this._manager._tmp.reset();
                }
            }
        }

        @Override
        public ImmutablePlayerSet removeAll(Iterable<Player> players) {
            synchronized (this._manager._lock) {
                try {
                    this._manager._tmp.setTo(this._data);
                    if (this._manager._tmp.removeAll(players)) {
                        return this._manager.find();
                    } else {
                        return this; // No changes
                    }
                } finally {
                    this._manager._tmp.reset();
                }
            }
        }
    }

    // stores the player set of data. This is stored both
    // for the key, as well as in a field of the garbage-collectable
    // value in the cache.
    private static final class CommonImmutablePlayerSetData {
        private Set<Player> set;
        private int hashCode;

        // mutable. Only used for the temporary cache object for finding entries in the cache
        public CommonImmutablePlayerSetData() {
            this.set = new HashSet<Player>();
            this.hashCode = 0;
        }

        // immutable. This is actually stored in the cache.
        public CommonImmutablePlayerSetData(CommonImmutablePlayerSetData data) {
            if (data.set.size() == 1) {
                // Use singleton set for single-entry sets
                this.set = Collections.singleton(data.set.iterator().next());
            } else {
                //TODO: Is Guava's immutable set an idea?
                this.set = Collections.unmodifiableSet(new HashSet<Player>(data.set));
            }
            // Copy hashCode
            this.hashCode = data.hashCode;
        }

        public void reset() {
            this.set.clear();
            this.hashCode = 0;
        }

        public void setTo(CommonImmutablePlayerSetData data) {
            this.hashCode = data.hashCode;
            this.set.clear();
            this.set.addAll(data.set);
        }

        public void quickAdd(Player p) {
            if (p == null) {
                throw new IllegalArgumentException("Null values not supported");
            }
            this.set.add(p);
            this.hashCode ^= p.hashCode();
        }

        public boolean addAll(Iterable<Player> players) {
            boolean changed = false;
            for (Player p : players) {
                if (p == null) {
                    throw new IllegalArgumentException("Null values not supported");
                }
                if (this.set.add(p)) {
                    this.hashCode ^= p.hashCode();
                    changed = true;
                }
            }
            return changed;
        }

        public void quickRemove(Player p) {
            this.set.remove(p);
            this.hashCode ^= p.hashCode();
        }

        public boolean removeAll(Iterable<Player> players) {
            boolean changed = false;
            for (Player p : players) {
                if (this.set.remove(p)) {
                    this.hashCode ^= p.hashCode();
                    changed = true;
                }
            }
            return changed;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(CommonImmutablePlayerSetData data) {
            return data.set.equals(this.set);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CommonImmutablePlayerSetData) {
                return this.equals((CommonImmutablePlayerSetData) o);
            } else {
                return false;
            }
        }
    }

}
