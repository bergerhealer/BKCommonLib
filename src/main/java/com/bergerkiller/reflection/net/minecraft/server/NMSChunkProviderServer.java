package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.generated.net.minecraft.server.ChunkProviderServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.World;

/**
 * Deprecated: use ChunkProviderServerHandle instead
 */
@Deprecated
public class NMSChunkProviderServer {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ChunkProviderServer")
            .addImport("it.unimi.dsi.fastutil.longs.Long2ObjectMap");

    public static final FieldAccessor<Object> chunkLoader     =  ChunkProviderServerHandle.T.chunkLoader.toFieldAccessor();
    public static final TranslatorFieldAccessor<World> world  =  ChunkProviderServerHandle.T.world.raw.toFieldAccessor().translate(DuplexConversion.world);
    public static final MethodAccessor<Boolean> isChunkLoaded =  ChunkProviderServerHandle.T.isLoaded.toMethodAccessor();
}
