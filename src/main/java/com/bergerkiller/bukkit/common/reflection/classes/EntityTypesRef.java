package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.HashMap;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

@SuppressWarnings("rawtypes")
public class EntityTypesRef {
	private static final ClassTemplate<?> TEMPLATE = new NMSClassTemplate("EntityTypes");
	private static final MethodAccessor<Void> register =TEMPLATE.getMethod("a", Class.class, String.class, int.class);
	public static final HashMap<String, Class> namesToClass = TEMPLATE.getStaticFieldValue("c");
	public static final HashMap<Class, String> classToNames = TEMPLATE.getStaticFieldValue("d");
	public static final HashMap<Integer, Class> idToClass = TEMPLATE.getStaticFieldValue("e");
	public static final HashMap<Class, Integer> classToId = TEMPLATE.getStaticFieldValue("f");
	public static final HashMap<String, Integer> namesToId = TEMPLATE.getStaticFieldValue("g");

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
