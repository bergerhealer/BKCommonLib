package com.bergerkiller.bukkit.common.proxies;

import net.friwi.reflection.ChunkPosition;
import net.minecraft.server.v1_9_R1.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_9_R1.*;

import java.util.List;

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

    public ChunkProviderServerProxy(Object worldServer, Object iChunkLoader, ChunkGenerator iChunkProvider, Object base) {
        super((WorldServer) worldServer, (IChunkLoader) iChunkLoader, (ChunkGenerator) iChunkProvider);
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
    
    public boolean canSave(){
    	return base.e();
    }

    public boolean super_canSave(){
    	return super.e();
    }

    @Deprecated
    public ChunkPosition _findNearestMapFeature(World world, String s, BlockPosition pos) {
        return new ChunkPosition(base.a(world, s, pos));
    }

    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition pos) {
        return new BlockPosition(base.a(world, s, pos));
    }

    @Deprecated
    public ChunkPosition findNearestMapFeature(World world, String s, int i, int j, int k) {
        return new ChunkPosition(base.a(world, s, new BlockPosition(i, j, k)));
    }

    @Deprecated
    public ChunkPosition super_findNearestMapFeature(World world, String s, int i, int j, int k) {
        return new ChunkPosition(super.a(world, s, new BlockPosition(i, j, k)));
    }

    @Deprecated
    public ChunkPosition _super_findNearestMapFeature(World world, String s, BlockPosition pos) {
        return new ChunkPosition(super.a(world, s, pos));
    }

    public BlockPosition super_findNearestMapFeature(World world, String s, BlockPosition pos) {
        return new BlockPosition(super.a(world, s, pos));
    }

    public void getChunkAt(IChunkProvider arg0, int arg1, int arg2) {
        base.getChunkAt(arg1, arg2, null);
    }

    public void super_getChunkAt(IChunkProvider arg0, int arg1, int arg2) {
        super.getChunkAt(arg1, arg2, null);
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

    public int getLoadedChunks() {
        return base.chunks.size();
    }

    public int super_getLoadedChunks() {
        return super.chunks.size();
    }

    public List<BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition pos) {
        return base.a(enumcreaturetype, pos);
    }

    @Deprecated
    public List<BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, int i, int j, int k) {
        return base.a(enumcreaturetype, new BlockPosition(i, j, k));
    }

    public List<BiomeMeta> super_getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition pos) {
        return super.a(enumcreaturetype, pos);
    }

    public List<BiomeMeta> super_getMobsFor(EnumCreatureType enumcreaturetype, int i, int j, int k) {
        return super.a(enumcreaturetype, new BlockPosition(i, j, k));
    }

    @Override
    public String getName() {
        return base.getName();
    }

    public String super_getName() {
        return super.getName();
    }

    public Chunk getOrCreateChunk(int x, int z) {
        return base.getOrLoadChunkAt(x, z);
    }

    public Chunk super_getOrCreateChunk(int x, int z) {
        return super.getOrLoadChunkAt(x, z);
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

    public void recreateStructures(Chunk chunk, int x, int z) {
        base.chunkGenerator.recreateStructures(chunk, x, z);
    }

    public void super_recreateStructures(Chunk chunk, int x, int z) {
        super.chunkGenerator.recreateStructures(chunk, x, z);
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

//    @Override
//    public boolean saveChunks(boolean arg0, IProgressUpdate arg1) {
//        return base.e(arg0, arg1);
//    }
//
//    public boolean super_saveChunks(boolean arg0, IProgressUpdate arg1) {
//        return super.e(arg0, arg1);
//    }

    @Override
    public boolean unloadChunks() {
        return base.unloadChunks();
    }

    public boolean super_unloadChunks() {
        return super.unloadChunks();
    }
}
