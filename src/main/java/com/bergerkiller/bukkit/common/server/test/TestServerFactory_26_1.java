package com.bergerkiller.bukkit.common.server.test;

import java.util.concurrent.Executor;

class TestServerFactory_26_1 extends TestServerFactory_1_21_2 {

    @Override
    protected Object initRegistries(ServerEnvironment env) throws Throwable {
        Object registryAccess = createFromCode(env.mc_server_type, "return net.minecraft.server.RegistryLayer.createRegistryAccess();");

        // Initialize tag data pack stuff
        // In WorldLoader.java:
        //            List<IRegistry.a<?>> list = TagDataPack.loadTagsForExistingRegistries(ireloadableresourcemanager, layeredregistryaccess.getLayer(RegistryLayer.STATIC));
        {
            env.tagDataPackRegistries = (java.util.List<?>) createFromCode(env.mc_server_type, "" +
                    "net.minecraft.server.packs.resources.CloseableResourceManager ireloadableresourcemanager = arg0;\n" +
                    "net.minecraft.core.LayeredRegistryAccess layeredregistryaccess = arg1;\n" +
                    "return net.minecraft.tags.TagLoader.loadTagsForExistingRegistries(ireloadableresourcemanager, layeredregistryaccess.getLayer(net.minecraft.server.RegistryLayer.STATIC));",

                    env.resourceManager, registryAccess);
        }

        // Initialize WORLDGEN_REGISTRIES <AND> DIMENSION_REGISTRIES (used for dimension type api)
        // In WorldLoader.java:
        //            List<IRegistry.a<?>> list = TagDataPack.loadTagsForExistingRegistries(ireloadableresourcemanager, layeredregistryaccess.getLayer(RegistryLayer.STATIC));
        //            IRegistryCustom.Dimension iregistrycustom_dimension = layeredregistryaccess.getAccessForLoading(RegistryLayer.WORLDGEN);
        //            List<HolderLookup.b<?>> list1 = TagDataPack.buildUpdatedLookups(iregistrycustom_dimension, list);
        //            IRegistryCustom.Dimension iregistrycustom_dimension1 = RegistryDataLoader.load((IResourceManager) ireloadableresourcemanager, list1, RegistryDataLoader.WORLDGEN_REGISTRIES);
        //            List<HolderLookup.b<?>> list2 = Stream.concat(list1.stream(), iregistrycustom_dimension1.listRegistries()).toList();
        //            IRegistryCustom.Dimension iregistrycustom_dimension2 = RegistryDataLoader.load((IResourceManager) ireloadableresourcemanager, list2, RegistryDataLoader.DIMENSION_REGISTRIES);
        {
            final Executor executor = new SyncExecutor();
            env.registries = createFromCode(env.mc_server_type, "" +
                    "net.minecraft.server.packs.resources.CloseableResourceManager ireloadableresourcemanager = arg0;\n" +
                    "net.minecraft.core.LayeredRegistryAccess layeredregistryaccess = arg1;\n" +
                    "java.util.List list = arg2;\n" +
                    "java.util.concurrent.Executor executor = arg3;\n" +
                    "net.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension = layeredregistryaccess.getAccessForLoading(net.minecraft.server.RegistryLayer.WORLDGEN);\n" +
                    "java.util.List list1 = net.minecraft.tags.TagLoader.buildUpdatedLookups(iregistrycustom_dimension, list);\n" +
                    "java.util.concurrent.CompletableFuture iregistrycustom_dimension1_future = net.minecraft.resources.RegistryDataLoader.load((net.minecraft.server.packs.resources.ResourceManager) ireloadableresourcemanager, list1, net.minecraft.resources.RegistryDataLoader.WORLDGEN_REGISTRIES, executor);\n" +
                    "net.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension1 = (net.minecraft.core.RegistryAccess$Frozen) iregistrycustom_dimension1_future.get();\n" +
                    "java.util.List list2 = java.util.stream.Stream.concat(list1.stream(), iregistrycustom_dimension1.listRegistries()).toList();\n" +
                    "java.util.concurrent.CompletableFuture iregistrycustom_dimension2_future = net.minecraft.resources.RegistryDataLoader.load((net.minecraft.server.packs.resources.ResourceManager) ireloadableresourcemanager, list2, net.minecraft.resources.RegistryDataLoader.DIMENSION_REGISTRIES, executor);\n" +
                    "net.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension2 = (net.minecraft.core.RegistryAccess$Frozen) iregistrycustom_dimension2_future.get();\n" +
                    "\n" +
                    "return layeredregistryaccess.replaceFrom(net.minecraft.server.RegistryLayer.WORLDGEN, java.util.Collections.singletonList(iregistrycustom_dimension1));",

                    env.resourceManager, registryAccess, env.tagDataPackRegistries, executor);
        }

        return env.registries;
    }

    public static class SyncExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}
