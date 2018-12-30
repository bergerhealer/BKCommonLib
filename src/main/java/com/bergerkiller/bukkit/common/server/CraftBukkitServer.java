package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.mountiplex.reflection.util.ASMUtil;

import org.bukkit.Bukkit;

public class CraftBukkitServer extends CommonServerBase {

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

    @Override
    public boolean init() {
        // No Bukkit server class, can't continue
        if (SERVER_CLASS == null) {
            return false;
        }

        // Find out what package version is used
        String serverPath = SERVER_CLASS.getName();
        if (!serverPath.startsWith(Common.CB_ROOT)) {
            return false;
        }
        PACKAGE_VERSION = StringUtil.getBefore(serverPath.substring(Common.CB_ROOT.length() + 1), ".");

        // Obtain the versioned roots
        if (PACKAGE_VERSION.isEmpty()) {
            NMS_ROOT_VERSIONED = Common.NMS_ROOT;
            CB_ROOT_VERSIONED = Common.CB_ROOT;
        } else {
            NMS_ROOT_VERSIONED = Common.NMS_ROOT + "." + PACKAGE_VERSION;
            CB_ROOT_VERSIONED = Common.CB_ROOT + "." + PACKAGE_VERSION;
        }

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

            if (Bukkit.getServer() != null) {
                // Obtain MinecraftServer instance from server
                Class<?> server = Class.forName(CB_ROOT_VERSIONED + ".CraftServer");
                MethodAccessor<Object> getServer = new SafeMethod<Object>(server, "getServer");
                Object minecraftServerInstance = getServer.invoke(Bukkit.getServer());

                // Use getVersion() on this instance
                MC_VERSION = (String) getVersionMethod.invoke(minecraftServerInstance);
            } else {
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
    public String getClassName(String path) {
        if (path.startsWith(Common.NMS_ROOT) && !path.startsWith(NMS_ROOT_VERSIONED)) {
            return NMS_ROOT_VERSIONED + path.substring(Common.NMS_ROOT.length());
        }
        if (path.startsWith(Common.CB_ROOT) && !path.startsWith(CB_ROOT_VERSIONED)) {
            return CB_ROOT_VERSIONED + path.substring(Common.CB_ROOT.length());
        }
        return path;
    }

    @Override
    public String getMethodName(Class<?> type, String methodName, Class<?>... params) {
        return methodName;
    }

    @Override
    public String getFieldName(Class<?> type, String fieldName) {
        return fieldName;
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

}
