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

class TestServerFactory_1_16 extends TestServerFactory {

    @Override
    protected void init() {
        //System.out.println("Detected server class under test: " + CommonServerBase.SERVER_CLASS);

        String cb_root = getPackagePath(CommonServerBase.SERVER_CLASS);
        String nms_root = "net.minecraft.server" + cb_root.substring(cb_root.lastIndexOf('.'));
        try {
            Field f = CommonServerBase.SERVER_CLASS.getDeclaredField("console");
            nms_root = getPackagePath(f.getType());
        } catch (Throwable t) {}
        cb_root += ".";
        nms_root += ".";

        //System.out.println("CB ROOT: " + cb_root);
        //System.out.println("NMS ROOT: " + nms_root);

        try {
            // Bootstrap is required
            Class<?> dispenserRegistryClass = Class.forName(nms_root + "DispenserRegistry");
            Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("init");
            dispenserRegistryBootstrapMethod.invoke(null);

            // Create some stuff by null-constructing them (not calling initializer)
            // This prevents loads of extra server logic executing during test
            ClassTemplate<?> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
            Object server = server_t.newInstanceNull();
            Class<?> minecraftServerType = Class.forName(nms_root + "MinecraftServer");
            Class<?> dedicatedType = Class.forName(nms_root + "DedicatedServer");
            ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
            Object mc_server = mc_server_t.newInstanceNull();

            // Since we null-construct, some members of the parent class "IAsyncTaskHandler" are not initialized. Do that here.
            Class<?> iAsyncTaskHandlerClass = Class.forName(nms_root + "IAsyncTaskHandler");
            setField(mc_server, iAsyncTaskHandlerClass, "b", "Server");
            setField(mc_server, iAsyncTaskHandlerClass, "d", createFromCode(minecraftServerType, 
                    "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));

            // Assign logger, nms Server instance and primary thread (current thread) to avoid NPE's during test
            setField(server, "logger",  MountiplexUtil.LOGGER);
            setField(server, "console", mc_server);
            setField(mc_server, "serverThread", Thread.currentThread());

            // Initialize the dimension root registry for the server
            // IRegistryCustom.Dimension iregistrycustom_dimension = IRegistryCustom.b(); (Main.java)
            // this.f = iregistrycustom_dimension; (MinecraftServer.java)
            Object customRegistry = createFromCode(minecraftServerType, "return IRegistryCustom.b();");
            if (CommonBootstrap.evaluateMCVersion(">=", "1.16.3")) {
                setField(mc_server, "customRegistry", customRegistry);
            } else {
                setField(mc_server, "f", customRegistry);
            }

            // Assign to the Bukkit server silently (don't want a duplicate server info log line with random null's)
            Field bkServerField = Bukkit.class.getDeclaredField("server");
            bkServerField.setAccessible(true);
            bkServerField.set(null, server);

            // Initialize propertyManager field, which is responsible for server-wide settings like view distance
            Object propertyManager = ClassTemplate.create(nms_root + "DedicatedServerSettings").newInstanceNull();
            setField(mc_server, "propertyManager", propertyManager);
            if (CommonBootstrap.evaluateMCVersion(">=", "1.16.2")) {
                setField(propertyManager, "properties", createFromCode(Class.forName(nms_root + "DedicatedServerProperties"),
                        "return new DedicatedServerProperties(new java.util.Properties(), arg0, new joptsimple.OptionParser().parse(new String[0]));\n",
                        customRegistry));
            } else {
                setField(propertyManager, "properties", createFromCode(Class.forName(nms_root + "DedicatedServerProperties"),
                        "return new DedicatedServerProperties(new java.util.Properties(), new joptsimple.OptionParser().parse(new String[0]));\n"));
            }

            // Create data converter registry manager object - used for serialization/deserialization
            // Only used >= MC 1.10.2
            Class<?> dataConverterRegistryClass = null;
            try {
                dataConverterRegistryClass = Class.forName(nms_root + "DataConverterRegistry");
                Method dataConverterRegistryInitMethod = dataConverterRegistryClass.getMethod("a");
                Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null);
                setField(mc_server, "dataConverterManager", dataConverterManager);
            } catch (ClassNotFoundException ex) {}

            // Create CraftingManager instance and load recipes for >= MC 1.13
            minecraftServerType.getDeclaredMethod("getCraftingManager");

            // this.executorService = SystemUtils.e();
            {
                setField(mc_server, "executorService", createFromCode(minecraftServerType,
                        "return SystemUtils.e();"));
            }

            // this.dataPackResources = DataPackResources (passed through constructor)
            // The place where this is created can be found in Main.java and looks similar to this
            // Difference is that no configuration is read in, and we assume a default environment
            // Lambdas are a pain in the ass, but we're coping :(
            {
                /*
                 * First create a lambda for ResourcePackLoader::new, which requires generating a class that implements an anonymous interface
                 * 
                 * Equivalent to: ResourcePackLoader::new
                 * TODO: Should be a cleaner method in Mountiplex for stuff like this
                 *                 
                 * T create(String s,
                 *          boolean flag,
                 *          Supplier<IResourcePack> supplier,
                 *          IResourcePack iresourcepack,
                 *          ResourcePackInfo resourcepackinfo,
                 *          ResourcePackLoader.Position resourcepackloader_position,
                 *          PackSource packsource);
                 */
                Object resourcePackLoaderNew;
                {
                    final Class<?> resourcePackLoaderType = Class.forName(nms_root + "ResourcePackLoader");
                    ClassInterceptor interceptor = new ClassInterceptor() {
                        @Override
                        protected Invoker<?> getCallback(Method method) {
                            if (method.getName().equals("create")) {
                                return (instance, args) -> construct(resourcePackLoaderType, args);
                            }
                            return null;
                        }
                    };

                    resourcePackLoaderNew = interceptor.createInstance(Class.forName(nms_root + "ResourcePackLoader$a"));
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
                    Object[] resourcePackSources = LogicUtil.createArray(Class.forName(nms_root + "ResourcePackSource"), 1);
                    resourcePackSources[0] = construct(Class.forName(nms_root + "ResourcePackSourceVanilla"));
                    resourcepackrepository = construct(Class.forName(nms_root + "ResourcePackRepository"),
                            resourcePackLoaderNew, resourcePackSources);
                }

                /*
                 * Create the Data pack configuration instance
                 * 
                 * DataPackConfiguration datapackconfiguration1 = MinecraftServer.a(resourcepackrepository, DataPackConfiguration.a, true);
                 */
                Object datapackconfiguration;
                {
                    Object defaultDPConfig = getStaticField(Class.forName(nms_root + "DataPackConfiguration"), "a");
                    Method createDPConfig = minecraftServerType.getDeclaredMethod("a",
                            Class.forName(nms_root + "ResourcePackRepository"),
                            Class.forName(nms_root + "DataPackConfiguration"),
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
                    Class<?> serverTypeType = Class.forName(nms_root + "CommandDispatcher$ServerType");
                    Object serverType = getStaticField(serverTypeType, "DEDICATED");
                    int functionPermissionLevel = 2;
                    Executor executor1 = (Executor) Class.forName(nms_root + "SystemUtils").getMethod("f").invoke(null);
                    Executor executor2 = Runnable::run;
                    Method startLoadingMethod = Class.forName(nms_root + "DataPackResources").getDeclaredMethod("a",
                            List.class, serverTypeType, int.class, Executor.class, Executor.class);
                    futureDPLoaded = (CompletableFuture<Object>) startLoadingMethod.invoke(null,
                            packs, serverType, functionPermissionLevel, executor1, executor2);
                }

                // Retrieve it, using get(). May throw if problems occur.
                Object datapackresources = futureDPLoaded.get();

                // Call i() on the result
                // datapackresources.i();
                {
                    Class<?> datapackresourceType = Class.forName(nms_root + "DataPackResources");
                    datapackresourceType.getMethod("i").invoke(datapackresources);
                }

                // Now set all these fields in the MinecraftServer instance
                setField(mc_server, "resourcePackRepository", resourcepackrepository);
                setField(mc_server, "datapackconfiguration", datapackconfiguration);
                setField(mc_server, "dataPackResources", datapackresources);
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
        } catch (Throwable t) {
            System.err.println("Failed to initialize server under test");
            System.out.println("Detected server class under test: " + CommonServerBase.SERVER_CLASS);
            System.out.println("Detected NMS_ROOT: " + nms_root);
            System.out.println("Detected CB_ROOT: " + cb_root);
            t.printStackTrace();
        }
    }

}
