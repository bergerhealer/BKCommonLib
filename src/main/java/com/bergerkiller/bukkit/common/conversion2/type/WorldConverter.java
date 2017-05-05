package com.bergerkiller.bukkit.common.conversion2.type;

import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.mountiplex.conversion2.annotations.ConverterMethod;

public class WorldConverter {

    @ConverterMethod(input="net.minecraft.server.World")
    public static org.bukkit.World toWorld(Object nmsWorldHandle) {
        return ((net.minecraft.server.v1_11_R1.World) nmsWorldHandle).getWorld();
    }

    @ConverterMethod(output="net.minecraft.server.World")
    public static Object toWorldHandle(org.bukkit.World world) {
        return ((CraftWorld) world).getHandle();
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcherObject<T>")
    public static <T> DataWatcher.Key<T> toKey(Object dataWatcherObject) {
        return new DataWatcher.Key<T>(dataWatcherObject);
    }

    @ConverterMethod(output="net.minecraft.server.DataWatcherObject<T>")
    public static <T> Object toKeyHandle(DataWatcher.Key<T> key) {
        return key.getHandle();
    }

    @ConverterMethod(input="net.minecraft.server.Entity")
    public static Entity toEntity(Object nmsEntityHandle) {
        return ((net.minecraft.server.v1_11_R1.Entity) nmsEntityHandle).getBukkitEntity();
    }

    @ConverterMethod(output="net.minecraft.server.Entity")
    public static Object toEntityHandle(Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }
}
