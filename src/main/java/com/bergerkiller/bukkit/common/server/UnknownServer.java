package com.bergerkiller.bukkit.common.server;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UnknownServer extends CommonServerBase {

	@Override
	public String getServerVersion() {
		return Bukkit.getServer().getVersion();
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
		return Bukkit.getServer().getVersion();
	}

	@Override
	public String getMinecraftVersion() {
		return "UNKNOWN";
	}

	@Override
	public List<Integer> getEntityRemoveQueue(Player player) {
		return Collections.emptyList();
	}

	@Override
	public String getClassName(String path) {
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
	}


}
