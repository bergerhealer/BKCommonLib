package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

/**
 * Conversions only used on MC 1.8.8
 */
public class MC1_8_8_Conversion {

    @ConverterMethod()
    public int dataWatcherObjectToId(com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<?> dataWatcherObject) {
        return ((com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<?>) dataWatcherObject).getId();
    }

    @ConverterMethod()
    public com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<?> dataWatcherObjectFromId(int id) {
        return new com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject<Object>(id);
    }

    @ConverterMethod()
    public static com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock createProxyDataPaletteBlock(char[] data) {
        return new com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock(data);
    }

    @ConverterMethod()
    public static com.bergerkiller.bukkit.common.internal.proxy.MobEffectList createMobEffectListFromId(Integer id) {
        return new com.bergerkiller.bukkit.common.internal.proxy.MobEffectList(id);
    }

    @ConverterMethod()
    public static Integer getMobEffectListId(com.bergerkiller.bukkit.common.internal.proxy.MobEffectList list) {
        return list.getId();
    }
}
