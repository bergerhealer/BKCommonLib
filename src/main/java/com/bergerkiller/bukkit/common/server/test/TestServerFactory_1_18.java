package com.bergerkiller.bukkit.common.server.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.server.CommonServerBase;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

class TestServerFactory_1_18 extends TestServerFactory {

    @Override
    protected void init(ServerEnvironment env) throws Throwable {
        // Initialize shared constants first - required by DispenserRegistry and DataConverterRegistry
        Class<?> sharedConstantsClass = resolveClass("net.minecraft.SharedConstants");
        Method initSharedConstantsMethod = Resolver.resolveAndGetDeclaredMethod(sharedConstantsClass, "tryDetectVersion");
        initSharedConstantsMethod.invoke(null);

        // Initialize the Data Converter Registry in such a way that no datafixers are registered at all
        // We don't need that trash during the tests we run - it slows it down by way too much
        // This is done by temporarily hacking the bootstrapExecutor to never run tasks - this allows
        // the build() method to return instantly.
        try (BackgroundWorkerDefuser defuser = BackgroundWorkerDefuser.start(resolveClass("net.minecraft.util.Util"))) {
            resolveClass("net.minecraft.util.datafix.DataFixers", true);
        }

        // Bootstrap is required
        Class<?> dispenserRegistryClass = resolveClass("net.minecraft.server.Bootstrap");
        Method dispenserRegistryBootstrapMethod = Resolver.resolveAndGetDeclaredMethod(dispenserRegistryClass, "bootStrap");
        dispenserRegistryBootstrapMethod.invoke(null);

        // Create some stuff by null-constructing them (not calling initializer)
        // This prevents loads of extra server logic executing during test
        ClassTemplate<?> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
        Object server = server_t.newInstanceNull();
        Class<?> minecraftServerType = resolveClass("net.minecraft.server.MinecraftServer");
        Class<?> dedicatedType = resolveClass("net.minecraft.server.dedicated.DedicatedServer");
        ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
        Object mc_server = mc_server_t.newInstanceNull();
        env.mc_server = mc_server;

        // Since we null-construct, some members of the parent class "IAsyncTaskHandler" are not initialized. Do that here.
        Class<?> iAsyncTaskHandlerClass = resolveClass("net.minecraft.util.thread.BlockableEventLoop");
        setField(mc_server, iAsyncTaskHandlerClass, "name", "Server");
        setField(mc_server, iAsyncTaskHandlerClass, "pendingRunnables", createFromCode(minecraftServerType, 
                "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));

        // Assign logger, nms Server instance and primary thread (current thread) to avoid NPE's during test
        setField(server, "logger",  MountiplexUtil.LOGGER);
        setField(server, "console", mc_server);
        setField(mc_server, "serverThread", Thread.currentThread());

        // Assign an empty list of loaded worlds to the server instance
        setField(mc_server, "levels", Collections.emptyMap());

        // Initialize the 'registries' cache field. Added in Bukkit 1.19.1
        if (CommonBootstrap.evaluateMCVersion(">=", "1.19.1")) {
            try {
                setField(server, "registries", new HashMap<Object, Object>());
            } catch (RuntimeException ex) {}
        }

        // Initialize the dimension root registry for the server
        // this.f = iregistrycustom_dimension; (MinecraftServer.java)
        Object customRegistryDimension = initCustomRegistryDimension(minecraftServerType);
        setField(mc_server, "registryHolder", customRegistryDimension);

        // Assign to the Bukkit server silently (don't want a duplicate server info log line with random null's)
        Field bkServerField = Bukkit.class.getDeclaredField("server");
        bkServerField.setAccessible(true);
        bkServerField.set(null, server);

        // Initialize propertyManager field, which is responsible for server-wide settings like view distance
        Object propertyManager = ClassTemplate.create("net.minecraft.server.dedicated.DedicatedServerSettings").newInstanceNull();
        setField(mc_server, "settings", propertyManager);
        setField(propertyManager, "properties", createFromCode(resolveClass("net.minecraft.server.dedicated.DedicatedServerProperties"), "" +
                "return new net.minecraft.server.dedicated.DedicatedServerProperties(" +
                "    new java.util.Properties()," +
                "    new joptsimple.OptionParser().parse(new String[0])" +
                ");"));

        // Create data converter registry manager object - used for serialization/deserialization
        // Only used >= MC 1.10.2
        Class<?> dataConverterRegistryClass = resolveClass("net.minecraft.util.datafix.DataFixers");
        Method dataConverterRegistryInitMethod = Resolver.resolveAndGetDeclaredMethod(dataConverterRegistryClass, "getDataFixer");
        Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null);
        setField(mc_server, "fixerUpper", dataConverterManager);

        // this.executorService = SystemUtils.e();
        {
            setField(mc_server, "executor", createFromCode(minecraftServerType,
                    "return net.minecraft.util.Util.backgroundExecutor();"));
        }

        // ResourcePack initialization (makes recipes available)
        initDataPack(minecraftServerType, mc_server, customRegistryDimension);

        // Initialize WorldDataServer instance
        // public WorldDataServer(WorldSettings worldsettings, GeneratorSettings generatorsettings, Lifecycle lifecycle)
        /*
        Object worldData;
        {
            Object worldSettings = createFromCode(minecraftServerType, "return MinecraftServer.c;");
            Object generatorSettings = createFromCode(minecraftServerType, "return GeneratorSettings.a();");
            Object lifeCycle = createFromCode(minecraftServerType, "return com.mojang.serialization.Lifecycle.stable();");
            Class<?> worldDataType = resolveClass(nms_root + "WorldDataServer");
            worldData = construct(worldDataType, worldSettings, generatorSettings, lifeCycle);
        }
        */

        /*
        // Initialize the server further, loading the resource packs, by calling MinecraftServer.a(File, WorldData)
        File serverDir = new File(System.getProperty("user.dir"), "target");
        java.lang.reflect.Method m = minecraftServerType.getDeclaredMethod("a", File.class, worldDataType);
        m.setAccessible(true);
        m.invoke(mc_server, serverDir, worldData);
        */
    }

    protected Object initCustomRegistryDimension(Class<?> minecraftServerType) {
        // IRegistryCustom.Dimension iregistrycustom_dimension = IRegistryCustom.b(); (Main.java)
        return createFromCode(minecraftServerType, "return net.minecraft.core.RegistryAccess.builtin();");
    }

    @SuppressWarnings({"unchecked"})
    protected void initDataPack(Class<?> minecraftServerType, Object mc_server, Object customRegistryDimension) throws Throwable {
        // this.dataPackResources = DataPackResources (passed through constructor)
        // The place where this is created can be found in Main.java and looks similar to this
        // Difference is that no configuration is read in, and we assume a default environment
        // Lambdas are a pain in the ass, but we're coping :(

        /*
         * First create a lambda, which requires generating a class that implements an anonymous interface
         * 
         * Equivalent to:
         * 
         * Inside ResourcePackRepository.java:
         * 
         * (s, ichatbasecomponent, flag, supplier, resourcepackinfo, resourcepackloader_position, packsource) -> {
         *     return new ResourcePackLoader(s, ichatbasecomponent, flag, supplier, resourcepackinfo, enumresourcepacktype, resourcepackloader_position, packsource);
         * }
         * 
         * TODO: Should be a cleaner method in Mountiplex for stuff like this
         *
         * T create(String s,
         *          IChatBaseComponent ichatbasecomponent,
         *          boolean flag,
         *          Supplier<IResourcePack> supplier,
         *          ResourcePackInfo resourcepackinfo,
         *          ResourcePackLoader.Position resourcepackloader_position,
         *          PackSource packsource);
         */
        Object resourcePackLoaderNew;
        {
            Class<?> enumSourcePackTypeClass = resolveClass("net.minecraft.server.packs.PackType");
            final Object packTypeServerData = getStaticField(enumSourcePackTypeClass, "SERVER_DATA");
            
            final Class<?> resourcePackLoaderType = resolveClass("net.minecraft.server.packs.repository.Pack");
            ClassInterceptor interceptor = new ClassInterceptor() {
                @Override
                protected Invoker<?> getCallback(Method method) {
                    if (method.getReturnType().equals(resourcePackLoaderType)) {
                        return (instance, args) -> {
                            return construct(resourcePackLoaderType,
                                    args[0], /* s */
                                    args[1], /* ichatbasecomponent */
                                    args[2], /* flag */
                                    args[3], /* supplier */
                                    args[4], /* resourcepackinfo */
                                    packTypeServerData, /* enumresourcepacktype */
                                    args[5], /* resourcepackloader_position */
                                    args[6]  /* packsource */);
                        };
                    }
                    return null;
                }
            };

            resourcePackLoaderNew = interceptor.createInstance(resolveClass("net.minecraft.server.packs.repository.Pack$PackConstructor"));
        }

        /*
         * Create the ResourcePackRepository instance
         * 
         * ResourcePackRepository<ResourcePackLoader> resourcepackrepository = new ResourcePackRepository<>(
         *     ResourcePackLoader::new,
         *     new ResourcePackSource[] {new ResourcePackSourceVanilla()}
         */
        Object resourcepackrepository;
        {
            Object[] resourcePackSources = LogicUtil.createArray(resolveClass("net.minecraft.server.packs.repository.RepositorySource"), 1);
            resourcePackSources[0] = construct(resolveClass("net.minecraft.server.packs.repository.ServerPacksSource"));
            resourcepackrepository = construct(resolveClass("net.minecraft.server.packs.repository.PackRepository"),
                    resourcePackLoaderNew, resourcePackSources);
        }

        /*
         * Create the Data pack configuration instance
         * 
         * DataPackConfiguration datapackconfiguration1 = MinecraftServer.a(resourcepackrepository, DataPackConfiguration.a, true);
         */
        Object datapackconfiguration;
        {
            Object defaultDPConfig = getStaticField(resolveClass("net.minecraft.world.level.DataPackConfig"), "a");
            Method createDPConfig = Resolver.resolveAndGetDeclaredMethod(minecraftServerType, "configurePackRepository",
                    resolveClass("net.minecraft.server.packs.repository.PackRepository"),
                    resolveClass("net.minecraft.world.level.DataPackConfig"),
                    boolean.class);
            datapackconfiguration = createDPConfig.invoke(null, resourcepackrepository, defaultDPConfig, true);
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
            java.util.List<?> packs = (java.util.List<?>) resourcepackrepository.getClass().getMethod("f").invoke(resourcepackrepository);
            Class<?> serverTypeType = resolveClass("net.minecraft.commands.Commands$CommandSelection");
            Object serverType = getStaticField(serverTypeType, "DEDICATED");
            int functionPermissionLevel = 2;
            Executor executor1 = (Executor) resolveClass("net.minecraft.util.Util").getMethod("f").invoke(null);
            Executor executor2 = newThreadExecutor();
            Class<?> dataPackResourcesType = resolveClass("net.minecraft.server.ServerResources");
            Method startLoadingMethod = Resolver.resolveAndGetDeclaredMethod(dataPackResourcesType, "loadResources",
                    List.class,
                    resolveClass("net.minecraft.core.RegistryAccess"),
                    serverTypeType,
                    int.class,
                    Executor.class,
                    Executor.class);
            futureDPLoaded = (CompletableFuture<Object>) startLoadingMethod.invoke(null,
                    packs, customRegistryDimension, serverType, functionPermissionLevel, executor1, executor2);
        }

        // Retrieve it, using get(). May throw if problems occur.
        Object datapackresources = futureDPLoaded.get();

        // Call j() on the result - which calls bind() on the tags
        // datapackresources.i();
        {
            Class<?> datapackresourceType = resolveClass("net.minecraft.server.ServerResources");
            Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateGlobals").invoke(datapackresources);
        }

        // Now set all these fields in the MinecraftServer instance
        setField(mc_server, "packRepository", resourcepackrepository);
        setField(mc_server, "datapackconfiguration", datapackconfiguration);
        setField(mc_server, "resources", datapackresources);
    }
}
