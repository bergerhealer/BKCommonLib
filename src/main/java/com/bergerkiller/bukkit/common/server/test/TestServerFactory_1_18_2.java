package com.bergerkiller.bukkit.common.server.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;

class TestServerFactory_1_18_2 extends TestServerFactory_1_18 {

    @Override
    protected Object initCustomRegistryDimension(Class<?> minecraftServerType) {
        return createFromCode(minecraftServerType, "return net.minecraft.core.RegistryAccess.builtinCopy().freeze();");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initDataPack(Class<?> minecraftServerType, Object mc_server, Object customRegistryDimension) throws Throwable {
        final Class<?> resourcePackRepositoryType = resolveClass("net.minecraft.server.packs.repository.PackRepository");

        /*
         * Create ResourcePackRepository (Main.java)
         * 
         * ResourcePackRepository resourcepackrepository = new ResourcePackRepository(
         *         EnumResourcePackType.SERVER_DATA, new ResourcePackSource[]{new ResourcePackSourceVanilla()});
         */
        Object resourcepackrepository;
        Object resourcepacktype;
        {
            // arg0: EnumResourcePackType
            Class<?> enumSourcePackTypeClass = resolveClass("net.minecraft.server.packs.PackType");
            resourcepacktype = getStaticField(enumSourcePackTypeClass, "SERVER_DATA");

            // arg1: ResourcePackSource[]
            final Object[] resourcePackSources = LogicUtil.createArray(resolveClass("net.minecraft.server.packs.repository.RepositorySource"), 1);
            resourcePackSources[0] = construct(resolveClass("net.minecraft.server.packs.repository.ServerPacksSource"));

            // Construct new ResourcePackRepository
            resourcepackrepository = construct(resourcePackRepositoryType, resourcepacktype, resourcePackSources);
        }

        /*
         * Create the Data pack configuration instance
         * 
         * DataPackConfiguration datapackconfiguration1 = MinecraftServer.a(resourcepackrepository, DataPackConfiguration.a, true);
         */
        Object datapackconfiguration;
        {
            Object defaultDPConfig = getStaticField(resolveClass("net.minecraft.world.level.DataPackConfig"), "DEFAULT");
            Method createDPConfig = Resolver.resolveAndGetDeclaredMethod(minecraftServerType, "configurePackRepository",
                    resourcePackRepositoryType,
                    resolveClass("net.minecraft.world.level.DataPackConfig"),
                    boolean.class);
            datapackconfiguration = createDPConfig.invoke(null, resourcepackrepository, defaultDPConfig, true);
        }

        /*
         * Create a ResourceManager with a list of resource packs
         */
        Object resourcemanager;
        {
            java.util.List<?> packs = (java.util.List<?>) Resolver.resolveAndGetDeclaredMethod(resourcePackRepositoryType, "openAllSelected")
                    .invoke(resourcepackrepository);
            resourcemanager = construct(resolveClass("net.minecraft.server.packs.resources.MultiPackResourceManager"),
                    resourcepacktype, packs);
        }

        /*
         * Create a completable future completed when the resource pack is loaded fully.
         * Call get() on it to load it synchronously right here right now
         * 
         * CompletableFuture completablefuture = DataPackResources.a(
         *         resourcepackrepository.f(),
         *         CommandDispatcher.ServerType.DEDICATED,
         *         2, //dedicatedserversettings.getProperties().functionPermissionLevel,
         *         SystemUtils.f(),
         *         newThreadExecutor());
         */
        CompletableFuture<Object> futureDPLoaded;
        {
            Class<?> serverTypeType = resolveClass("net.minecraft.commands.Commands$CommandSelection");
            Object serverType = getStaticField(serverTypeType, "DEDICATED");
            int functionPermissionLevel = 2;
            Executor executor1 = (Executor) Resolver.resolveAndGetDeclaredMethod(resolveClass("net.minecraft.util.Util"),
                    "bootstrapExecutor").invoke(null);
            Executor executor2 = newThreadExecutor();
            Class<?> dataPackResourcesType = resolveClass("net.minecraft.server.ReloadableServerResources");
            Method startLoadingMethod = Resolver.resolveAndGetDeclaredMethod(dataPackResourcesType, "loadResources",
                    resolveClass("net.minecraft.server.packs.resources.ResourceManager"),
                    resolveClass("net.minecraft.core.RegistryAccess$Frozen"),
                    serverTypeType,
                    int.class,
                    Executor.class,
                    Executor.class);
            futureDPLoaded = (CompletableFuture<Object>) startLoadingMethod.invoke(null,
                    resourcemanager, customRegistryDimension, serverType, functionPermissionLevel, executor1, executor2);
        }

        // Retrieve it, using get(). May throw if problems occur.
        Object datapackresources = futureDPLoaded.get();

        // Call j() on the result - which calls bind() on the tags
        // datapackresources.i();
        {
            Class<?> datapackresourceType = resolveClass("net.minecraft.server.ReloadableServerResources");
            Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateRegistryTags",
                    resolveClass("net.minecraft.core.RegistryAccess"))
                        .invoke(datapackresources, customRegistryDimension);
        }

        // Now set all these fields in the MinecraftServer instance
        setField(mc_server, "packRepository", resourcepackrepository);
        setField(mc_server, "datapackconfiguration", datapackconfiguration);

        // As of 1.18.2 the 'resources' field is class that stores two fields inside (IReloadableResourceManager + DataPackResources)
        {
            String resourcesFieldName = Resolver.resolveFieldName(minecraftServerType, "resources");
            Field field = minecraftServerType.getDeclaredField(resourcesFieldName);
            field.setAccessible(true);

            Constructor<?> constr = field.getType().getConstructor(
                    resolveClass("net.minecraft.server.packs.resources.CloseableResourceManager"),
                    resolveClass("net.minecraft.server.ReloadableServerResources"));
            constr.setAccessible(true);
            Object managerWithResources = constr.newInstance(resourcemanager, datapackresources);

            field.set(mc_server, managerWithResources);
        }
    }
}
