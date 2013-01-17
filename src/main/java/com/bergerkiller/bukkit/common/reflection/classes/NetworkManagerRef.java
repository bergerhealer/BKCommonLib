package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_4_R1.NetworkManager;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class NetworkManagerRef {
	public static FieldAccessor<Integer> queueSize = new SafeField<Integer>(NetworkManager.class, "y");
}
