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
    public boolean isForgeServer() {
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
    public boolean init() {
        return true;
    }

    @Override
    public void postInit(PostInitEvent event) {
        // It's not compatible at all because we don't know what server is used
        event.signalIncompatible("Server software type could not be identified");

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

    @Override
    public String getNMSRoot() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCBRoot() {
        // TODO Auto-generated method stub
        return null;
    }
}
