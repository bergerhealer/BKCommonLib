package com.bergerkiller.bukkit.common.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import net.minecraft.server.v1_8_R1.BlockPosition;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.WorldManager;

class CommonWorldListener extends WorldManager {

    private boolean isEnabled = false;
    private HashSet<EntityPlayer> addedPlayers = new HashSet<>();

    public CommonWorldListener(org.bukkit.World world) {
        super(CommonNMS.getMCServer(), CommonNMS.getNative(world));
    }

    public static boolean isValid() {
        return WorldServerRef.accessList.isValid();
    }

    /**
     * Enables the listener<br>
     * Will send entity add messages for all current entities
     */
    @SuppressWarnings("unchecked")
    public void enable() {
        if (isValid()) {
            WorldServerRef.accessList.get(this.world).add(this);
            this.addedPlayers.addAll(this.world.players);
            this.isEnabled = true;
        } else {
            new RuntimeException("Failed to listen in World").printStackTrace();
        }
    }

    /**
     * Disables the listener
     */
    public void disable() {
        if (isValid()) {
            WorldServerRef.accessList.get(this.world).remove(this);
            this.addedPlayers.clear();
            this.isEnabled = false;
        }
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public final void a(Entity added) {
        if (added != null) {
            // Add entity
            if (added instanceof EntityPlayer && !this.addedPlayers.add((EntityPlayer) added)) {
                return;
            }
            // Notify it is added
            CommonPlugin.getInstance().notifyAdded(CommonNMS.getEntity(added));
            // Event
            CommonUtil.callEvent(new EntityAddEvent(CommonNMS.getEntity(added)));
        }
    }

    @Override
    public final void b(Entity removed) {
        if (removed != null) {
            // Remove entity
            if (removed instanceof EntityPlayer && !this.addedPlayers.remove(removed)) {
                return;
            }
            // Notify it is removed
            CommonPlugin.getInstance().notifyRemoved(CommonNMS.getEntity(removed));
            // Event
            CommonUtil.callEvent(new EntityRemoveEvent(CommonNMS.getEntity(removed)));
        }
    }

    @Override
    public final void a(BlockPosition blockposition) {
        // Block notify (physics)
    }

    @Override
    public void a(int arg0, BlockPosition blockposition, int arg4) {
    }

    @Override
    public void a(String name, double x, double y, double z, float yaw, float pitch) {
    }

    @Override
    public void a(EntityHuman human, int code, BlockPosition blockposition, int dat) {
    }

    @Override
    public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int[] aint) {
    }

    @Override
    public void b(BlockPosition blockposition) {
    }

    @Override
    public void a(String name, BlockPosition blockposition) {
    }

    @Override
    public void a(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
    }
    
    @Override
    public void b(int arg0, BlockPosition blockposition, int arg4) {
    }

    @Override
    public void a(EntityHuman human, String name, double x, double y, double z, float yaw, float pitch) {
    }

    /*
     * Method is part of SportBukkit only!
     */
    public void a(String text, double d0, double d1, double d2, float f0, float f1, Entity entity) {
    }

    static {
        // Validate that ALL methods in WorldManager are properly overrided
        for (Method method : WorldManager.class.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            SafeMethod<?> commonMethod = new SafeMethod<>(method);
            if (!commonMethod.isOverridedIn(CommonWorldListener.class)) {
                StringBuilder msg = new StringBuilder();
                msg.append("Method ");
                msg.append(method.getReturnType().getSimpleName()).append(' ');
                msg.append(method.getName()).append('(');
                boolean first = true;
                for (Class<?> param : method.getParameterTypes()) {
                    if (!first) {
                        msg.append(", ");
                    }
                    msg.append(param.getSimpleName());
                    first = false;
                }
                msg.append(") is not overrided in the World Listener!");
                CommonPlugin.LOGGER.log(Level.WARNING, msg.toString());
            }
        }
    }
}
