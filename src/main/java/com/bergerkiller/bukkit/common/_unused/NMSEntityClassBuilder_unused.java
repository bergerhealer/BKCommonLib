package com.bergerkiller.bukkit.common._unused;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;
import com.bergerkiller.server.CommonNMS;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Takes care of NMS Entity class creation, allowing multiple callback methods
 * to be implemented. All methods provided by a callback Class that are supposed
 * to be inherited by the produced Entity class instance, should be in a
 * separate interface Class implemented by the callback class.<br>
 * <br>
 * For example, an 'InventoryHookImpl' class implementing 'InventoryHook', with
 * InventoryHook declaring the methods 'setItem' and 'super_setItem'.<br>
 * <br>
 * To call super methods, add methods in the interface starting with
 * <i>super_</i>. These methods are automatically redirected to the base Entity
 * class.<br>
 * <br>
 * All callback classes must have a constructor that accepts a single
 * CommonEntity instance.
 */
public class NMSEntityClassBuilder_unused {

    private static final Class<?>[] DEFAULT_CONSTRUCTOR_TYPES = {NMSWorld.T.getType()};
    private static final Object[] DEFAULT_CONSTRUCTOR_ARGS = {CommonNMS.getWorlds().iterator().next()};
    private final List<Constructor<?>> callbackConstructors = new ArrayList<Constructor<?>>();
    private Class<?> baseClass;

    public NMSEntityClassBuilder_unused(Class<?> superClass, Collection<Class<?>> callbackClasses) {
        this.baseClass = superClass;
        /*
        for (Class<?> callbackClass : this.getCallbackClasses()) {
            try {
                callbackConstructors.add(callbackClass.getConstructor(CommonEntity.class));
            } catch (Throwable t) {
                throw new RuntimeException("Callback class '" + callbackClass.getName() + "' is invalid: No one-argument 'CommonEntity' constructor");
            }
        }
        */
    }

    /**
     * Creates a new Entity instance
     *
     * @return new Entiy instance
     */
    public synchronized Object create(CommonEntity<?> entity, Object... args) {
        EntityHook hook = new EntityHook();
        return hook.constructInstance(baseClass, DEFAULT_CONSTRUCTOR_TYPES, DEFAULT_CONSTRUCTOR_ARGS);
        /*
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
        */
    }
}
