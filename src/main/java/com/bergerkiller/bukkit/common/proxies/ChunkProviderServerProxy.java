package com.bergerkiller.bukkit.common.proxies;

import java.util.List;
import net.minecraft.server.v1_8_R1.BlockPosition;

import net.minecraft.server.v1_8_R1.Chunk;
//import net.minecraft.server.v1_8_R1.ChunkPosition;
import net.minecraft.server.v1_8_R1.ChunkProviderServer;
import net.minecraft.server.v1_8_R1.EnumCreatureType;
import net.minecraft.server.v1_8_R1.IChunkLoader;
import net.minecraft.server.v1_8_R1.IChunkProvider;
import net.minecraft.server.v1_8_R1.IProgressUpdate;
import net.minecraft.server.v1_8_R1.World;
import net.minecraft.server.v1_8_R1.WorldServer;

/**
 * A chunk provider server proxy class. To call methods in the base class, call
 * the regular methods. To call methods from the current implementation, call
 * the super_ methods.
 */
public class ChunkProviderServerProxy extends ChunkProviderServer implements Proxy<Object> {

    private ChunkProviderServer base;

    static {
        ProxyBase.validate(ChunkProviderServerProxy.class);
    }

    public ChunkProviderServerProxy(Object worldServer, Object iChunkLoader, Object iChunkProvider, Object base) {
        super((WorldServer) worldServer, (IChunkLoader) iChunkLoader, (IChunkProvider) iChunkProvider);
        setProxyBase(base);
    }

    @Override
    public Object getProxyBase() {
        return base;
    }

    @Override
    public void setProxyBase(Object chunkProviderServer) {
        base = (ChunkProviderServer) chunkProviderServer;
    }

    @Override
    public void b() {
        base.b();
    }

    public void super_b() {
        super.b();
    }

    @Override
    public boolean canSave() {
        return base.canSave();
    }

    public boolean super_canSave() {
        return super.canSave();
    }

    @Override
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition) {
        return base.chunkProvider.findNearestMapFeature(world, s, blockposition);
    }

    public BlockPosition super_findNearestMapFeature(World world, String s, BlockPosition blockposition) {
        return super.chunkProvider.findNearestMapFeature(world, s, blockposition);
    }

    @Override
    public void getChunkAt(IChunkProvider arg0, int arg1, int arg2) {
        base.getChunkAt(arg0, arg1, arg2);
    }

    public void super_getChunkAt(IChunkProvider arg0, int arg1, int arg2) {
        super.getChunkAt(arg0, arg1, arg2);
    }

    @Override
    public Chunk getChunkAt(int x, int z, Runnable task) {
        return base.getChunkAt(x, z, task);
    }

    public Chunk super_getChunkAt(int x, int z, Runnable task) {
        return super.getChunkAt(x, z, task);
    }

    @Override
    public Chunk getChunkAt(int x, int z) {
        return base.getChunkAt(x, z);
    }

    public Chunk super_getChunkAt(int x, int z) {
        return super.getChunkAt(x, z);
    }

    @Override
    public int getLoadedChunks() {
        return base.getLoadedChunks();
    }

    public int super_getLoadedChunks() {
        return super.getLoadedChunks();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return base.chunkProvider.getMobsFor(enumcreaturetype, blockposition);
    }

    @SuppressWarnings("rawtypes")
    public List super_getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return super.chunkProvider.getMobsFor(enumcreaturetype, blockposition);
    }

    @Override
    public String getName() {
        return base.getName();
    }

    public String super_getName() {
        return super.getName();
    }

    @Override
    public Chunk getOrCreateChunk(int x, int z) {
        return base.getOrCreateChunk(x, z);
    }

    public Chunk super_getOrCreateChunk(int x, int z) {
        return super.getOrCreateChunk(x, z);
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return base.isChunkLoaded(x, z);
    }

    public boolean super_isChunkLoaded(int x, int z) {
        return super.isChunkLoaded(x, z);
    }

    @Override
    public Chunk loadChunk(int x, int z) {
        return base.loadChunk(x, z);
    }

    public Chunk super_loadChunk(int x, int z) {
        return super.loadChunk(x, z);
    }

    @Override
    public void queueUnload(int x, int z) {
        base.queueUnload(x, z);
    }

    public void super_queueUnload(int x, int z) {
        super.queueUnload(x, z);
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {
        base.recreateStructures(chunk, x, z);
    }

    public void super_recreateStructures(Chunk chunk, int x, int z) {
        super.recreateStructures(chunk, x, z);
    }

    @Override
    public void saveChunk(Chunk chunk) {
        base.saveChunk(chunk);
    }

    public void super_saveChunk(Chunk chunk) {
        super.saveChunk(chunk);
    }

    @Override
    public void saveChunkNOP(Chunk chunk) {
        base.saveChunkNOP(chunk);
    }

    public void super_saveChunkNOP(Chunk chunk) {
        super.saveChunkNOP(chunk);
    }

    @Override
    public boolean saveChunks(boolean arg0, IProgressUpdate arg1) {
        return base.saveChunks(arg0, arg1);
    }

    public boolean super_saveChunks(boolean arg0, IProgressUpdate arg1) {
        return super.saveChunks(arg0, arg1);
    }

    @Override
    public boolean unloadChunks() {
        return base.unloadChunks();
    }

    public boolean super_unloadChunks() {
        return super.unloadChunks();
    }
    
    @Override
    public Chunk getChunkAt(BlockPosition blockposition) {
        return base.getChunkAt(blockposition);
    }
    
    public Chunk super_getChunkAt(BlockPosition blockposition) {
        return super.getChunkAt(blockposition);
    }
}
