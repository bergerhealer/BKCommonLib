package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.generated.net.minecraft.network.syncher.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectListHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

/**
 * Conversions only used on MC 1.8.8
 */
public class MC1_8_8_Conversion {

    @ConverterMethod()
    public static int dataWatcherObjectToId(com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<?> dataWatcherObject) {
        return ((com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<?>) dataWatcherObject).getId();
    }

    @ConverterMethod()
    public static com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock createProxyDataPaletteBlock(char[] data) {
        return new com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock(data);
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffectList")
    public static Object createMobEffectListFromId(Integer id) {
        return MobEffectListHandle.T.fromId.raw.invoker.invoke(null, id);
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffectList")
    public static Integer getMobEffectListId(Object mobEffectListHandle) {
        return (Integer) MobEffectListHandle.T.getId.raw.invoker.invoke(mobEffectListHandle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.DataWatcher.WatchableObject")
    public static com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<?> watchableObjectToItem(Object watchableObject) {
        DataWatcherHandle.ItemHandle handle = DataWatcherHandle.ItemHandle.createHandle(watchableObject);
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<Object>(handle);
    }

    @ConverterMethod()
    public static com.bergerkiller.bukkit.common.internal.proxy.SoundEffect_1_8_8 soundEffectFromName(String name) {
        return new com.bergerkiller.bukkit.common.internal.proxy.SoundEffect_1_8_8(MinecraftKeyHandle.createNew(name));
    }
}
