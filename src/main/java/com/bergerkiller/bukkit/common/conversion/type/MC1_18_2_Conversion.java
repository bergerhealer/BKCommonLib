package com.bergerkiller.bukkit.common.conversion.type;

import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.core.RegistryMaterialsHandle;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEffectHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Converts between Holder&lt;DimensionManager&gt; and DimensionManager
 */
public class MC1_18_2_Conversion {
    private static WeakHashMap<Object, Object> holdersByDimensionManager = new WeakHashMap<>();
    private static HolderLogic handler;

    public static void init() {
        handler = Template.Class.create(HolderLogic.class);
        handler.forceInitialization();
    }

    public static LibraryComponent initComponent(final CommonPlugin plugin) {
        return new LibraryComponent() {
            @Override
            public void enable() throws Throwable {
                for (World world : Bukkit.getWorlds()) {
                    track(world);
                }
                plugin.register(new Listener() {
                    @EventHandler(priority = EventPriority.LOWEST)
                    public void onWorldInit(WorldInitEvent event) {
                        track(event.getWorld());
                    }
                });
            }

            @Override
            public void disable() throws Throwable {
                synchronized (holdersByDimensionManager) {
                    holdersByDimensionManager.clear();
                }
            }
        };
    }

    private static void track(World world) {
        synchronized (holdersByDimensionManager) {
            holdersByDimensionManager.put(handler.getDimensionTypeOfWorld(world),
                                          handler.getHolderOfWorld(world));
        }
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionManager>",
                     output="net.minecraft.world.level.dimension.DimensionManager")
    public static Object fromHolderToDimensionManager(Object holder) {
        return handler.getValue(holder);
    }

    @ConverterMethod(input="net.minecraft.world.level.dimension.DimensionManager",
                     output="net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionManager>")
    public static Object fromDimensionManagerToHolder(Object dimensionManager) {
        synchronized (holdersByDimensionManager) {
            Object holder = holdersByDimensionManager.get(dimensionManager);
            if (holder == null) {
                throw new IllegalArgumentException("Unknown or unregistered dimension type");
            }
            return holder;
        }
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.sounds.SoundEffect>")
    public static ResourceKey<SoundEffect> soundEffectHolderToResourceKey(Object nmsHolderHandle) {
        return ResourceKey.fromResourceKeyHandle(handler.getResourceKey(nmsHolderHandle));
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.sounds.SoundEffect>")
    public static Object soundEffectHolderFromResourceKey(ResourceKey<SoundEffect> soundKey) {
        Object raw_registry = SoundEffectHandle.T.opt_getRegistry.raw.invoke();
        return RegistryMaterialsHandle.T.rawSoundEffectResourceKeyToHolder.invoke(raw_registry, soundKey.getRawHandle());
    }

    @Template.Optional
    @Template.Import("org.bukkit.craftbukkit.CraftWorld")
    @Template.Import("net.minecraft.world.level.dimension.DimensionManager")
    @Template.InstanceType("net.minecraft.core.Holder")
    public static abstract class HolderLogic extends Template.Class<Template.Handle> {

        /*
         * <GET_HOLDER_VALUE>
         * public static Object getValue(Holder holder) {
         *     return holder.value();
         * }
         */
        @Template.Generated("%GET_HOLDER_VALUE%")
        public abstract Object getValue(Object holder);

        /*
         * <GET_HOLDER_RESOURCEKEY>
         * public static Object getResourceKey(Holder holder) {
         *     return holder.unwrapKey().orElse(null);
         * }
         */
        @Template.Generated("%GET_HOLDER_RESOURCEKEY%")
        public abstract Object getResourceKey(Object holder);

        /*
         * <GET_DIMENSION_TYPE_OF_WORLD>
         * public static Object getDimensionType(CraftWorld world) {
         *     return world.getHandle().dimensionType();
         * }
         */
        @Template.Generated("%GET_DIMENSION_TYPE_OF_WORLD%")
        public abstract Object getDimensionTypeOfWorld(World world);

        /*
         * <GET_HOLDER_OF_WORLD>
         * public static Object getHolder(CraftWorld world) {
         *     return ((net.minecraft.world.level.World) world.getHandle()).dimensionTypeRegistration();
         * }
         */
        @Template.Generated("%GET_HOLDER_OF_WORLD%")
        public abstract Object getHolderOfWorld(World world);
    }
}
