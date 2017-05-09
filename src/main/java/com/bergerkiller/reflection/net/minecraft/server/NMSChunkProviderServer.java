package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.World;

public class NMSChunkProviderServer {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ChunkProviderServer")
            .addImport("it.unimi.dsi.fastutil.longs.Long2ObjectMap");

    public static final FieldAccessor<Object> chunkLoader     =  T.selectField("private final IChunkLoader chunkLoader");
    public static final FieldAccessor<Object> chunks          =  T.selectField("public final Long2ObjectMap<Chunk> chunks");
    public static final FieldAccessor<Object> unloadQueue     =  T.selectField("public final Set<Long> unloadQueue");
    public static final TranslatorFieldAccessor<World> world  =  T.selectField("public final WorldServer world").translate(DuplexConversion.world);
    public static final MethodAccessor<Boolean> isChunkLoaded =  T.selectMethod("public boolean isLoaded(int x, int z)");
}
