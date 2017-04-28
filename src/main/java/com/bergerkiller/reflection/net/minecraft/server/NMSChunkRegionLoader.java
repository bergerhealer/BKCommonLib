package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import org.bukkit.World;

public class NMSChunkRegionLoader {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ChunkRegionLoader");

    private static final MethodAccessor<Boolean> chunkExists = T.getMethod("chunkExists", NMSWorld.T.getType(), int.class, int.class);

    public static boolean chunkExists(Object chunkRegionLoader, World world, int x, int z) {
        return chunkExists.invoke(chunkRegionLoader, Conversion.toWorldHandle.convert(world), x, z);
    }
}
