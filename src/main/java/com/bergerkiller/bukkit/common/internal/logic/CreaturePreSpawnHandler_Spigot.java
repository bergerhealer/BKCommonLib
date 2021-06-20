package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.events.CommonEventFactory;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkProviderServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.biome.BiomeSettingsMobsHandle.SpawnRateHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Hooks the ChunkGenerator field of the ChunkProviderServer
 * to override the getMobsFor method, in which we fire our event.
 * Not used since Paper 1.13 added their own event.
 */
public class CreaturePreSpawnHandler_Spigot extends CreaturePreSpawnHandler {
    private final SafeField<Object> cpsChunkGeneratorField;
    private final Class<? extends ClassHook<?>> hookType;
    private final Function<World, ? extends ClassHook<?>> hookConstructor;

    public CreaturePreSpawnHandler_Spigot() throws Throwable {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            cpsChunkGeneratorField = CommonUtil.unsafeCast(SafeField.create(ChunkProviderServerHandle.T.getType(),
                    "generator", CommonUtil.getClass("net.minecraft.world.level.chunk.ChunkGenerator")));
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            cpsChunkGeneratorField = CommonUtil.unsafeCast(SafeField.create(ChunkProviderServerHandle.T.getType(),
                    "chunkGenerator", CommonUtil.getClass("net.minecraft.world.level.chunk.ChunkGenerator")));
        } else {
            cpsChunkGeneratorField = CommonUtil.unsafeCast(SafeField.create(ChunkProviderServerHandle.T.getType(),
                    "chunkProvider", CommonUtil.getClass("net.minecraft.world.level.chunk.IChunkProvider")));
        }

        if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            hookType = ChunkGeneratorHook_1_17.class;
            hookConstructor = ChunkGeneratorHook_1_17::new;
        } else if (CommonBootstrap.evaluateMCVersion("<", "1.16")) {
            hookType = ChunkGeneratorHook_1_8_to_1_15_2.class;
            hookConstructor = ChunkGeneratorHook_1_8_to_1_15_2::new;
        } else {
            hookType = ChunkGeneratorHook_1_16.class;
            hookConstructor = ChunkGeneratorHook_1_16::new;
        }
    }

    @Override
    public void onEventHasHandlers() {
        for (World world : WorldUtil.getWorlds()) {
            onWorldEnabled(world);
        }
    }

    @Override
    public void onWorldEnabled(World world) {
        // If nobody handles the event, do not do anything!
        if (!CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList())) {
            return;
        }

        Object cps = getCPS(world);
        Object generator = cpsChunkGeneratorField.get(cps);
        ClassHook<?> hook = ClassHook.get(generator, hookType);
        if (hook == null && generator != null) {
            if (Modifier.isFinal(generator.getClass().getModifiers())) {
                // Generally doesn't work anymore, this requires Paper to work!
                //System.err.println("Failed to hook " + world.getName() + ": Generator " + generator.getClass().getName() + " is final!");
            } else {
                try {
                    hook = hookConstructor.apply(world);
                    generator = hook.hook(generator);
                    cpsChunkGeneratorField.set(cps, generator);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onWorldDisabled(World world) {
        Object cps = getCPS(world);
        Object generator = cpsChunkGeneratorField.get(cps);
        ClassHook<?> hook = ClassHook.get(generator, hookType);
        if (hook != null) {
            try {
                generator = ClassHook.unhook(generator);
                cpsChunkGeneratorField.set(cps, generator);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static Object getCPS(org.bukkit.World world) {
        return WorldServerHandle.fromBukkit(world).getChunkProviderServer().getRaw();
    }

    private static List<Object> handleMobsFor(World world, Object blockposition, List<Object> mobs) {
        try {
            // First check if anyone is even interested in this information
            // There is no use wasting CPU time when no one handles the event!
            if (LogicUtil.nullOrEmpty(mobs) || !CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList())) {
                return mobs;
            }

            // Wrap the parameters and send the event along
            BlockPositionHandle pos = BlockPositionHandle.createHandle(blockposition);

            // Check all instances of SpawnRateHandle if they need to be cancelled
            // If they're cancelled, initialize a new List with those entries omitted
            CommonEventFactory eventFactory =  CommonPlugin.getInstance().getEventFactory();
            List<Object> result = mobs;
            int numMobs = mobs.size();
            for (int n = 0; n < numMobs; n++) {
                SpawnRateHandle handle = SpawnRateHandle.createHandle(mobs.get(n));
                EntityType entityType = CommonEntityType.byNMSEntityClass(handle.getEntityClass()).entityType;

                if (eventFactory.handleCreaturePreSpawn(world, pos.getX(), pos.getY(), pos.getZ(), entityType)) {
                    // Allowed, if the list we're returning is new (has cancelled spawns) then
                    // add this entry to the list.
                    if (result != mobs) {
                        result.add(handle.getRaw());
                    }
                } else if (result == mobs) {
                    // Create a new List of stuff, omit the entry that was cancelled
                    result = new ArrayList<Object>();
                    result.addAll(mobs.subList(0, n));
                }
            }

            return result;
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to handle mob pre-spawn event", t);
            return mobs;
        }
    }

    @ClassHook.HookPackage("net.minecraft.world.level.chunk")
    @ClassHook.HookImport("net.minecraft.util.random.WeightedRandomList")
    @ClassHook.HookImport("net.minecraft.world.level.biome.BiomeSettingsMobs")
    @ClassHook.HookImport("net.minecraft.world.level.StructureManager")
    @ClassHook.HookImport("net.minecraft.world.entity.EnumCreatureType")
    @ClassHook.HookImport("net.minecraft.core.BlockPosition")
    public static class ChunkGeneratorHook_1_17 extends ClassHook<ChunkGeneratorHook_1_17> {
        private static final WeightedRandomListHandle weightedRandomListHandle = Template.Class.create(WeightedRandomListHandle.class, Common.TEMPLATE_RESOLVER);
        private final World world;

        public ChunkGeneratorHook_1_17(World world) {
            this.world = world;
        }

        @HookMethod("public WeightedRandomList getMobsFor(BiomeSettingsMobs biome, StructureManager structManager, EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
        public Object getMobsFor(Object biomeBase, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = base.getMobsFor(biomeBase, structureManager, enumcreaturetype, blockposition);
            List<Object> mobs = weightedRandomListHandle.extractList(weightedList);
            mobs = handleMobsFor(this.world, blockposition, mobs);
            return weightedRandomListHandle.createWeightedRandomList(mobs);
        }

        @Template.InstanceType("net.minecraft.util.random.WeightedRandomList")
        public static abstract class WeightedRandomListHandle extends Template.Class<Template.Handle> {

            /*
             * <CREATE_WEIGHTED_RANDOM_LIST>
             * public static WeightedRandomList createWeightedRandomList(List<?> list) {
             *     return WeightedRandomList.a(list);
             * }
             */
            @Template.Generated("%CREATE_WEIGHTED_RANDOM_LIST%")
            public abstract Object createWeightedRandomList(List<?> list);

            /*
             * <EXTRACT_LIST>
             * public static List extractList(WeightedRandomList weightedRandomList) {
             *     return weightedRandomList.d();
             * }
             */
            @Template.Generated("%EXTRACT_LIST%")
            public abstract List<Object> extractList(Object weightedRandomList);
        }
    }

    @ClassHook.HookPackage("net.minecraft.server")
    public static class ChunkGeneratorHook_1_16 extends ClassHook<ChunkGeneratorHook_1_16> {
        private final World world;

        public ChunkGeneratorHook_1_16(World world) {
            this.world = world;
        }

        @HookMethod("public List getMobsFor(BiomeSettingsMobs biome, StructureManager structManager, EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
        public List<Object> getMobsFor(Object biomeBase, Object structureManager, Object enumcreaturetype, Object blockposition) {
            List<Object> mobs = base.getMobsFor(biomeBase, structureManager, enumcreaturetype, blockposition);
            return handleMobsFor(this.world, blockposition, mobs);
        }
    }

    @ClassHook.HookPackage("net.minecraft.server")
    public static class ChunkGeneratorHook_1_8_to_1_15_2 extends ClassHook<ChunkGeneratorHook_1_8_to_1_15_2> {
        private final World world;

        public ChunkGeneratorHook_1_8_to_1_15_2(World world) {
            this.world = world;
        }

        @HookMethod("public List getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
        public List<Object> getMobsFor(Object enumcreaturetype, Object blockposition) {
            List<Object> mobs = base.getMobsFor(enumcreaturetype, blockposition);
            return handleMobsFor(this.world, blockposition, mobs);
        }
    }
}
