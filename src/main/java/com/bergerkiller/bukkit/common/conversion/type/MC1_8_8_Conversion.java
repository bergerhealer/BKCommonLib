package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.network.syncher.SynchedEntityDataHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
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

    @ConverterMethod
    public static Holder<MobEffectHandle> createMobEffectListHolderFromId(Integer id) {
        return Holder.directWrap(MobEffectHandle.T.fromId.raw.invoker.invoke(null, id),
                MobEffectHandle::createHandle);
    }

    @ConverterMethod
    public static Integer getMobEffectListIdFromHolder(Holder<MobEffectHandle> holder) {
        return getMobEffectListId(holder.rawValue());
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffect")
    public static Object createMobEffectListFromId(Integer id) {
        return MobEffectHandle.T.fromId.raw.invoker.invoke(null, id);
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffect")
    public static Integer getMobEffectListId(Object mobEffectListHandle) {
        return (Integer) MobEffectHandle.T.getId.raw.invoker.invoke(null, mobEffectListHandle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.SynchedEntityData.WatchableObject")
    public static com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<?> watchableObjectToItem(Object watchableObject) {
        SynchedEntityDataHandle.DataItemHandle handle = SynchedEntityDataHandle.DataItemHandle.createHandle(watchableObject);
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<Object>(handle);
    }

    @ConverterMethod()
    public static com.bergerkiller.bukkit.common.internal.proxy.SoundEffect_1_8_8 soundEffectFromName(String name) {
        return new com.bergerkiller.bukkit.common.internal.proxy.SoundEffect_1_8_8(IdentifierHandle.createNew(name));
    }
}
