package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

/**
 * From Minecraft 1.14 onwards the best way to listen to entity add/remove events is
 * to hook the 'entitiesByUUID' map, and override the methods that add/remove from it.
 */
public class EntityAddRemoveHandler_1_17 extends EntityAddRemoveHandler {
    private final Class<?> levelCallbackType;
    private final FastField<Object> entityManagerField = new FastField<Object>();
    private final FastField<Object> callbacksField = new FastField<Object>();
    private final List<LevelCallbackInterceptor> hooks = new ArrayList<LevelCallbackInterceptor>();

    public EntityAddRemoveHandler_1_17() {
        // This is the interface we implement to receive events
        levelCallbackType = Resolver.loadClass("net.minecraft.world.level.entity.LevelCallback", false);
        if (levelCallbackType == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find LevelCallback class");
        }

        // Initialize accessor of PersistentEntitySectionManager WorldServer.entityManager
        Class<?> sectionManagerClass = Resolver.loadClass("net.minecraft.world.level.entity.PersistentEntitySectionManager", false);
        if (sectionManagerClass == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find PersistentEntitySectionManager class");
        }
        try {
            String fieldName = Resolver.resolveFieldName(WorldServerHandle.T.getType(), "entityManager");
            entityManagerField.init(WorldServerHandle.T.getType().getDeclaredField(fieldName));
            if (!sectionManagerClass.isAssignableFrom(entityManagerField.getType())) {
                throw new IllegalStateException("Field not assignable to PersistentEntitySectionManager");
            }
            entityManagerField.forceInitialization();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize WorldServer entityManager field: " + t.getMessage(), t);
            entityManagerField.initUnavailable("entityManager");
        }

        // Initialize the callbacks field of PersistentEntitySectionManager
        try {
            String fieldName = Resolver.resolveFieldName(sectionManagerClass, "callbacks");
            callbacksField.init(sectionManagerClass.getDeclaredField(fieldName));
            if (!levelCallbackType.equals(callbacksField.getType())) {
                throw new IllegalStateException("Field not assignable to LevelCallback");
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize PersistentEntitySectionManager callbacks field: " + t.getMessage(), t);
            callbacksField.initUnavailable("callbacks");
        }
    }

    @Override
    public void processEvents() {
        for (LevelCallbackInterceptor hook : hooks) {
            hook.processEvents();
        }
    }

    @Override
    public void hook(World world) {
        Object sectionManager = entityManagerField.get(HandleConversion.toWorldHandle(world));
        Object callbacks = callbacksField.get(sectionManager);
        if (ClassInterceptor.get(callbacks, LevelCallbackInterceptor.class) == null) {
            LevelCallbackInterceptor interceptor = new LevelCallbackInterceptor(callbacks, world);
            callbacksField.set(sectionManager, interceptor.createInstance(levelCallbackType));
            hooks.add(interceptor);
        }
    }

    @Override
    public void unhook(World world) {
        Object sectionManager = entityManagerField.get(HandleConversion.toWorldHandle(world));
        Object callbacks = callbacksField.get(sectionManager);
        LevelCallbackInterceptor interceptor = ClassInterceptor.get(callbacks, LevelCallbackInterceptor.class);
        if (interceptor != null) {
            callbacksField.set(sectionManager, interceptor.base);
            hooks.remove(interceptor);
        }
    }

    /**
     * WorldServer.entityManager -> PersistentEntitySectionManager.callbacks is hooked by this
     * class to be notified of entities being added or removed
     */
    public static class LevelCallbackInterceptor extends ClassInterceptor {
        private final Object base;
        private final World world;
        private final Queue<org.bukkit.entity.Entity> pendingAddEvents = new LinkedList<org.bukkit.entity.Entity>();

        public LevelCallbackInterceptor(Object base, World world) {
            this.base = base;
            this.world = world;
        }

        @Override
        protected Invoker<?> getCallback(Method ref_method) {
            // Create a default invoker that calls this same method on the original callback
            final FastMethod<Object> method = new FastMethod<Object>();
            method.init(ref_method);
            final Invoker<Object> baseInvoker = new Invoker<Object>() {
                @Override
                public Object invokeVA(Object instance, Object... args) {
                    return method.invokeVA(base, args);
                }
            };

            // Override certain methods
            String name = MPLType.getName(ref_method);
            Class<?>[] params = ref_method.getParameterTypes();

            // onCreated:f is fired when new entities are spawned, but not when existing
            // entities are loaded when chunks are loaded. For our purposes, it is not the right
            // callback event.

            // Overrides onTrackingStart:b - it is here we want to fire entity add events
            if (name.equals("b") && params.length == 1 && params[0] == Object.class) {
                return new Invoker<Object>() {
                    @Override
                    public Object invokeVA(Object instance, Object... args) {
                        // Callback
                        onEntityAdded(WrapperConversion.toEntity(args[0]));

                        // Execute normal invoking logic
                        return baseInvoker.invokeVA(base, args);
                    }
                };
            }

            // Overrides onTrackingEnd:a - it is here we want to fire entity remove events
            // The onDestroy callback only fires when entities are permanently removed
            if (name.equals("a") && params.length == 1 && params[0] == Object.class) {
                return new Invoker<Object>() {
                    @Override
                    public Object invokeVA(Object instance, Object... args) {
                        // Callback
                        onEntityRemoved(WrapperConversion.toEntity(args[0]));

                        // Execute normal invoking logic
                        return baseInvoker.invokeVA(base, args);
                    }
                };
            }
            
            return baseInvoker;
        }

        public void onEntityRemoved(Entity entity) {
            pendingAddEvents.remove(entity);
            CommonPlugin.getInstance().notifyRemoved(this.world, entity);
        }

        public void onEntityAdded(Entity entity) {
            CommonPlugin.getInstance().notifyAddedEarly(world, entity);
            pendingAddEvents.add(entity);
        }

        public void processEvents() {
            while (!pendingAddEvents.isEmpty()) {
                CommonPlugin.getInstance().notifyAdded(world, pendingAddEvents.poll());
            }
        }
    }

    @Override
    public void replace(World world, EntityHandle oldEntity, EntityHandle newEntity) {
    }
}
