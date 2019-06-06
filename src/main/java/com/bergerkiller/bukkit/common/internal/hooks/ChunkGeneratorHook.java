package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkProviderServerHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.server.BiomeBaseHandle.BiomeMetaHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;

public class ChunkGeneratorHook extends ClassHook<ChunkGeneratorHook> {
    private static final SafeField<Object> cpsChunkGeneratorField;
    private final World world;

    static {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            cpsChunkGeneratorField = CommonUtil.unsafeCast(SafeField.create(ChunkProviderServerHandle.T.getType(),
                    "chunkGenerator", CommonUtil.getNMSClass("ChunkGenerator")));
        } else {
            cpsChunkGeneratorField = CommonUtil.unsafeCast(SafeField.create(ChunkProviderServerHandle.T.getType(),
                    "chunkProvider", CommonUtil.getNMSClass("IChunkProvider")));
        }
    }

    public ChunkGeneratorHook(World world) {
        this.world = world;
    }

    public static void hook(org.bukkit.World world) {
        Object cps = getCPS(world);
        Object generator = cpsChunkGeneratorField.get(cps);
        ChunkGeneratorHook hook = ChunkGeneratorHook.get(generator, ChunkGeneratorHook.class);
        if (hook == null) {
            try {
                hook = new ChunkGeneratorHook(world);
                generator = hook.hook(generator);
                cpsChunkGeneratorField.set(cps, generator);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void unhook(org.bukkit.World world) {
        Object cps = getCPS(world);
        Object generator = cpsChunkGeneratorField.get(cps);
        ChunkGeneratorHook hook = ChunkGeneratorHook.get(generator, ChunkGeneratorHook.class);
        if (hook != null) {
            try {
                generator = ChunkGeneratorHook.unhook(generator);
                cpsChunkGeneratorField.set(cps, generator);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static Object getCPS(org.bukkit.World world) {
        return WorldServerHandle.fromBukkit(world).getChunkProviderServer().getRaw();
    }

    @HookMethod("public List getMobsFor(net.minecraft.server.EnumCreatureType enumcreaturetype, net.minecraft.server.BlockPosition blockposition)")
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
            List<BiomeMetaHandle> mobsHandles = new ConvertingList<BiomeMetaHandle>(mobs, BiomeMetaHandle.T.getHandleConverter());
            mobsHandles = CommonPlugin.getInstance().getEventFactory().handleCreaturePreSpawn(this.world, 
                    pos.getX(), pos.getY(), pos.getZ(), mobsHandles);

            return new ConvertingList<Object>(mobsHandles, BiomeMetaHandle.T.getHandleConverter().reverse());
        } else {
            return mobs;
        }
    }
}
