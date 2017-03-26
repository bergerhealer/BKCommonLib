package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;
import com.bergerkiller.reflection.TranslatorFieldAccessor;

import org.bukkit.World;

public class NMSChunkProviderServer {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ChunkProviderServer");
    
    public static final FieldAccessor<Object> chunkLoader     =  T.selectField("private final IChunkLoader chunkLoader");
    public static final FieldAccessor<Object> chunks          =  T.selectField("public final it.unimi.dsi.fastutil.longs.Long2ObjectMap chunks");
    public static final FieldAccessor<Object> unloadQueue     =  T.selectField("public final Set unloadQueue");
    public static final TranslatorFieldAccessor<World> world  =  T.selectField("public final WorldServer world").translate(ConversionPairs.world);
    public static final MethodAccessor<Boolean> isChunkLoaded = T.selectMethod("public boolean isLoaded(int x, int z)");
}
