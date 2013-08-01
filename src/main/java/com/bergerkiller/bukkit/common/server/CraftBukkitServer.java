package com.bergerkiller.bukkit.common.server;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.utils.StringUtil;

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
		// Find out what package version is used
		String serverPath = Bukkit.getServer().getClass().getName();
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
			// Obtain MinecraftServer instance from server
			Class<?> server = Class.forName(CB_ROOT_VERSIONED + ".CraftServer");
			MethodAccessor<Object> getServer = new SafeMethod<Object>(server, "getServer");
			Object minecraftServerInstance = getServer.invoke(Bukkit.getServer());

			// Use MinecraftServer instance to obtain the version
			Class<?> mcServer = minecraftServerInstance.getClass();
			MethodAccessor<String> getVersion = new SafeMethod<String>(mcServer, "getVersion");
			MC_VERSION = getVersion.invoke(minecraftServerInstance);
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
	@SuppressWarnings("unchecked")
	public List<Integer> getEntityRemoveQueue(Player player) {
		return CommonNMS.getNative(player).removeQueue;
	}

	@Override
	public boolean isCompatible() {
		return PACKAGE_VERSION.isEmpty() || PACKAGE_VERSION.equals(Common.DEPENDENT_MC_VERSION);
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
