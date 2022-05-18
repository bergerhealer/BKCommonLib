package com.bergerkiller.bukkit.common.offline;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Stores information about an offline world. The world uuid is always
 * available, with the Bukkit world also efficiently accessible
 * if the world happens to be loaded. If not, <i>null</i> is returned.<br>
 * <br>
 * No expensive lookups are performed when querying the loaded Bukkit
 * World of this World. This information is automatically updated through
 * event listeners in the background.
 */
public final class OfflineWorld {
    /**
     * Single OfflineWorld constant for a 'null' world. This world will never be loaded,
     * the UUID and Bukkit World methods return null, and all methods act as if it refers
     * to an unloaded World.
     */
    public static final OfflineWorld NONE = new OfflineWorld();

    private static OfflineWorld cacheLastReturned = NONE; // Speeds up of(world)
    private static Map<World, OfflineWorld> byBukkitWorld = new IdentityHashMap<>();
    private static Map<UUID, OfflineWorld> worlds = new HashMap<UUID, OfflineWorld>(); // Copy created on modify
    private static BukkitWorldSupplierHandler toWorldSupplierFunc = DefaultBukkitWorldSupplierHandler.INSTANCE;
    private final int hashCode;
    private final UUID worldUUID;
    protected BukkitWorldSupplier loadedWorldSupplier;

    /**
     * Used to initialize the OfflineWorld API at startup. Don't ever call this. Internal
     * use only!
     *
     * @param bkcommonlibPlugin
     * @return library component to enable/disable the at-runtime logic
     */
    public static LibraryComponent initializeComponent(CommonPlugin bkcommonlibPlugin) {
        return new OfflineWorldLoadedChangeListener(bkcommonlibPlugin);
    }

    static {
        worlds.put(null, NONE);
        byBukkitWorld.put(null, NONE);
    }

    /**
     * Installs a new factory for suppliers of the loaded Bukkit world given a world uuid.
     *
     * @param newToWorldSupplierFunc
     */
    static void setLoadedWorldSupplier(BukkitWorldSupplierHandler newToWorldSupplierFunc) {
        synchronized (OfflineWorld.class) {
            toWorldSupplierFunc = newToWorldSupplierFunc;
            for (OfflineWorld world : worlds.values()) {
                world.loadedWorldSupplier = newToWorldSupplierFunc.createBukkitWorldSupplier(world.worldUUID);
            }
            if (!newToWorldSupplierFunc.cacheByBukkitWorld()) {
                clearByBukkitWorldCache();
            }
        }
    }

    /**
     * Clears the identity mapping by Bukkit world
     */
    static void clearByBukkitWorldCache() {
        synchronized (OfflineWorld.class) {
            byBukkitWorld = new IdentityHashMap<>();
            byBukkitWorld.put(null, NONE);
            cacheLastReturned = NONE;
        }
    }

    /**
     * Gets the OfflineWorld for a world with the specified
     * world uuid.
     * If the input UUID argument is null, returns {@link #NONE}
     *
     * @param worldUUID
     * @return OfflineWorld instance
     */
    public static OfflineWorld of(UUID worldUUID) {
        return LogicUtil.synchronizeCopyOnWrite(OfflineWorld.class, worlds, worldUUID, Map::get, (map, key) -> {
            Map<UUID, OfflineWorld> copy = new HashMap<>(map);
            OfflineWorld world = copy.computeIfAbsent(key, OfflineWorld::new);
            worlds = copy;
            return world;
        });
    }

    /**
     * Gets the OfflineWorld for a given Bukkit World.
     * If the input World argument is null, returns {@link #NONE}
     *
     * @param world Bukkit World
     * @return world OfflineWorld
     */
    public static OfflineWorld of(World world) {
        // Optimizes repetitive calls to of() with the same Bukkit World
        {
            OfflineWorld lastReturned = cacheLastReturned;
            if (lastReturned.loadedWorldSupplier.isWorld(world)) {
                return lastReturned;
            }
        }

        // Try fast by-identity cache. If this fails, try by world UUID
        // If cache is disabled, don't cache to avoid memory leaks.
        return cacheLastReturned = LogicUtil.synchronizeCopyOnWrite(OfflineWorld.class, byBukkitWorld, world, Map::get, (map, w) -> {
            OfflineWorld offlineWorld = of(w.getUID());

            // Modify it while synchronized, also check we should cache at all...
            // This is turned off when the plugin is disabling/disabled
            if (toWorldSupplierFunc.cacheByBukkitWorld() && offlineWorld.getLoadedWorld() == w) {
                IdentityHashMap<World, OfflineWorld> copy = new IdentityHashMap<>(byBukkitWorld);
                copy.put(w, offlineWorld);
                byBukkitWorld = copy;
            }

            return offlineWorld;
        });
    }

    private OfflineWorld() {
        this.worldUUID = null;
        this.hashCode = 0;
        this.loadedWorldSupplier = new BukkitWorldSupplier() {
            @Override
            public World get() {
                return null;
            }

            @Override
            public boolean isWorld(World world) {
                return world == null;
            }
        };
    }

    private OfflineWorld(UUID worldUUID) {
        this.worldUUID = worldUUID;
        this.hashCode = worldUUID.hashCode();
        this.loadedWorldSupplier = toWorldSupplierFunc.createBukkitWorldSupplier(worldUUID);
    }

    /**
     * Gets the unique world's UUID
     *
     * @return world uuid
     */
    public UUID getUniqueId() {
        return this.worldUUID;
    }

    /**
     * Gets whether this offline world is loaded
     *
     * @return True if loaded
     */
    public boolean isLoaded() {
        return this.loadedWorldSupplier.get() != null;
    }

    /**
     * Gets the loaded Bukkit World instance, if this world
     * is currently loaded. If not, returns <i>null</i>
     *
     * @return Bukkit World, or null if not loaded
     */
    public World getLoadedWorld() {
        return this.loadedWorldSupplier.get();
    }

    /**
     * Gets the OfflineBlock of a Block on this world
     *
     * @param position Coordinates of the Block
     * @return OfflineBlock for the Block at these coordinates
     */
    public OfflineBlock getBlockAt(IntVector3 position) {
        return new OfflineBlock(this, position);
    }

    /**
     * Gets the OfflineBlock of a Block on this world
     *
     * @param x X-coordinate of the Block
     * @param y Y-coordinate of the Block
     * @param z Z-coordinate of the Block
     * @return OfflineBlock for the Block at these coordinates
     */
    public OfflineBlock getBlockAt(int x, int y, int z) {
        return new OfflineBlock(this, IntVector3.of(x, y, z));
    }

    /**
     * Gets the OfflineBlock of a Bukkit Block on this world
     *
     * @param bukkitBlock Bukkit Block
     * @return OfflineBlock for the Block
     */
    public OfflineBlock getBlock(Block bukkitBlock) {
        return new OfflineBlock(this, IntVector3.coordinatesOf(bukkitBlock));
    }

    /**
     * Gets a block at the given coordinates, if this offline
     * world is loaded. If not, returns null.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @return Block on this world at these coordinates, or null if this
     *         world is not loaded.
     */
    public Block getLoadedBlockAt(int x, int y, int z) {
        World world = this.loadedWorldSupplier.get();
        return (world == null) ? null : world.getBlockAt(x, y, z);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

    @Override
    public String toString() {
        World world = this.loadedWorldSupplier.get();
        if (world != null) {
            return "OfflineWorld{uuid=" + this.worldUUID + ", name=" + world.getName() + "}";
        } else {
            return "OfflineWorld{uuid=" + this.worldUUID + "}";
        }
    }

    /**
     * Supplies the Bukkit World. Also has a check to see if an
     * input argument is the World, which handles null properly.
     */
    static interface BukkitWorldSupplier {
        World get();
        boolean isWorld(World world);
    }

    /**
     * Creates new Bukkit World Supplier instances for a given World UUID
     */
    static interface BukkitWorldSupplierHandler {

        /**
         * Creates a new supplier for the Bukkit World of a world UUID
         *
         * @param worldUUID
         * @return Bukkit World supplier
         */
        BukkitWorldSupplier createBukkitWorldSupplier(UUID worldUUID);

        /**
         * Whether to track a by-instance identity map of Bukkit World to OfflineWorld
         * instances
         *
         * @return Whether to cache by bukkit world
         */
        boolean cacheByBukkitWorld();
    }

    static final class DefaultBukkitWorldSupplierHandler implements BukkitWorldSupplierHandler {
        public static final DefaultBukkitWorldSupplierHandler INSTANCE = new DefaultBukkitWorldSupplierHandler();

        @Override
        public BukkitWorldSupplier createBukkitWorldSupplier(final UUID worldUUID) {
            return new BukkitWorldSupplier() {
                @Override
                public World get() {
                    return Bukkit.getWorld(worldUUID);
                }

                @Override
                public boolean isWorld(World world) {
                    return world != null && world.getUID().equals(worldUUID);
                }
            };
        }

        @Override
        public boolean cacheByBukkitWorld() {
            return false;
        }
    }
}
