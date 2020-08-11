package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;

import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;

public class CraftBukkitServer extends CommonServerBase implements ClassPathResolver {
    private static final String CB_ROOT = "org.bukkit.craftbukkit";
    private static final String NMS_ROOT = "net.minecraft.server";

    /**
     * Defines the Package Version
     */
    public String PACKAGE_VERSION;
    /**
     * Defines the Minecraft Version
     */
    public String MC_VERSION;
    /**
     * Defines the net.minecraft.server root path
     */
    public String NMS_ROOT_VERSIONED;
    /**
     * Defines the org.bukkit.craftbukkit root path
     */
    public String CB_ROOT_VERSIONED;
    /**
     * Defines the org.bukkit.craftbukkit.libs root path
     */
    public String CB_ROOT_LIBS;
    /**
     * Defines class name remappings to perform right after the version info is included in the class path
     */
    private Map<String, String> remappings = Collections.emptyMap();

    @Override
    public boolean init() {
        // No Bukkit server class, can't continue
        if (SERVER_CLASS == null) {
            return false;
        }

        // Find out what package version is used
        String serverPath = SERVER_CLASS.getName();
        if (!serverPath.startsWith(CB_ROOT)) {
            return false;
        }
        PACKAGE_VERSION = StringUtil.getBefore(serverPath.substring(CB_ROOT.length() + 1), ".");

        // Obtain the versioned roots
        if (PACKAGE_VERSION.isEmpty()) {
            NMS_ROOT_VERSIONED = NMS_ROOT;
            CB_ROOT_VERSIONED = CB_ROOT;
        } else {
            NMS_ROOT_VERSIONED = NMS_ROOT + "." + PACKAGE_VERSION;
            CB_ROOT_VERSIONED = CB_ROOT + "." + PACKAGE_VERSION;
        }
        CB_ROOT_LIBS = CB_ROOT + ".libs";

        // Figure out the MC version from the server
        MC_VERSION = PACKAGE_VERSION;
        return true;
    }

    @Override
    public void postInit() {
        try {
            // Find getVersion() method using reflection
            Class<?> minecraftServerType = Class.forName(NMS_ROOT_VERSIONED + ".MinecraftServer", false, this.getClass().getClassLoader());
            java.lang.reflect.Method getVersionMethod = minecraftServerType.getMethod("getVersion");

            // This is easy when the server is already initialized
            if (Bukkit.getServer() != null) {
                // Obtain MinecraftServer instance from server
                Class<?> server = Class.forName(CB_ROOT_VERSIONED + ".CraftServer");

                // Standard CraftServer::getServer()
                Object minecraftServerInstance;
                try {
                    java.lang.reflect.Method getServerMethod = server.getDeclaredMethod("getServer");
                    minecraftServerInstance = getServerMethod.invoke(Bukkit.getServer());
                    MC_VERSION = (String) getVersionMethod.invoke(minecraftServerInstance);
                    return;
                } catch (NoSuchMethodException ex) {}

                throw new RuntimeException("Server version could not be identified");
            }

            // If MinecraftVersion class exists, we can use the static a() method to retrieve it
            // Alternative is using SharedConstants, but it initializes a whole lot extra and is therefore slow
            // Both of these methods work since MC 1.14
            try {
                Object gameVersion;
                try {
                    Class<?> minecraftVersionClass = Class.forName(NMS_ROOT_VERSIONED + ".MinecraftVersion");
                    gameVersion = minecraftVersionClass.getDeclaredMethod("a").invoke(null);
                } catch (ClassNotFoundException | NoSuchMethodException ex) {
                    Class<?> sharedConstantsClass = Class.forName(NMS_ROOT_VERSIONED + ".SharedConstants");
                    gameVersion = sharedConstantsClass.getDeclaredMethod("a").invoke(null);
                    Logging.LOGGER.warning("Failed to find Minecraft Version using MinecraftVersion.class, used SharedConstants instead");
                }
                MC_VERSION = gameVersion.getClass().getMethod("getName").invoke(gameVersion).toString();

            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                // No server instance is available, fastest way is to inspect the bytecode using ASM
                // Creating an instance, even without calling constructors, takes a long while to initialize
                MC_VERSION = ASMUtil.findStringConstantReturnedByMethod(getVersionMethod);

                // Create an instance of Minecraft Server without calling any constructors
                // This is a bit slower, but works as a reliable fallback
                if (MC_VERSION == null) {
                    Logging.LOGGER.warning("Failed to find Minecraft Version using ASM, falling back to slower null-constructing");
                    ClassTemplate<?> nms_server_tpl = ClassTemplate.create(NMS_ROOT_VERSIONED + ".DedicatedServer");
                    Object minecraftServerInstance = nms_server_tpl.newInstanceNull();
                    MC_VERSION = (String) getVersionMethod.invoke(minecraftServerInstance);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public String resolveClassPath(String path) {
        if (path.startsWith(NMS_ROOT) && !path.startsWith(NMS_ROOT_VERSIONED)) {
            path = NMS_ROOT_VERSIONED + path.substring(NMS_ROOT.length());
        } else if (path.startsWith(CB_ROOT) && !path.startsWith(CB_ROOT_VERSIONED) && !path.startsWith(CB_ROOT_LIBS)) {
            path = CB_ROOT_VERSIONED + path.substring(CB_ROOT.length());
        }

        String remapped = this.remappings.get(path);
        if (remapped != null) {
            path = remapped;
        }

        return path;
    }

    @Override
    public String getMinecraftVersion() {
        return MC_VERSION;
    }

    @Override
    public String getServerVersion() {
        return (PACKAGE_VERSION.isEmpty() ? "(Unknown)" : PACKAGE_VERSION) + " (Minecraft " + MC_VERSION + ")";
    }

    @Override
    public String getServerDescription() {
        String desc = Bukkit.getServer().getVersion();
        desc = desc.replace(" (MC: " + MC_VERSION + ")", "");
        return desc;
    }

    @Override
    public String getServerName() {
        return "CraftBukkit";
    }

    @Override
    public String getNMSRoot() {
        return NMS_ROOT_VERSIONED;
    }

    @Override
    public String getCBRoot() {
        return CB_ROOT_VERSIONED;
    }

    /**
     * Sets the remappings to perform right after the version info is included in the Class name package path.
     * 
     * @param remappings
     */
    public void setEarlyRemappings(Map<String, String> remappings) {
        this.remappings = remappings;
    }
}
