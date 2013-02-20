package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import org.bukkit.Server;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeDirectField;

public class WorldServerRef {
	public static final ClassTemplate<?> WORLD = NMSClassTemplate.create("World");
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("WorldServer");
	public static final FieldAccessor<Object> playerManager = TEMPLATE.getField("manager");
	public static final FieldAccessor<List<Object>> accessList = TEMPLATE.getField("v");
	/**
	 * Type: IntHashMap
	 */
	public static final FieldAccessor<Object> entitiesById = TEMPLATE.getField("entitiesById");
	public static final FieldAccessor<Object> chunkProviderServer = new SafeDirectField<Object>() {
		private final FieldAccessor<Object> field1 = TEMPLATE.getField("chunkProviderServer");
		private final FieldAccessor<Object> field2 = TEMPLATE.getField("chunkProvider");

		@Override
		public Object get(Object instance) {
			return field1.get(instance);
		}

		@Override
		public boolean set(Object instance, Object value) {
			return field1.set(instance, value) && field2.set(instance, value);
		}
	};
	private static final MethodAccessor<Server> getServer = TEMPLATE.getMethod("getServer");

	public static Server getServer(Object worldHandle) {
		return getServer.invoke(worldHandle);
	}
}
