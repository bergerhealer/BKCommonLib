package com.bergerkiller.bukkit.common.offline;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.World;

/**
 * Maps a value by an OfflineWorld argument. Has optimized methods to deal
 * with a loaded Bukkit world argument.<br>
 * <br>
 * This class is not thread-safe.
 * 
 * @param <V> Value type
 */
public class OfflineWorldMap<V> {
    private final Map<OfflineWorld, V> values = new IdentityHashMap<OfflineWorld, V>();
    private OfflineWorld lastGetKey = OfflineWorld.NONE;
    private V lastGetValue = null;

    /**
     * Gets whether this map is empty
     *
     * @return True if empty
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Gets the number of values stored in this mapping
     *
     * @return number of values
     */
    public int size() {
        return values.size();
    }

    /**
     * Gets the value mapped to an OfflineWorld
     *
     * @param world
     * @return Value mapped to world
     */
    public V get(OfflineWorld world) {
        if (world == lastGetKey) {
            return lastGetValue;
        } else {
            lastGetKey = world;
            return lastGetValue = values.get(world);
        }
    }

    /**
     * Gets the value mapped to a Bukkit World
     *
     * @param world
     * @return Value mapped to world
     */
    public V get(World world) {
        if (world == lastGetKey.getLoadedWorld()) {
            return lastGetValue;
        } else {
            OfflineWorld oWorld = OfflineWorld.of(world);
            lastGetKey = oWorld;
            return lastGetValue = values.get(oWorld);
        }
    }

    /**
     * Gets the value mapped to an OfflineWorld, or returns the
     * default value if no value is stored, or the value is null.<br>
     * <br>
     * <b>Does not support null values</b>
     *
     * @param world
     * @param defaultValue Default value to return when no value is stored, or is null
     * @return Value mapped to world, or defaultValue if not stored or is null
     */
    public V getOrDefault(OfflineWorld world, V defaultValue) {
        V value = this.get(world);
        return (value != null) ? value : defaultValue;
    }

    /**
     * Gets the value mapped to a Bukkit World, or returns the
     * default value if no value is stored, or the value is null.<br>
     * <br>
     * <b>Does not support null values</b>
     *
     * @param world
     * @param defaultValue Default value to return when no value is stored, or is null
     * @return Value mapped to world, or defaultValue if not stored or is null
     */
    public V getOrDefault(World world, V defaultValue) {
        V value = this.get(world);
        return (value != null) ? value : defaultValue;
    }

    /**
     * Removes a mapping of an OfflineWorld, if stored
     *
     * @param world
     * @return Removed value, or null if no value was removed
     */
    public V remove(OfflineWorld world) {
        lastGetKey = OfflineWorld.NONE;
        return values.remove(world);
    }

    /**
     * Removes a mapping of a Bukkit World, if stored
     *
     * @param world
     * @return Removed value, or null if no value was removed
     */
    public V remove(World world) {
        return remove(OfflineWorld.of(world));
    }

    /**
     * Stores a new mapping to an OfflineWorld
     *
     * @param world
     * @param value
     * @return Previously stored value, or null if no value was previously stored
     */
    public V put(OfflineWorld world, V value) {
        lastGetKey = OfflineWorld.NONE;
        return values.put(world, value);
    }

    /**
     * Stores a new mapping to a Bukkit World
     *
     * @param world
     * @param value
     * @return Previously stored value, or null if no value was previously stored
     */
    public V put(World world, V value) {
        return put(OfflineWorld.of(world), value);
    }

    /**
     * Computes a new value if no value is currently stored for an OfflineWorld
     *
     * @param world
     * @param mappingFunction
     * @return Current or computed value mapped to world
     */
    public V computeIfAbsent(OfflineWorld world, Function<OfflineWorld, ? extends V> mappingFunction) {
        V value;
        if (lastGetKey == world && (value = lastGetValue) != null) {
            return value;
        } else {
            try {
                lastGetKey = world;
                return lastGetValue = values.computeIfAbsent(world, mappingFunction);
            } catch (RuntimeException | Error e) {
                lastGetKey = OfflineWorld.NONE;
                throw e;
            }
        }
    }

    /**
     * Computes a new value if no value is currently stored for a Bukkit World
     *
     * @param world
     * @param mappingFunction
     * @return Current or computed value mapped to world
     */
    public V computeIfAbsent(World world, Function<World, ? extends V> mappingFunction) {
        V value;
        if (lastGetKey.getLoadedWorld() == world && (value = lastGetValue) != null) {
            return value;
        } else {
            try {
                OfflineWorld offlineWorld = OfflineWorld.of(world);
                lastGetKey = offlineWorld;
                return lastGetValue = values.computeIfAbsent(offlineWorld, unused -> mappingFunction.apply(world));
            } catch (RuntimeException | Error e) {
                lastGetKey = OfflineWorld.NONE;
                throw e;
            }
        }
    }

    /**
     * Gets a collection of all values stored in this mapping
     *
     * @return values
     */
    public Collection<V> values() {
        return values.values();
    }

    /**
     * Gets a Set of all OfflineWorld keys stored in this mapping
     *
     * @return map key set
     */
    public Set<OfflineWorld> keySet() {
        return values.keySet();
    }

    /**
     * Gets a set of all map entries, with OfflineWorld keys and
     * values bound to them.
     *
     * @return map entry set
     */
    public Set<Map.Entry<OfflineWorld, V>> entrySet() {
        return values.entrySet();
    }

    /**
     * Clears all values
     */
    public void clear() {
        lastGetKey = OfflineWorld.NONE;
        values.clear();
    }
}
