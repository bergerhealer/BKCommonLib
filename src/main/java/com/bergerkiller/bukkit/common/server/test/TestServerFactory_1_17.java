package com.bergerkiller.bukkit.common.server.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

class TestServerFactory_1_17 extends TestServerFactory {

    @Override
    protected boolean init() {
        //System.out.println("Detected server class under test: " + CommonServerBase.SERVER_CLASS);

        String cb_root = getPackagePath(CommonServerBase.SERVER_CLASS) + ".";

        //System.out.println("CB ROOT: " + cb_root);
        //System.out.println("NMS ROOT: " + nms_root);

        try {
            // Initialize shared constants first - required by DispenserRegistry
            Class<?> sharedConstantsClass = Class.forName("net.minecraft.SharedConstants");
            Method initSharedConstantsMethod = sharedConstantsClass.getDeclaredMethod("a");
            initSharedConstantsMethod.invoke(null);

            // Bootstrap is required
            Class<?> dispenserRegistryClass = Class.forName("net.minecraft.server.DispenserRegistry");
            Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("init");
            dispenserRegistryBootstrapMethod.invoke(null);

            // Create some stuff by null-constructing them (not calling initializer)
            // This prevents loads of extra server logic executing during test
            ClassTemplate<?> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
            Object server = server_t.newInstanceNull();
            Class<?> minecraftServerType = Class.forName("net.minecraft.server.MinecraftServer");
            Class<?> dedicatedType = Class.forName("net.minecraft.server.dedicated.DedicatedServer");
            ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
            Object mc_server = mc_server_t.newInstanceNull();

            // Since we null-construct, some members of the parent class "IAsyncTaskHandler" are not initialized. Do that here.
            Class<?> iAsyncTaskHandlerClass = Class.forName("net.minecraft.util.thread.IAsyncTaskHandler");
            setField(mc_server, iAsyncTaskHandlerClass, "name", "Server");
            setField(mc_server, iAsyncTaskHandlerClass, "pendingRunnables", createFromCode(minecraftServerType, 
                    "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));

            // Assign logger, nms Server instance and primary thread (current thread) to avoid NPE's during test
            setField(server, "logger",  MountiplexUtil.LOGGER);
            setField(server, "console", mc_server);
            setField(mc_server, "serverThread", Thread.currentThread());

            // Initialize the dimension root registry for the server
            // IRegistryCustom.Dimension iregistrycustom_dimension = IRegistryCustom.b(); (Main.java)
            // this.f = iregistrycustom_dimension; (MinecraftServer.java)
            Object customRegistry = createFromCode(minecraftServerType, "return net.minecraft.core.IRegistryCustom.a();");
            setField(mc_server, "registryHolder", customRegistry);

            // Assign to the Bukkit server silently (don't want a duplicate server info log line with random null's)
            Field bkServerField = Bukkit.class.getDeclaredField("server");
            bkServerField.setAccessible(true);
            bkServerField.set(null, server);

            // Initialize propertyManager field, which is responsible for server-wide settings like view distance
            Object propertyManager = ClassTemplate.create("net.minecraft.server.dedicated.DedicatedServerSettings").newInstanceNull();
            setField(mc_server, "settings", propertyManager);
            setField(propertyManager, "properties", createFromCode(Class.forName("net.minecraft.server.dedicated.DedicatedServerProperties"),
                    "return new DedicatedServerProperties(new java.util.Properties(), new joptsimple.OptionParser().parse(new String[0]));\n"));

            // Create data converter registry manager object - used for serialization/deserialization
            // Only used >= MC 1.10.2
            Class<?> dataConverterRegistryClass = null;
            try {
                dataConverterRegistryClass = Class.forName("net.minecraft.util.datafix.DataConverterRegistry");
                Method dataConverterRegistryInitMethod = dataConverterRegistryClass.getMethod("a");
                Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null);
                setField(mc_server, "fixerUpper", dataConverterManager);
            } catch (ClassNotFoundException ex) {}

            // Create CraftingManager instance and load recipes for >= MC 1.13
            minecraftServerType.getDeclaredMethod("getCraftingManager");

            // this.executorService = SystemUtils.e();
            {
                setField(mc_server, "executor", createFromCode(minecraftServerType,
                        "return net.minecraft.SystemUtils.e();"));
            }

            // this.dataPackResources = DataPackResources (passed through constructor)
            // The place where this is created can be found in Main.java and looks similar to this
            // Difference is that no configuration is read in, and we assume a default environment
            // Lambdas are a pain in the ass, but we're coping :(
            {
                final String repopath = "net.minecraft.server.packs.repository.";

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
                    Class<?> enumSourcePackTypeClass = Class.forName("net.minecraft.server.packs.EnumResourcePackType");
                    final Object packTypeServerData = getStaticField(enumSourcePackTypeClass, "SERVER_DATA");
                    
                    final Class<?> resourcePackLoaderType = Class.forName(repopath + "ResourcePackLoader");
                    ClassInterceptor interceptor = new ClassInterceptor() {
                        @Override
                        protected Invoker<?> getCallback(Method method) {
                            if (method.getName().equals("create")) {
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

                    resourcePackLoaderNew = interceptor.createInstance(Class.forName(repopath + "ResourcePackLoader$a"));
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
                    Object[] resourcePackSources = LogicUtil.createArray(Class.forName(repopath + "ResourcePackSource"), 1);
                    resourcePackSources[0] = construct(Class.forName(repopath + "ResourcePackSourceVanilla"));
                    resourcepackrepository = construct(Class.forName(repopath + "ResourcePackRepository"),
                            resourcePackLoaderNew, resourcePackSources);
                }

                /*
                 * Create the Data pack configuration instance
                 * 
                 * DataPackConfiguration datapackconfiguration1 = MinecraftServer.a(resourcepackrepository, DataPackConfiguration.a, true);
                 */
                Object datapackconfiguration;
                {
                    Object defaultDPConfig = getStaticField(Class.forName("net.minecraft.world.level.DataPackConfiguration"), "a");
                    Method createDPConfig = minecraftServerType.getDeclaredMethod("a",
                            Class.forName(repopath + "ResourcePackRepository"),
                            Class.forName("net.minecraft.world.level.DataPackConfiguration"),
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
                 *         Runnable::run);
                 */
                CompletableFuture<Object> futureDPLoaded;
                {
                    java.util.List<?> packs = (java.util.List<?>) resourcepackrepository.getClass().getMethod("f").invoke(resourcepackrepository);
                    Class<?> serverTypeType = Class.forName("net.minecraft.commands.CommandDispatcher$ServerType");
                    Object serverType = getStaticField(serverTypeType, "DEDICATED");
                    int functionPermissionLevel = 2;
                    Executor executor1 = (Executor) Class.forName("net.minecraft.SystemUtils").getMethod("f").invoke(null);
                    Executor executor2 = Runnable::run;
                    Method startLoadingMethod = Class.forName("net.minecraft.server.DataPackResources").getDeclaredMethod("a",
                            List.class,
                            Class.forName("net.minecraft.core.IRegistryCustom"),
                            serverTypeType,
                            int.class,
                            Executor.class,
                            Executor.class);
                    futureDPLoaded = (CompletableFuture<Object>) startLoadingMethod.invoke(null,
                            packs, customRegistry, serverType, functionPermissionLevel, executor1, executor2);
                }

                // Retrieve it, using get(). May throw if problems occur.
                Object datapackresources = futureDPLoaded.get();

                // Call j() on the result - which calls bind() on the tags
                // datapackresources.i();
                {
                    Class<?> datapackresourceType = Class.forName("net.minecraft.server.DataPackResources");
                    datapackresourceType.getMethod("j").invoke(datapackresources);
                }

                // Now set all these fields in the MinecraftServer instance
                setField(mc_server, "packRepository", resourcepackrepository);
                setField(mc_server, "datapackconfiguration", datapackconfiguration);
                setField(mc_server, "resources", datapackresources);
            }

            // Initialize WorldDataServer instance
            // public WorldDataServer(WorldSettings worldsettings, GeneratorSettings generatorsettings, Lifecycle lifecycle)
            /*
            Object worldData;
            {
                Object worldSettings = createFromCode(minecraftServerType, "return MinecraftServer.c;");
                Object generatorSettings = createFromCode(minecraftServerType, "return GeneratorSettings.a();");
                Object lifeCycle = createFromCode(minecraftServerType, "return com.mojang.serialization.Lifecycle.stable();");
                Class<?> worldDataType = Class.forName(nms_root + "WorldDataServer");
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

            return true;
        } catch (Throwable t) {
            System.err.println("Failed to initialize server under test");
            System.out.println("Detected server class under test: " + CommonServerBase.SERVER_CLASS);
            System.out.println("Detected CB_ROOT: " + cb_root);
            t.printStackTrace();
            return false;
        }
    }

}
