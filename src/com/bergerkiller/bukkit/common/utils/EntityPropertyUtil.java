package com.bergerkiller.bukkit.common.utils;

import org.bukkit.entity.Entity;

public class EntityPropertyUtil extends EntityGroupingUtil {

	public static double getLocX(Entity entity) {
		return NativeUtil.getNative(entity).locX;
	}

	public static void setLocX(Entity entity, double value) {
		NativeUtil.getNative(entity).locX = value;
	}

	public static double getLocY(Entity entity) {
		return NativeUtil.getNative(entity).locY;
	}

	public static void setLocY(Entity entity, double value) {
		NativeUtil.getNative(entity).locY = value;
	}

	public static double getLocZ(Entity entity) {
		return NativeUtil.getNative(entity).locZ;
	}

	public static void setLocZ(Entity entity, double value) {
		NativeUtil.getNative(entity).locZ = value;
	}

	public static double getLastX(Entity entity) {
		return NativeUtil.getNative(entity).lastX;
	}

	public static void setLastX(Entity entity, double value) {
		NativeUtil.getNative(entity).lastX = value;
	}

	public static double getLastY(Entity entity) {
		return NativeUtil.getNative(entity).lastY;
	}

	public static void setLastY(Entity entity, double value) {
		NativeUtil.getNative(entity).lastY = value;
	}

	public static double getLastZ(Entity entity) {
		return NativeUtil.getNative(entity).lastZ;
	}

	public static void setLastZ(Entity entity, double value) {
		NativeUtil.getNative(entity).lastZ = value;
	}
}
