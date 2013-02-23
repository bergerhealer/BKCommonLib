package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import org.bukkit.Server;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class WorldRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("World");
	private static final MethodAccessor<Server> getServer = TEMPLATE.getMethod("getServer");
	public static final FieldAccessor<List<Object>> tileEntityList = TEMPLATE.getField("tileEntityList");

	public static Server getServer(Object worldHandle) {
		return getServer.invoke(worldHandle);
	}
}
