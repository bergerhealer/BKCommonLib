package com.bergerkiller.bukkit.common.conversion.type;

import java.util.WeakHashMap;

import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeHandle;
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
import com.bergerkiller.generated.net.minecraft.sounds.SoundEventHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Converts between Holder&lt;DimensionType&gt; and DimensionType, and does
 * similar logic for MobEffect (potion effects) and Attribute (attributes)
 */
public class MC1_18_2_Conversion {
    private static WeakHashMap<Object, Object> holdersByDimensionType = new WeakHashMap<>();
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
                synchronized (holdersByDimensionType) {
                    holdersByDimensionType.clear();
                }
            }
        };
    }

    private static void track(World world) {
        synchronized (holdersByDimensionType) {
            holdersByDimensionType.put(handler.getDimensionTypeOfWorld(world),
                                          handler.getHolderOfWorld(world));
        }
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType>",
                     output="net.minecraft.world.level.dimension.DimensionType")
    public static Object fromHolderToDimensionType(Object holder) {
        return handler.getValue(holder);
    }

    @ConverterMethod(input="net.minecraft.world.level.dimension.DimensionType",
                     output="net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType>")
    public static Object fromDimensionTypeToHolder(Object dimensionManager) {
        synchronized (holdersByDimensionType) {
            Object holder = holdersByDimensionType.get(dimensionManager);
            if (holder == null) {
                throw new IllegalArgumentException("Unknown or unregistered dimension type");
            }
            return holder;
        }
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent>")
    public static ResourceKey<SoundEffect> soundEffectHolderToResourceKey(Object nmsHolderHandle) {
        return ResourceKey.fromResourceKeyHandle(handler.getResourceKey(nmsHolderHandle));
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent>")
    public static Object soundEffectHolderFromResourceKey(ResourceKey<SoundEffect> soundKey) {
        return SoundEventHandle.T.rawSoundEffectResourceKeyToHolder.invoke(soundKey.getRawHandle());
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>")
    public static Holder<MobEffectHandle> wrapMobEffectHolder(Object nmsHolder) {
        return Holder.fromHandle(nmsHolder, MobEffectHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>")
    public static Object unwrapMobEffectHolder(Holder<MobEffectHandle> holder) {
        return holder.toRawHolder();
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>")
    public static Holder<AttributeHandle> wrapAttributeHolder(Object nmsHolder) {
        return Holder.fromHandle(nmsHolder, AttributeHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>")
    public static Object unwrapAttributeHolder(Holder<AttributeHandle> holder) {
        return holder.toRawHolder();
    }

    @Template.Optional
    @Template.Import("org.bukkit.craftbukkit.CraftWorld")
    @Template.Import("net.minecraft.world.level.dimension.DimensionType")
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
         *     return ((net.minecraft.world.level.Level) world.getHandle()).dimensionTypeRegistration();
         * }
         */
        @Template.Generated("%GET_HOLDER_OF_WORLD%")
        public abstract Object getHolderOfWorld(World world);
    }
}
