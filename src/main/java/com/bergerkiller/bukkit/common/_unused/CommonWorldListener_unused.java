package com.bergerkiller.bukkit.common._unused;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.reflection.SafeMethod;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldManager;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldServer;
import com.bergerkiller.server.CommonNMS;

import net.minecraft.server.v1_11_R1.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Matthijs
 */
public class CommonWorldListener_unused extends WorldManager {

    private boolean isEnabled = false;
    private HashSet<EntityPlayer> addedPlayers = new HashSet<EntityPlayer>();

    public CommonWorldListener_unused(org.bukkit.World world) {
        super(CommonNMS.getMCServer(), CommonNMS.getNative(world));
    }

    public static boolean isValid() {
    	return NMSWorld.accessList.isValid();
    }

    /**
     * Enables the listener<br>
     * Will send entity add messages for all current entities
     */
    public void enable() {
        if (isValid()) {
        	Object world = NMSWorldManager.world.getInternal(this);
            NMSWorldServer.accessList.get(world).add(this);
            List<?> l = NMSWorld.players.get(world);
            for (Object x : l) {
                if (x instanceof EntityPlayer) {
                    this.addedPlayers.add((EntityPlayer) x);
                }
            }
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
            NMSWorldServer.accessList.get(NMSWorldManager.world.getInternal(this)).remove(this);
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
    public void a(EntityHuman human, SoundEffect soundeffect, SoundCategory soundcategory, double x, double y, double z, float yaw, float pitch) {
    }

    /*
     * Method is part of SportBukkit only!
     */
    public void a(String text, double d0, double d1, double d2, float f0, float f1, Entity entity) {
    }

    @Override
    public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int[] aint) {
    }

    @Override
    public void a(int i, int j, int k, int l, int i1, int j1) {
    }

    @Override
    public void a(BlockPosition blockposition) {
    }

    @Override
    public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
    }

    @Override
    public void a(int i, BlockPosition blockposition, int j) {
    }

    @Override
    public void b(int i, BlockPosition blockposition, int j) {
    }

    @Override
    public void a(SoundEffect soundeffect, BlockPosition blockposition) {
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i) {
    }

    static {
        // Validate that ALL methods in WorldManager are properly overrided
        for (Method method : WorldManager.class.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            SafeMethod<?> commonMethod = new SafeMethod<Void>(method);
            if (!commonMethod.isOverridedIn(CommonWorldListener_unused.class)) {
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
                Logging.LOGGER.log(Level.WARNING, msg.toString());
            }
        }
    }
}
