package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;

import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.BiomeBaseHandle.BiomeMetaHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkProviderServerHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.ClassHook;

/**
 * This hook is used exclusively for the 'getMobsFor' function, which enables
 * pre-creature-spawn handling to reduce CPU usage when disabling entity spawns.
 */
public class ChunkProviderServerHook extends ClassHook<ChunkProviderServerHook> {

    public static void hook(org.bukkit.World world) {
        Object cps = getCPS(world);
        ChunkProviderServerHook hook = ChunkProviderServerHook.get(cps, ChunkProviderServerHook.class);
        if (hook == null) {
            try {
                hook = new ChunkProviderServerHook();
                cps = hook.hook(cps);
                setCPS(world, ChunkProviderServerHandle.createHandle(cps));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void unhook(org.bukkit.World world) {
        Object cps = getCPS(world);
        ChunkProviderServerHook hook = ChunkProviderServerHook.get(cps, ChunkProviderServerHook.class);
        if (hook != null) {
            try {
                cps = ChunkProviderServerHook.unhook(cps);
                setCPS(world, ChunkProviderServerHandle.createHandle(cps));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static Object getCPS(org.bukkit.World world) {
        return WorldServerHandle.fromBukkit(world).getChunkProviderServer().getRaw();
    }

    private static void setCPS(org.bukkit.World world, ChunkProviderServerHandle cps) {
        WorldServerHandle.fromBukkit(world).setChunkProviderServer(cps);
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
}
