package com.bergerkiller.bukkit.common.server;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.Common;

import java.lang.reflect.Constructor;

public class UnknownServer extends CommonServerBase {

    /**
     * Defines the Package Version
     */
    public String PACKAGE_VERSION;
    /**
     * Defines the net.minecraft.server root path
     */
    public String NMS_ROOT_VERSIONED;
    /**
     * Defines the org.bukkit.craftbukkit root path
     */
    public String CB_ROOT_VERSIONED;
	
    @Override
    public String getServerVersion() {
    	if (Bukkit.getServer() == null) {
    		return "UNKNOWN";
    	} else {
    		return Bukkit.getServer().getVersion();
    	}
    }

    @Override
    public String getServerName() {
        return "Unknown Server";
    }

    @Override
    public boolean isCompatible() {
        return false;
    }

    @Override
    public String getServerDescription() {
    	if (Bukkit.getServer() == null) {
    		return "NULL";
    	}
        return Bukkit.getServer().getVersion();
    }

    @Override
    public String getMinecraftVersion() {
        return "UNKNOWN";
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
    public boolean init() {
        return true;
    }

    @Override
    public void postInit() {
        // Hardcode the NMS and CB paths at compilation time
        // UnknownServer is only used during testing phase when no server is available, so it's fine
        if (CommonServerBase.SERVER_CLASS != null) {
            CB_ROOT_VERSIONED = CommonServerBase.SERVER_CLASS.getPackage().getName();
            NMS_ROOT_VERSIONED = "net.minecraft.server" + CB_ROOT_VERSIONED.substring(Common.CB_ROOT.length());
            for (Constructor<?> c : CommonServerBase.SERVER_CLASS.getDeclaredConstructors()) {
                for (Class<?> type : c.getParameterTypes()) {
                    if (type.getName().startsWith(Common.NMS_ROOT)) {
                        NMS_ROOT_VERSIONED = type.getPackage().getName();
                        break;
                    }
                }
            }
        } else {
            CB_ROOT_VERSIONED = Common.CB_ROOT;
            NMS_ROOT_VERSIONED = Common.NMS_ROOT;
        }
    }

}
