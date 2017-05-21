package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.BiomeBaseHandle.BiomeMetaHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkProviderServerHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.ClassHook;

public class ChunkProviderServerHook extends ClassHook<ChunkProviderServerHook> {

    public static void hook(org.bukkit.World world) {
        Object cps = getCPS(world);
        ChunkProviderServerHook hook = ChunkProviderServerHook.get(cps, ChunkProviderServerHook.class);
        if (hook == null) {
            hook = new ChunkProviderServerHook();
            cps = hook.hook(cps);
            setCPS(world, cps);
        }
    }

    public static void unhook(org.bukkit.World world) {
        Object cps = getCPS(world);
        ChunkProviderServerHook hook = ChunkProviderServerHook.get(cps, ChunkProviderServerHook.class);
        if (hook != null) {
            cps = ChunkProviderServerHook.unhook(cps);
            setCPS(world, cps);
        }
    }

    private static Object getCPS(org.bukkit.World world) {
        return WorldServerHandle.fromBukkit(world).getChunkProviderServer().getRaw();
    }

    private static void setCPS(org.bukkit.World world, Object cps) {
        WorldHandle.T.chunkProvider.raw.set(Conversion.toWorldHandle.convert(world), cps);
    }

    private final org.bukkit.World getWorld() {
        return WrapperConversion.toWorld(ChunkProviderServerHandle.T.world.raw.get(instance()));
    }

    @HookMethod("public List<BiomeBase.BiomeMeta> getBiomeSpawnInfo:???(EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
    public List<?> getMobsFor(Object enumcreaturetype, Object blockposition) {
        List<?> mobs = base.getMobsFor(enumcreaturetype, blockposition);
        if (CommonPlugin.hasInstance()) {
            // First check if anyone is even interested in this information
            // There is no use wasting CPU time when no one handles the event!
            if (LogicUtil.nullOrEmpty(mobs) || !CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList())) {
                return mobs;
            }
            // Wrap the parameters and send the event along
            BlockPositionHandle pos = BlockPositionHandle.createHandle(blockposition);
            org.bukkit.World world = getWorld();
            List<BiomeMetaHandle> mobsHandles = new ConvertingList<BiomeMetaHandle>(mobs, BiomeMetaHandle.T.getHandleConverter());
            mobsHandles = CommonPlugin.getInstance().getEventFactory().handleCreaturePreSpawn(world, 
                    pos.getX(), pos.getY(), pos.getZ(), mobsHandles);

            return new ConvertingList<Object>(mobsHandles, BiomeMetaHandle.T.getHandleConverter().reverse());
        } else {
            return mobs;
        }
    }

    @HookMethod("public Chunk loadChunk(int cx, int cz)")
    public Object loadChunk(int cx, int cz) {
        // Perform chunk load from file timings
        if (!CommonPlugin.TIMINGS.isActive()) {
            return base.loadChunk(cx, cz);
        } else {
            long time = System.nanoTime();
            Object nmsChunk = base.loadChunk(cx, cz);
            if (nmsChunk != null) {
                time = System.nanoTime() - time;
                CommonPlugin.TIMINGS.onChunkLoad(Conversion.toChunk.convert(nmsChunk), time);
            }
            return nmsChunk;
        }
    }

    @HookMethod("public Chunk getChunkAt(int cx, int cz, Runnable runnable, boolean generate)")
    public Object getChunkAt(int cx, int cz, Runnable runnable, boolean generate) {
        if (!CommonPlugin.TIMINGS.isActive()) {
            return base.getChunkAt(cx, cz, runnable, generate);
        }

        // First check if we can fetch the chunk right away (this is not loading!)
        Object nmsChunk = ChunkProviderServerHandle.T.getChunkIfLoaded.raw.invoke(instance(), cx, cz);
        if (nmsChunk != null) {
            if (runnable != null) {
                runnable.run();
            }
            return nmsChunk;
        }

        // We need to load or generate the chunk - time it
        long time = System.nanoTime();
        nmsChunk = base.getChunkAt(cx, cz, runnable, generate);
        CommonPlugin.TIMINGS.onChunkLoad(Conversion.toChunk.convert(nmsChunk), System.nanoTime() - time);
        return nmsChunk;
    }

    @HookMethod("public boolean unloadChunks()")
    public boolean unloadChunks() {
        if (!CommonPlugin.TIMINGS.isActive()) {
            return base.unloadChunks();
        }

        long time = System.nanoTime();
        try {
            return base.unloadChunks();
        } finally {
            time = System.nanoTime() - time;
            org.bukkit.World world = Conversion.toWorld.convert(ChunkProviderServerHandle.T.world.raw.get(instance()));
            CommonPlugin.TIMINGS.onChunkUnloading(world, time);
        }
    }
}
