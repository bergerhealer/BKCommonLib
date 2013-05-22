package com.bergerkiller.bukkit.common.server;

import org.bukkit.Bukkit;

public class UnknownServer implements CommonServer {

	@Override
	public String getServerVersion() {
		return Bukkit.getServer().getVersion();
	}

	@Override
	public String getServerName() {
		return "Unknown server (" + Bukkit.getServer().getName() + ")";
	}

	@Override
	public boolean isCompatible() {
		return false;
	}

	@Override
	public String getMinecraftVersion() {
		return "UNKNOWN";
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
}
