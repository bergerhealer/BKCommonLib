package com.bergerkiller.bukkit.common.reflection;

import net.minecraft.server.NetworkManager;

import com.bergerkiller.bukkit.common.SafeField;

public class NetworkManagerRef {
	public static SafeField<Integer> queueSize = new SafeField<Integer>(NetworkManager.class, "y");
}
