package com.bergerkiller.bukkit.common.server.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.server.CommonServerBase;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

class TestServerFactory_1_14 extends TestServerFactory {

    @Override
    protected String detectNMSRoot() throws Throwable {
        String cb_root = this.detectCBRoot();
        String nms_root = "net.minecraft.server" + cb_root.substring(cb_root.lastIndexOf('.'));
        try {
            Field f = CommonServerBase.SERVER_CLASS.getDeclaredField("console");
            nms_root = getPackagePath(f.getType());
        } catch (Throwable t) {}
        nms_root += ".";
        return nms_root;
    }

    @Override
    protected void init(ServerEnvironment env) throws Throwable {
        // Bootstrap is required
        Class<?> dispenserRegistryClass = resolveClass("net.minecraft.server.Bootstrap");
        Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("init");
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
        setField(mc_server, iAsyncTaskHandlerClass, "b", "Server");
        setField(mc_server, iAsyncTaskHandlerClass, "d", createFromCode(minecraftServerType, 
                "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));

        // Assign logger, nms Server instance and primary thread (current thread) to avoid NPE's during test
        setField(server, "logger",  MountiplexUtil.LOGGER);
        setField(server, "console", mc_server);
        setField(mc_server, "serverThread", Thread.currentThread());

        // Assign an empty list of loaded worlds to the server instance
        setField(mc_server, "worldServer", Collections.emptyMap());

        // Assign to the Bukkit server silently (don't want a duplicate server info log line with random null's)
        Field bkServerField = Bukkit.class.getDeclaredField("server");
        bkServerField.setAccessible(true);
        bkServerField.set(null, server);

        // Initialize propertyManager field, which is responsible for server-wide settings like view distance
        Object propertyManager = ClassTemplate.create("net.minecraft.server.dedicated.DedicatedServerSettings").newInstanceNull();
        setField(mc_server, "propertyManager", propertyManager);
        setField(propertyManager, "properties", createFromCode(resolveClass("net.minecraft.server.dedicated.DedicatedServerProperties"), "" +
                "return new net.minecraft.server.dedicated.DedicatedServerProperties(" +
                "    new java.util.Properties()," +
                "    new joptsimple.OptionParser().parse(new String[0])" +
                ");"));

        // Create data converter registry manager object - used for serialization/deserialization
        // Only used >= MC 1.10.2
        Class<?> dataConverterRegistryClass = resolveClass("net.minecraft.util.datafix.DataFixers");
        Method dataConverterRegistryInitMethod = dataConverterRegistryClass.getMethod("a");
        Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null);
        setField(mc_server, "dataConverterManager", dataConverterManager);

        // Create CraftingManager instance and load recipes for >= MC 1.13
        minecraftServerType.getDeclaredMethod("getCraftingManager");

        // this.executorService = SystemUtils.e();
        {
            setField(mc_server, "executorService", createFromCode(minecraftServerType,
                    "return net.minecraft.util.Util.e();"));
        }

        // this.ac = new ResourceManager(EnumResourcePackType.SERVER_DATA);
        {
            String fieldname;
            if (CommonBootstrap.evaluateMCVersion(">=", "1.14.4")) {
                fieldname = "ae";
            } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14.3")) {
                fieldname = "ad";
            } else {
                fieldname = "ae";
            }
            setField(mc_server, fieldname, createFromCode(minecraftServerType, "" +
                    "return new net.minecraft.server.packs.resources.SimpleReloadableResourceManager(" +
                    "  net.minecraft.server.packs.PackType.SERVER_DATA," +
                    "  java.lang.Thread.currentThread()" +
                    ");"));
        }

        // this.resourcePackRepository = new ResourcePackRepository(ResourcePackLoader::new);
        {
            final FastMethod<Object> loaderCreator = compileCode(minecraftServerType,
                    "public static Object create(Object args_t) {"
                   +"  Object[] args = (Object[]) args_t;"
                   +"  return new net.minecraft.server.packs.repository.Pack("
                   + "     (String) args[0],"
                   + "     ((Boolean) args[1]).booleanValue(), "
                   + "     (java.util.function.Supplier) args[2], "
                   + "     (net.minecraft.server.packs.PackResources) args[3], "
                   + "     (net.minecraft.server.packs.metadata.pack.PackMetadataSection) args[4], "
                   + "     (net.minecraft.server.packs.repository.Pack$Position) args[5]"
                   + ");"
                   +"}");

            Class<?> resourcePackLoaderFuncType = resolveClass("net.minecraft.server.packs.repository.Pack$PackConstructor");
            Object resourcePackLoaderFunc = Proxy.newProxyInstance(
                   TestServerFactory.class.getClassLoader(),
                   new Class<?>[]{resourcePackLoaderFuncType},
                   (proxy, method, args) -> loaderCreator.invoke(null, args));

            Class<?> resourcePackRepositoryType = resolveClass("net.minecraft.server.packs.repository.PackRepository");
            setField(mc_server, "resourcePackRepository", construct(resourcePackRepositoryType, resourcePackLoaderFunc));
        }

        // this.resourcePackRepository.a((ResourcePackSource) (new ResourcePackSourceVanilla()));
        {
            /*
            compileCode(minecraftServerType,
                    "public void register() {"
                  + "  instance.getResourcePackRepository().a((ResourcePackSource) (new ResourcePackSourceVanilla()));"
                  + "  instance.getResourcePackRepository().a();"
                  + "  instance.getResourcePackRepository().a(new java.util.ArrayList());"
                  + "}").invoke(mc_server);
                  */
        }

        Class<?> recipeManagerType = resolveClass("net.minecraft.world.item.crafting.RecipeManager");
        Class<?> tagManagerType = resolveClass("net.minecraft.tags.TagManager");

        if (CommonBootstrap.evaluateMCVersion(">=", "1.15")) {
            // this.craftingManager = new CraftingManager();
            setField(mc_server, "craftingManager", recipeManagerType.newInstance());

            // this.tagRegistry = new TagRegistry();
            setField(mc_server, "tagRegistry", tagManagerType.newInstance());
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14.4")) {
            // this.ai = new CraftingManager();
            setField(mc_server, "ai", recipeManagerType.newInstance());

            // this.aj = new TagRegistry();
            setField(mc_server, "aj", tagManagerType.newInstance());
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14.3")) {
            // this.ah = new CraftingManager();
            setField(mc_server, "ah", recipeManagerType.newInstance());

            // this.ai = new TagRegistry();
            setField(mc_server, "ai", tagManagerType.newInstance());
        } else {
            // this.ag = new CraftingManager();
            setField(mc_server, "ai", recipeManagerType.newInstance());

            // this.ah = new TagRegistry();
            setField(mc_server, "aj", tagManagerType.newInstance());
        }

        // this.ac.a((IResourcePackListener) this.ah);
        {
            compileCode(minecraftServerType,
                      "public void register() {"
                    + "  instance.getResourceManager().a(instance.getTagRegistry());"
                    + "}").invoke(mc_server);
        }

        // this.ac.a((IResourcePackListener) this.ag);
        {
            compileCode(minecraftServerType,
                      "public void register() {"
                    + "  instance.getResourceManager().a(instance.getCraftingManager());"
                    + "}").invoke(mc_server);
        }

        // Initialize the server further, loading the resource packs, by calling MinecraftServer.a(File, WorldData)
        File serverDir = new File(System.getProperty("user.dir"), "target");
        Class<?> worldDataType = resolveClass("net.minecraft.world.level.storage.LevelData");
        java.lang.reflect.Constructor<?> con = worldDataType.getDeclaredConstructor();
        con.setAccessible(true);
        Object worldData = con.newInstance();
        java.lang.reflect.Method m = minecraftServerType.getDeclaredMethod("a", File.class, worldDataType);
        m.setAccessible(true);
        m.invoke(mc_server, serverDir, worldData);
    }
}
