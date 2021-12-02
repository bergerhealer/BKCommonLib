package com.bergerkiller.bukkit.common.server.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        Class<?> dispenserRegistryClass = Class.forName(env.NMS_ROOT + "DispenserRegistry");
        Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("init");
        dispenserRegistryBootstrapMethod.invoke(null);

        // Create some stuff by null-constructing them (not calling initializer)
        // This prevents loads of extra server logic executing during test
        ClassTemplate<?> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
        Object server = server_t.newInstanceNull();
        Class<?> minecraftServerType = Class.forName(env.NMS_ROOT + "MinecraftServer");
        Class<?> dedicatedType = Class.forName(env.NMS_ROOT + "DedicatedServer");
        ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
        Object mc_server = mc_server_t.newInstanceNull();

        // Since we null-construct, some members of the parent class "IAsyncTaskHandler" are not initialized. Do that here.
        Class<?> iAsyncTaskHandlerClass = Class.forName(env.NMS_ROOT + "IAsyncTaskHandler");
        setField(mc_server, iAsyncTaskHandlerClass, "b", "Server");
        setField(mc_server, iAsyncTaskHandlerClass, "d", createFromCode(minecraftServerType, 
                "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));

        // Assign logger, nms Server instance and primary thread (current thread) to avoid NPE's during test
        setField(server, "logger",  MountiplexUtil.LOGGER);
        setField(server, "console", mc_server);
        setField(mc_server, "serverThread", Thread.currentThread());

        // Assign to the Bukkit server silently (don't want a duplicate server info log line with random null's)
        Field bkServerField = Bukkit.class.getDeclaredField("server");
        bkServerField.setAccessible(true);
        bkServerField.set(null, server);

        // Initialize propertyManager field, which is responsible for server-wide settings like view distance
        Object propertyManager = ClassTemplate.create(env.NMS_ROOT + "DedicatedServerSettings").newInstanceNull();
        setField(mc_server, "propertyManager", propertyManager);
        setField(propertyManager, "properties", createFromCode(Class.forName(env.NMS_ROOT + "DedicatedServerProperties"),
                "return new DedicatedServerProperties(new java.util.Properties(), new joptsimple.OptionParser().parse(new String[0]));\n"));

        // Create data converter registry manager object - used for serialization/deserialization
        // Only used >= MC 1.10.2
        Class<?> dataConverterRegistryClass = null;
        try {
            dataConverterRegistryClass = Class.forName(env.NMS_ROOT + "DataConverterRegistry");
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
            setField(mc_server, fieldname, createFromCode(minecraftServerType,
                    "return new ResourceManager(EnumResourcePackType.SERVER_DATA, java.lang.Thread.currentThread());"));
        }

        // this.resourcePackRepository = new ResourcePackRepository(ResourcePackLoader::new);
        {
            final FastMethod<Object> loaderCreator = compileCode(minecraftServerType,
                    "public static Object create(Object args_t) {"
                   +"  Object[] args = (Object[]) args_t;"
                   +"  return new ResourcePackLoader("
                   + "     (String) args[0],"
                   + "     ((Boolean) args[1]).booleanValue(), "
                   + "     (java.util.function.Supplier) args[2], "
                   + "     (IResourcePack) args[3], "
                   + "     (ResourcePackInfo) args[4], "
                   + "     (ResourcePackLoader$Position) args[5]"
                   + ");"
                   +"}");

            Class<?> resourcePackLoaderFuncType = Class.forName(env.NMS_ROOT + "ResourcePackLoader$b");
            Object resourcePackLoaderFunc = Proxy.newProxyInstance(
                   TestServerFactory.class.getClassLoader(),
                   new Class<?>[]{resourcePackLoaderFuncType},
                   (proxy, method, args) -> loaderCreator.invoke(null, args));

            Class<?> resourcePackRepositoryType = Class.forName(env.NMS_ROOT + "ResourcePackRepository");
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

        if (CommonBootstrap.evaluateMCVersion(">=", "1.15")) {
            // this.craftingManager = new CraftingManager();
            {
                Class<?> craftingManagerType = Class.forName(env.NMS_ROOT + "CraftingManager");
                setField(mc_server, "craftingManager", craftingManagerType.newInstance());
            }

            // this.tagRegistry = new TagRegistry();
            {
                Class<?> craftingManagerType = Class.forName(env.NMS_ROOT + "TagRegistry");
                setField(mc_server, "tagRegistry", craftingManagerType.newInstance());
            }
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14.4")) {
            // this.ai = new CraftingManager();
            {
                Class<?> craftingManagerType = Class.forName(env.NMS_ROOT + "CraftingManager");
                setField(mc_server, "ai", craftingManagerType.newInstance());
            }

            // this.aj = new TagRegistry();
            {
                Class<?> craftingManagerType = Class.forName(env.NMS_ROOT + "TagRegistry");
                setField(mc_server, "aj", craftingManagerType.newInstance());
            }
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14.3")) {
            // this.ah = new CraftingManager();
            {
                Class<?> craftingManagerType = Class.forName(env.NMS_ROOT + "CraftingManager");
                setField(mc_server, "ah", craftingManagerType.newInstance());
            }

            // this.ai = new TagRegistry();
            {
                Class<?> craftingManagerType = Class.forName(env.NMS_ROOT + "TagRegistry");
                setField(mc_server, "ai", craftingManagerType.newInstance());
            }
        } else {
            // this.ag = new CraftingManager();
            {
                Class<?> craftingManagerType = Class.forName(env.NMS_ROOT + "CraftingManager");
                setField(mc_server, "ai", craftingManagerType.newInstance());
            }

            // this.ah = new TagRegistry();
            {
                Class<?> craftingManagerType = Class.forName(env.NMS_ROOT + "TagRegistry");
                setField(mc_server, "aj", craftingManagerType.newInstance());
            }
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
        Class<?> worldDataType = Class.forName(env.NMS_ROOT + "WorldData");
        java.lang.reflect.Constructor<?> con = worldDataType.getDeclaredConstructor();
        con.setAccessible(true);
        Object worldData = con.newInstance();
        java.lang.reflect.Method m = minecraftServerType.getDeclaredMethod("a", File.class, worldDataType);
        m.setAccessible(true);
        m.invoke(mc_server, serverDir, worldData);
    }
}
