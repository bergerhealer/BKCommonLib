package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

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
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Hooks the ChunkGenerator field of the ChunkProviderServer
 * to override the getMobsFor method, in which we fire our event.
 * Not used since Paper 1.13 added their own event.
 * 
 * Does not work on version 1.18 and later. On there, for Spigot, we just
 * don't optimize it. Only handles NATURAL spawn causes.
 */
public class CreaturePreSpawnHandler_Spigot extends CreaturePreSpawnHandler {
    private final SafeField<Object> cpsChunkGeneratorField;
    private final Class<? extends ClassHook<?>> hookType;
    private final Function<World, ? extends ClassHook<?>> hookConstructor;

    private static final BiomeSpawnClusterHandle spawnClusterHandle = LogicUtil.tryCreate(
            () -> {
                BiomeSpawnClusterHandle h = Template.Class.create(BiomeSpawnClusterHandle.class, Common.TEMPLATE_RESOLVER);
                h.forceInitialization();;
                return h;
            },
            (err) -> {
                Logging.LOGGER.log(Level.SEVERE, "Failed to initialize template for the mob spawn cluster", err);
                return null;
            });

    public CreaturePreSpawnHandler_Spigot() throws Throwable {
        if (spawnClusterHandle == null) {
            throw new UnsupportedOperationException("Spawn cluster template is not available");
        }

        if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            cpsChunkGeneratorField = LogicUtil.unsafeCast(SafeField.create(ChunkProviderServerHandle.T.getType(),
                    "generator", CommonUtil.getClass("net.minecraft.world.level.chunk.ChunkGenerator")));
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            cpsChunkGeneratorField = LogicUtil.unsafeCast(SafeField.create(ChunkProviderServerHandle.T.getType(),
                    "chunkGenerator", CommonUtil.getClass("net.minecraft.world.level.chunk.ChunkGenerator")));
        } else {
            cpsChunkGeneratorField = LogicUtil.unsafeCast(SafeField.create(ChunkProviderServerHandle.T.getType(),
                    "chunkProvider", CommonUtil.getClass("net.minecraft.world.level.chunk.IChunkProvider")));
        }

        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.5")) {
            hookType = ChunkGeneratorHook_1_21_5.class;
            hookConstructor = ChunkGeneratorHook_1_21_5::new;
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
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
                    Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error hooking CreaturePreSpawnHandler", t);
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
                Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error un-hooking CreaturePreSpawnHandler", t);
            }
        }
    }

    private static Object getCPS(org.bukkit.World world) {
        return WorldServerHandle.fromBukkit(world).getChunkProviderServer().getRaw();
    }

    private static List<Object> handleMobsFor(World world, Object blockposition, List<Object> mobs, Function<Object, CommonEntityType> entityTypeFunc) {
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
                Object rawSpawnCluster = mobs.get(n);
                EntityType entityType = entityTypeFunc.apply(rawSpawnCluster).entityType;

                if (eventFactory.handleCreaturePreSpawn(world, pos.getX(), pos.getY(), pos.getZ(), entityType,
                        CreatureSpawnEvent.SpawnReason.NATURAL))
                {
                    // Allowed, if the list we're returning is new (has cancelled spawns) then
                    // add this entry to the list.
                    if (result != mobs) {
                        result.add(rawSpawnCluster);
                    }
                } else if (result == mobs) {
                    // Create a new List of stuff, omit the entry that was cancelled
                    result = new ArrayList<>(mobs.subList(0, n));
                }
            }

            return result;
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to handle mob pre-spawn event", t);
            return mobs;
        }
    }

    @ClassHook.HookPackage("net.minecraft.world.level.chunk")
    @ClassHook.HookImport("net.minecraft.util.random.WeightedList")
    @ClassHook.HookImport("net.minecraft.core.Holder")
    @ClassHook.HookImport("net.minecraft.world.level.biome.BiomeBase")
    @ClassHook.HookImport("net.minecraft.world.level.StructureManager")
    @ClassHook.HookImport("net.minecraft.world.entity.EnumCreatureType")
    @ClassHook.HookImport("net.minecraft.core.BlockPosition")
    public static class ChunkGeneratorHook_1_21_5 extends ClassHook<ChunkGeneratorHook_1_21_5> {
        private static final WeightedListHandle weightedListHandle = Template.Class.create(WeightedListHandle.class, Common.TEMPLATE_RESOLVER);
        private final World world;

        public ChunkGeneratorHook_1_21_5(World world) {
            this.world = world;
        }

        @HookMethod("public WeightedList getMobsAt(Holder<BiomeBase> biomeBaseHolder, StructureManager structManager, EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
        public Object getMobsAt_1_18_2(Object biomeBaseHolder, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = base.getMobsAt_1_18_2(biomeBaseHolder, structureManager, enumcreaturetype, blockposition);
            return processWeightedList(weightedList, blockposition);
        }

        private Object processWeightedList(Object weightedList, Object blockposition) {
            List<Object> weights = weightedListHandle.extractList(weightedList);
            List<Object> newMobWeights = handleMobsFor(this.world, blockposition, weights, weightedEntry -> {
                Object spawnCluster = weightedListHandle.getWeightedEntryValue(weightedEntry);
                return spawnClusterHandle.getEntityType(spawnCluster);
            });
            if (newMobWeights != weights) {
                return weightedListHandle.createWeightedList(newMobWeights);
            } else {
                return weightedList;
            }
        }

        @Template.InstanceType("net.minecraft.util.random.WeightedList")
        @Template.Import("net.minecraft.util.random.Weighted")
        public static abstract class WeightedListHandle extends Template.Class<Template.Handle> {

            /*
             * <CREATE_WEIGHTED_LIST>
             * public static WeightedList createWeightedList(List<Weighted<?>> list) {
             *     return WeightedList.of(list);
             * }
             */
            @Template.Generated("%CREATE_WEIGHTED_LIST%")
            public abstract Object createWeightedList(List<?> list);

            /*
             * <EXTRACT_WEIGHTED_LIST>
             * public static List extractList(WeightedList weightedList) {
             *     return weightedList.unwrap();
             * }
             */
            @Template.Generated("%EXTRACT_WEIGHTED_LIST%")
            public abstract List<Object> extractList(Object weightedList);

            /*
             * <GET_WEIGHTED_ENTRY_VALUE>
             * public static Object getWeightedEntryValue(Weighted weighted) {
             *     return weighted.value();
             * }
             */
            @Template.Generated("%GET_WEIGHTED_ENTRY_VALUE%")
            public abstract Object getWeightedEntryValue(Object weightedEntry);
        }
    }

    @ClassHook.HookPackage("net.minecraft.world.level.chunk")
    @ClassHook.HookImport("net.minecraft.util.random.WeightedRandomList")
    @ClassHook.HookImport("net.minecraft.core.Holder")
    @ClassHook.HookImport("net.minecraft.world.level.biome.BiomeBase")
    @ClassHook.HookImport("net.minecraft.world.level.StructureManager")
    @ClassHook.HookImport("net.minecraft.world.entity.EnumCreatureType")
    @ClassHook.HookImport("net.minecraft.core.BlockPosition")
    public static class ChunkGeneratorHook_1_17 extends ClassHook<ChunkGeneratorHook_1_17> {
        private static final WeightedRandomListHandle weightedRandomListHandle = Template.Class.create(WeightedRandomListHandle.class, Common.TEMPLATE_RESOLVER);
        private final World world;

        public ChunkGeneratorHook_1_17(World world) {
            this.world = world;
        }

        @HookMethodCondition("version >= 1.18.2")
        @HookMethod("public WeightedRandomList getMobsAt(Holder<BiomeBase> biomeBaseHolder, StructureManager structManager, EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
        public Object getMobsAt_1_18_2(Object biomeBaseHolder, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = base.getMobsAt_1_18_2(biomeBaseHolder, structureManager, enumcreaturetype, blockposition);
            return processWeightedList(weightedList, blockposition);
        }

        @HookMethodCondition("version >= 1.18 && version < 1.18.2")
        @HookMethod("public WeightedRandomList getMobsAt(BiomeBase biomeBase, StructureManager structManager, EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
        public Object getMobsAt_1_18(Object biomeBase, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = base.getMobsAt_1_18(biomeBase, structureManager, enumcreaturetype, blockposition);
            return processWeightedList(weightedList, blockposition);
        }

        @HookMethodCondition("version < 1.18")
        @HookMethod("public WeightedRandomList getMobsFor(BiomeBase biome, StructureManager structManager, EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
        public Object getMobsFor_1_17(Object biomeBase, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = base.getMobsFor_1_17(biomeBase, structureManager, enumcreaturetype, blockposition);
            return processWeightedList(weightedList, blockposition);
        }

        private Object processWeightedList(Object weightedList, Object blockposition) {
            List<Object> mobs = weightedRandomListHandle.extractList(weightedList);
            List<Object> newMobs = handleMobsFor(this.world, blockposition, mobs, spawnClusterHandle::getEntityType);
            if (newMobs != mobs) {
                return weightedRandomListHandle.createWeightedRandomList(newMobs);
            } else {
                return weightedList;
            }
        }

        @Template.InstanceType("net.minecraft.util.random.WeightedRandomList")
        public static abstract class WeightedRandomListHandle extends Template.Class<Template.Handle> {

            /*
             * <CREATE_WEIGHTED_RANDOM_LIST>
             * public static WeightedRandomList createWeightedRandomList(List<?> list) {
             * #if version >= 1.18
             *     return WeightedRandomList.create(list);
             * #else
             *     return WeightedRandomList.a(list);
             * #endif
             * }
             */
            @Template.Generated("%CREATE_WEIGHTED_RANDOM_LIST%")
            public abstract Object createWeightedRandomList(List<?> list);

            /*
             * <EXTRACT_RANDOM_WEIGHTED_LIST>
             * public static List extractList(WeightedRandomList weightedRandomList) {
             * #if version >= 1.18
             *     return weightedRandomList.unwrap();
             * #else
             *     return weightedRandomList.d();
             * #endif
             * }
             */
            @Template.Generated("%EXTRACT_RANDOM_WEIGHTED_LIST%")
            public abstract List<Object> extractList(Object weightedRandomList);
        }
    }

    @ClassHook.HookPackage("net.minecraft.server")
    @ClassHook.HookImport("net.minecraft.world.level.biome.BiomeBase")
    public static class ChunkGeneratorHook_1_16 extends ClassHook<ChunkGeneratorHook_1_16> {
        private final World world;

        public ChunkGeneratorHook_1_16(World world) {
            this.world = world;
        }

        @HookMethod("public List getMobsFor(BiomeBase biomeBase, StructureManager structManager, EnumCreatureType enumcreaturetype, BlockPosition blockposition)")
        public List<Object> getMobsFor(Object biomeBase, Object structureManager, Object enumcreaturetype, Object blockposition) {
            List<Object> mobs = base.getMobsFor(biomeBase, structureManager, enumcreaturetype, blockposition);
            return handleMobsFor(this.world, blockposition, mobs, spawnClusterHandle::getEntityType);
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
            return handleMobsFor(this.world, blockposition, mobs, spawnClusterHandle::getEntityType);
        }
    }

    // 1.8 - 1.16.1: net.minecraft.world.level.biome.BiomeBase.BiomeMeta
    // 1.17+: net.minecraft.world.level.biome.BiomeSettingsMobs$c
    @Template.Import("com.bergerkiller.bukkit.common.entity.CommonEntityType")
    @Template.Import("net.minecraft.world.entity.EntityTypes")
    @Template.InstanceType("net.minecraft.world.level.biome.BiomeSpawnCluster")
    public static abstract class BiomeSpawnClusterHandle extends Template.Class<Template.Handle> {

        /*
         * <GET_CLUSTER_ENTITY_TYPE>
         * public static CommonEntityType getEntityType(BiomeSpawnCluster biomeSpawnCluster) {
         * #if version >= 1.13
         *     EntityTypes entityType;
         *   #if version >= 1.21.5
         *     entityType = biomeSpawnCluster.type();
         *   #else
         *     #select version >=
         *     #case 1.17: #require net.minecraft.world.level.biome.BiomeSpawnCluster public net.minecraft.world.entity.EntityTypes entityType:type;
         *     #case 1.16: #require net.minecraft.world.level.biome.BiomeSpawnCluster public net.minecraft.world.entity.EntityTypes entityType:c;
         *     #case else: #require net.minecraft.world.level.biome.BiomeSpawnCluster public net.minecraft.world.entity.EntityTypes entityType:b;
         *     #endselect
         *     entityType = biomeSpawnCluster#entityType;
         *   #endif
         *
         *     return CommonEntityType.byNMSEntityTypeRaw(entityType);
         * #else
         *     #require net.minecraft.world.level.biome.BiomeSpawnCluster public java.lang.Class<? extends net.minecraft.world.entity.EntityInsentient> entityClass:b;
         *     Class entityClass = biomeSpawnCluster#entityClass;
         *     return CommonEntityType.byNMSEntityClass(entityClass);
         * #endif
         * }
         */
        @Template.Generated("%GET_CLUSTER_ENTITY_TYPE%")
        public abstract CommonEntityType getEntityType(Object biomeSpawnCluster);
    }
}
