package com.bergerkiller.server.bases;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldServer;
import com.bergerkiller.server.proxies.ChunkProviderServerProxy;

import net.minecraft.server.v1_11_R1.Chunk;

/**
 * This class is mainly used by NoLagg chunks - for compatibilities' sake, it is
 * ported to here.
 */
public class DummyChunkProviderServer extends ChunkProviderServerProxy {

    DummyChunkProviderServer(Object worldHandle) {
        super(worldHandle, null, null, null);
    }

    public void setBase(org.bukkit.World world) {
        setProxyBase(NMSWorldServer.chunkProviderServer.get(Conversion.toWorldHandle.convert(world)));
    }

    @Override
    public Chunk getChunkAt(int x, int z, Runnable task) {
        return null;
    }
}
