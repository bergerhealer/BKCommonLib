package com.bergerkiller.bukkit.common.server.test;

class TestServerFactory_1_21_2 extends TestServerFactory_1_20_2 {

    @Override
    protected void init(ServerEnvironment env) throws Throwable {
        super.init(env);

        // FuelValues used for RecipeUtil (furnace recipe burn times)
        // In MinecraftServer.java:
        //            this.fuelValues = FuelValues.vanillaBurnTimes(this.registries.compositeAccess(), this.worldData.enabledFeatures());
        setField(env.mc_server, "fuelValues", createFromCode(env.mc_server_type, "" +
                "return net.minecraft.world.level.block.entity.FuelValues.vanillaBurnTimes(\n" +
                "    arg0.compositeAccess(),\n" +
                "    arg1\n" +
                ");", env.registries, env.featureFlagSet));
    }

    @Override
    protected Object initRegistries(ServerEnvironment env) throws Throwable {
        Object registryAccess = createFromCode(env.mc_server_type, "return net.minecraft.server.RegistryLayer.createRegistryAccess();");

        // Initialize tag data pack stuff
        // In WorldLoader.java:
        //            List<IRegistry.a<?>> list = TagDataPack.loadTagsForExistingRegistries(ireloadableresourcemanager, layeredregistryaccess.getLayer(RegistryLayer.STATIC));
        {
            env.tagDataPackRegistries = (java.util.List<?>) createFromCode(env.mc_server_type, "" +
                    "net.minecraft.server.packs.resources.IReloadableResourceManager ireloadableresourcemanager = arg0;\n" +
                    "net.minecraft.core.LayeredRegistryAccess layeredregistryaccess = arg1;\n" +
                    "return net.minecraft.tags.TagDataPack.loadTagsForExistingRegistries(ireloadableresourcemanager, layeredregistryaccess.getLayer(net.minecraft.server.RegistryLayer.STATIC));",

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
            env.registries = createFromCode(env.mc_server_type, "" +
                    "net.minecraft.server.packs.resources.IReloadableResourceManager ireloadableresourcemanager = arg0;\n" +
                    "net.minecraft.core.LayeredRegistryAccess layeredregistryaccess = arg1;\n" +
                    "java.util.List list = arg2;\n" +
                    "net.minecraft.core.IRegistryCustom$Dimension iregistrycustom_dimension = layeredregistryaccess.getAccessForLoading(net.minecraft.server.RegistryLayer.WORLDGEN);\n" +
                    "java.util.List list1 = net.minecraft.tags.TagDataPack.buildUpdatedLookups(iregistrycustom_dimension, list);\n" +
                    "net.minecraft.core.IRegistryCustom$Dimension iregistrycustom_dimension1 = net.minecraft.resources.RegistryDataLoader.load((net.minecraft.server.packs.resources.IResourceManager) ireloadableresourcemanager, list1, net.minecraft.resources.RegistryDataLoader.WORLDGEN_REGISTRIES);\n" +
                    "java.util.List list2 = java.util.stream.Stream.concat(list1.stream(), iregistrycustom_dimension1.listRegistries()).toList();\n" +
                    "net.minecraft.core.IRegistryCustom$Dimension iregistrycustom_dimension2 = net.minecraft.resources.RegistryDataLoader.load((net.minecraft.server.packs.resources.IResourceManager) ireloadableresourcemanager, list2, net.minecraft.resources.RegistryDataLoader.DIMENSION_REGISTRIES);\n" +
                    "\n" +
                    "return layeredregistryaccess.replaceFrom(RegistryLayer.WORLDGEN, java.util.Collections.singletonList(iregistrycustom_dimension1));",

                    env.resourceManager, registryAccess, env.tagDataPackRegistries);
        }

        return env.registries;
    }
}
