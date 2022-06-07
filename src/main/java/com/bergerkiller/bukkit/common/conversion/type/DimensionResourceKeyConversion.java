package com.bergerkiller.bukkit.common.conversion.type;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.dimension.DimensionManagerHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Converts between ResourceKey&lt;DimensionManager&gt; and DimensionType.
 * If enabled, makes use of a plugin-tracked cache of these mappings in case
 * dimensions aren't registered.
 *
 * Only used on Minecraft 1.16 and 1.16.1, and for 1.19+
 */
public class DimensionResourceKeyConversion {
    private static Tracker TRACKER = null;
    private static final FastMethod<Object> registryGetByKeyMethod = new FastMethod<>();
    private static final FastMethod<java.util.Optional<?>> registryGetByValueMethod = new FastMethod<>();

    public static void init() throws Throwable {
        Class<?> minecraftServerType = Resolver.loadClass("net.minecraft.server.MinecraftServer", false);
        if (minecraftServerType == null) {
            throw new IllegalStateException("MinecraftServer type not found");
        }

        // Methods of IRegistry to get a value by key, or key by value
        Class<?> registryType = Resolver.loadClass("net.minecraft.core.IRegistry", false);
        if (registryType == null) {
            throw new IllegalStateException("IRegistry type not found");
        }
        Class<?> resourceKeyType = Resolver.loadClass("net.minecraft.resources.ResourceKey", false);
        if (resourceKeyType == null) {
            throw new IllegalStateException("ResourceKey type not found");
        }

        if (CommonBootstrap.evaluateMCVersion(">=", "1.19")) {
            registryGetByKeyMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "get", resourceKeyType));
            registryGetByValueMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "getResourceKey", Object.class));
        } else {
            registryGetByKeyMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "a", resourceKeyType));
            registryGetByValueMethod.init(Resolver.resolveAndGetDeclaredMethod(registryType, "c", Object.class));
        }
        registryGetByKeyMethod.forceInitialization();
        registryGetByValueMethod.forceInitialization();
    }

    @ConverterMethod(input="net.minecraft.resources.ResourceKey<net.minecraft.world.level.dimension.DimensionManager>")
    public static DimensionType toDimensionType(Object resourceKeyHandle) {
        Object registry = DimensionManagerHandle.getDimensionTypeRegistry();
        Object dimensionManagerHandle = registryGetByKeyMethod.invoke(registry, resourceKeyHandle);
        if (dimensionManagerHandle == null) {
            // Try cache
            Tracker tracker = TRACKER;
            if (tracker != null) {
                dimensionManagerHandle = tracker.findDimensionManager(
                        ResourceKey.fromResourceKeyHandle(resourceKeyHandle));
            }
            if (dimensionManagerHandle == null) {
                throw new IllegalArgumentException("Dimension key is not registered: " + resourceKeyHandle);
            }
        }
        return DimensionType.fromDimensionManagerHandle(dimensionManagerHandle);
    }

    @ConverterMethod(output="net.minecraft.resources.ResourceKey<net.minecraft.world.level.dimension.DimensionManager>")
    public static Object toResourceKey(DimensionType dimensionType) {
        Object dimensionManagerHandle = dimensionType.getDimensionManagerHandle();
        Object registry = DimensionManagerHandle.getDimensionTypeRegistry();
        java.util.Optional<?> resourceKeyHandle = registryGetByValueMethod.invoke(registry, dimensionManagerHandle);
        if (resourceKeyHandle != null && resourceKeyHandle.isPresent()) {
            return resourceKeyHandle.get();
        }

        // Try cache
        Tracker tracker = TRACKER;
        if (tracker != null) {
            ResourceKey<?> resourceKey = tracker.findResourceKey(dimensionManagerHandle);
            if (resourceKey != null) {
                return resourceKey.getRawHandle();
            }
        }

        throw new IllegalArgumentException("Dimension is not registered: " + dimensionManagerHandle);
    }

    public static class Tracker implements LibraryComponent {
        private final BiMap<Object, ResourceKey<?>> RESOURCE_KEY_BY_DIMENSION_MANAGER = HashBiMap.create();
        private final BiMap<ResourceKey<?>, Object> DIMENSION_MANAGER_BY_RESOURCE_KEY = RESOURCE_KEY_BY_DIMENSION_MANAGER.inverse();
        private final FastMethod<Object> getWorldTypeKeyMethod = new FastMethod<>();
        private final JavaPlugin plugin;
        private final Listener listener;

        public Tracker(JavaPlugin plugin) {
            this.plugin = plugin;
            this.listener = new Listener() {
                @EventHandler(priority = EventPriority.MONITOR)
                protected void onWorldInit(final WorldInitEvent event) {
                    registerDimensionManager(WorldServerHandle.fromBukkit(event.getWorld()));
                }

                @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
                protected void onWorldUnload(WorldUnloadEvent event) {
                    deregisterDimensionManager(WorldServerHandle.fromBukkit(event.getWorld()));
                }
            };
        }

        @Override
        public void enable() throws Throwable {
            getWorldTypeKeyMethod.init(Resolver.resolveAndGetDeclaredMethod(WorldHandle.T.getType(), "getTypeKey"));
            getWorldTypeKeyMethod.forceInitialization();

            // Now all is well, register the initial values and go
            for (World world : Bukkit.getWorlds()) {
                registerDimensionManager(WorldServerHandle.fromBukkit(world));
            }
            Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);

            // Make available
            TRACKER = this;
        }

        @Override
        public void disable() {
            TRACKER = null;
            CommonUtil.unregisterListener(this.listener);
        }

        public synchronized Object findDimensionManager(ResourceKey<?> resourceKey) {
            return DIMENSION_MANAGER_BY_RESOURCE_KEY.get(resourceKey);
        }

        public synchronized ResourceKey<?> findResourceKey(Object dimensionManager) {
            return RESOURCE_KEY_BY_DIMENSION_MANAGER.get(dimensionManager);
        }

        private void registerDimensionManager(WorldServerHandle world) {
            Object dimensionManager = world.getDimensionType().getDimensionManagerHandle();
            Object rawResourceKey = getWorldTypeKeyMethod.invoke(world.getRaw());
            ResourceKey<?> resourceKey = ResourceKey.fromResourceKeyHandle(rawResourceKey);

            synchronized (this) {
                RESOURCE_KEY_BY_DIMENSION_MANAGER.forcePut(dimensionManager, resourceKey);
            }
        }

        private void deregisterDimensionManager(WorldServerHandle world) {
            Object dimensionManager = world.getDimensionType().getDimensionManagerHandle();

            synchronized (this) {
                RESOURCE_KEY_BY_DIMENSION_MANAGER.remove(dimensionManager);
            }
        }
    }
}
