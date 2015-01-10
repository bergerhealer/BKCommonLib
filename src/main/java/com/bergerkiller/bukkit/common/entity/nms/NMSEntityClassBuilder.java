package com.bergerkiller.bukkit.common.entity.nms;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.ClassBuilder;
import com.bergerkiller.bukkit.common.reflection.classes.WorldRef;

/**
 * Takes care of NMS Entity class creation, allowing multiple callback methods to be implemented.
 * All methods provided by a callback Class that are supposed to be inherited by the produced Entity
 * class instance, should be in a separate interface Class implemented by the callback class.<br>
 * <br>
 * For example, an 'InventoryHookImpl' class implementing 'InventoryHook', with InventoryHook
 * declaring the methods 'setItem' and 'super_setItem'.<br>
 * <br>
 * To call super methods, add methods in the interface starting with <i>super_</i>.
 * These methods are automatically redirected to the base Entity class.<br>
 * <br>
 * All callback classes must have a constructor that accepts a single CommonEntity instance.
 */
public class NMSEntityClassBuilder extends ClassBuilder {
	private static final Class<?>[] DEFAULT_CONSTRUCTOR_TYPES = {WorldRef.TEMPLATE.getType()};
	private static final Object[] DEFAULT_CONSTRUCTOR_ARGS = {CommonNMS.getWorlds().iterator().next()};
	private final List<Constructor<?>> callbackConstructors = new ArrayList<Constructor<?>>();

	public NMSEntityClassBuilder(Class<?> superClass, Collection<Class<?>> callbackClasses) {
		super(superClass, callbackClasses);
		for (Class<?> callbackClass : this.getCallbackClasses()) {
			try {
				callbackConstructors.add(callbackClass.getConstructor(CommonEntity.class));
			} catch (Throwable t) {
				throw new RuntimeException("Callback class '" + callbackClass.getName() + "' is invalid: No one-argument 'CommonEntity' constructor");
			}
		}
	}

	/**
	 * Creates a new Entity instance
	 * 
	 * @return new Entiy instance
	 */
	public synchronized Object create(CommonEntity<?> entity, Object... args) {
		// Prepare new Callback instances
		List<Object> instances = new ArrayList<Object>(callbackConstructors.size());
		for (Constructor<?> callbackConstructor : callbackConstructors) {
			try {
				instances.add(callbackConstructor.newInstance(entity));
			} catch (Throwable t) {
				throw new RuntimeException("Unable to construct Callback Class:", t);
			}
		}
		
		// Create and return
		return create(DEFAULT_CONSTRUCTOR_TYPES, DEFAULT_CONSTRUCTOR_ARGS, instances);
	}
}
