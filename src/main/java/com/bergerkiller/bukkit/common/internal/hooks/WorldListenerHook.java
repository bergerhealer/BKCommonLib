package com.bergerkiller.bukkit.common.internal.hooks;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.IWorldAccessHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.Invokable;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;

/**
 * Handles all the method calls coming from a WorldListener instance that is hooked.
 * Most of it is ignored and discarded. We need it for Entity Add/Remove event handling.
 */
public class WorldListenerHook extends ClassHook<WorldListenerHook> {
    private final World world;

    public WorldListenerHook(World world) {
        this.world = world;
    }

    public static void hook(World world) {
        List<Object> accessList = NMSWorld.accessList.get(Conversion.toWorldHandle.convert(world));
        for (Object o : accessList) {
            if (get(o, WorldListenerHook.class) != null) {
                return; // Already hooked
            }
        }

        // Create a listener hook and add
        accessList.add(new WorldListenerHook(world).createInstance(IWorldAccessHandle.T.getType()));
    }

    public static void unhook(World world) {
        Iterator<Object> iter = NMSWorld.accessList.get(Conversion.toWorldHandle.convert(world)).iterator();
        while (iter.hasNext()) {
            if (get(iter.next(), WorldListenerHook.class) != null) {
                iter.remove();
            }
        }
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

    @HookMethod("public void onEntityAdded:???(Entity entity)")
    public void onEntityAdded(Object entity) {
        CommonPlugin.getInstance().notifyAdded(world, WrapperConversion.toEntity(entity));
    }

    @HookMethod("public void onEntityRemoved:???(Entity entity)")
    public void onEntityRemoved(Object entity) {
        CommonPlugin.getInstance().notifyRemoved(world, WrapperConversion.toEntity(entity));
    }
}
