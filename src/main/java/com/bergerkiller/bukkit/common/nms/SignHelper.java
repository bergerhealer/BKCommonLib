package com.bergerkiller.bukkit.common.nms;

import org.bukkit.World;

public class SignHelper {
	public static boolean replace(int x, int y, int z, World w) {
		return RemoteSign.replace(x, y, z, w);
	}
}
