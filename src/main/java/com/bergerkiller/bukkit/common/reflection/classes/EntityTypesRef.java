package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.HashMap;

import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;

import net.minecraft.server.v1_4_6.EntityTypes;

@SuppressWarnings("rawtypes")
public class EntityTypesRef {
	private static final MethodAccessor<Void> register = new SafeMethod<Void>(EntityTypes.class, "a", Class.class, String.class, int.class);
	public static final HashMap<String, Class> namesToClass = SafeField.get(EntityTypes.class, "b");
	public static final HashMap<Class, String> classToNames = SafeField.get(EntityTypes.class, "c");
	public static final HashMap<Integer, Class> idToClass = SafeField.get(EntityTypes.class, "d");
	public static final HashMap<Class, Integer> classToId = SafeField.get(EntityTypes.class, "e");
	public static final HashMap<String, Integer> namesToId = SafeField.get(EntityTypes.class, "f");

	/**
	 * Registers a new entity
	 * 
	 * @param entityClass to register
	 * @param entityName of the entity to register
	 * @param entityId of the entity to register
	 */
	public static void register(Class<?> entityClass, String entityName, int entityId) {
		register.invoke(null, entityClass, entityName, entityId);
	}

	/**
	 * Unregisters a registered entity
	 * 
	 * @param entityClass to unregister
	 */
	public static void unregister(Class<?> entityClass) {
		String name = classToNames.remove(entityClass);
		if (name != null) {
			namesToClass.remove(name);
			namesToId.remove(name);
		}
		Integer id = classToId.remove(entityClass);
		if (id != null) {
			idToClass.remove(id);
		}
	}
}
