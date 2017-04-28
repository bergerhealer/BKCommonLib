package com.bergerkiller.bukkit.common.internal.hooks;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.Invokable;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;

/**
 * Handles all the method calls coming from a WorldListener instance that is hooked.
 * Most of it is ignored and discarded. We need it for Entity Add/Remove event handling.
 */
public class WorldListenerHook extends ClassHook<WorldListenerHook> {
    public static final Object instance = new WorldListenerHook().createInstance(CommonUtil.getNMSClass("IWorldAccess"));

    public static void hook(World world) {
        List<Object> accessList = NMSWorld.accessList.get(Conversion.toWorldHandle.convert(world));
        if (!accessList.contains(instance)) {
            accessList.add(instance);
        }
    }

    public static void unhook(World world) {
        NMSWorld.accessList.get(Conversion.toWorldHandle.convert(world)).remove(instance);
    }

    @Override
    protected Invokable getCallback(Method method) {
        // First check if this method is hooked
        Invokable result = super.getCallback(method);
        if (result != null) {
            return result;
        }

        // Allow methods declared in Object through
        if (method.getDeclaringClass().equals(Object.class)) {
            return null;
        }

        // All others are ignored
        return new NullInvokable(method);
    }

    @HookMethod("public void a(Entity entity)")
    public void onEntityAdded(Object entity) {
        CommonPlugin.getInstance().notifyAdded(Conversion.toEntity.convert(entity));
    }

    @HookMethod("public void b(Entity entity)")
    public void onEntityRemoved(Object entity) {
        CommonPlugin.getInstance().notifyRemoved(Conversion.toEntity.convert(entity));
    }
}
