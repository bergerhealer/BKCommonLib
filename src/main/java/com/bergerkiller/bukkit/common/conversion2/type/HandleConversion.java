package com.bergerkiller.bukkit.common.conversion2.type;

import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;

import com.bergerkiller.mountiplex.conversion2.annotations.ConverterMethod;

public class HandleConversion {

    /*
     * HANDLE CONVERTERS!!!
     */
    
    @ConverterMethod(output="net.minecraft.server.Entity")
    public static Object toEntityHandle(org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.World")
    public static Object toWorldHandle(org.bukkit.World world) {
        return ((CraftWorld) world).getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.Chunk")
    public static Object toChunkHandle(org.bukkit.Chunk chunk) {
        return ((CraftChunk) chunk).getHandle();
    }

    @ConverterMethod(output="net.minecraft.server.DataWatcherObject<T>")
    public static <T> Object toKeyHandle(com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T> key) {
        return key.getHandle();
    }

}
