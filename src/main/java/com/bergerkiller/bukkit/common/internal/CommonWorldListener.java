package com.bergerkiller.bukkit.common.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

import net.friwi.reflection.WorldManagerReflector;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import net.minecraft.server.v1_9_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_9_R1.SoundCategory;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.World;
import net.minecraft.server.v1_9_R1.WorldManager;
import net.minecraft.server.v1_9_R1.WorldServer;

/**
 *
 * @author Matthijs
 */
public class CommonWorldListener extends WorldManager {

    private boolean isEnabled = false;
    private HashSet<EntityPlayer> addedPlayers = new HashSet<EntityPlayer>();

    public CommonWorldListener(org.bukkit.World world) {
        super(CommonNMS.getMCServer(), CommonNMS.getNative(world));
    }

    public static boolean isValid() {
        return true;
//		return WorldServerRef.accessList.isValid();
    }

    /**
     * Enables the listener<br>
     * Will send entity add messages for all current entities
     */
    public void enable() {
        if (isValid()) {
            WorldServerRef.accessList.get(WorldManagerReflector.get(this)).add(this);
            List<EntityHuman> l = WorldManagerReflector.get(this).players;
            for (EntityHuman x : l) {
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
            WorldServerRef.accessList.get(WorldManagerReflector.get(this)).remove(this);
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

//    public void a(EntityHuman human, String name, double x, double y, double z, float yaw, float pitch) {
//    }

    /*
     * Method is part of SportBukkit only!
     */
    public void a(String text, double d0, double d1, double d2, float f0, float f1, Entity entity) {
    }

    @Override
    public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int[] aint) {
    }

//    public void a(String s, double d0, double d1, double d2, float f, float f1) {
//    }

    @Override
    public void a(int i, int j, int k, int l, int i1, int j1) {
    }

    @Override
    public void a(BlockPosition blockposition) {
    }
//
//    public void b(BlockPosition blockposition) {
//    }
//
//    public void a(String s, BlockPosition blockposition) {
//    }

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
        try{
        	((WorldServer)worldf.get(this)).getPlayerChunkMap().flagDirty(blockposition);
        }catch(Exception e){
        	e.printStackTrace();
        }
    	
      }
    
    @Override
    public void a(EntityHuman entityhuman, SoundEffect soundeffect, SoundCategory soundcategory, double d0, double d1, double d2, float f, float f1)
    {
    	 try{
         	((MinecraftServer)af.get(this)).getPlayerList().sendPacketNearby(entityhuman, d0, d1, d2, f > 1.0F ? 16.0F * f : 16.0D, ((WorldServer)worldf.get(this)).dimension, new PacketPlayOutNamedSoundEffect(soundeffect, soundcategory, d0, d1, d2, f, f1));
         }catch(Exception e){
         	e.printStackTrace();
         }
    }
    static Field worldf;
    static Field af;
    static {
    	try{
    		worldf = WorldManager.class.getDeclaredField("world");
    		worldf.setAccessible(true);
    		af = WorldManager.class.getDeclaredField("a");
    		af.setAccessible(true);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        // Validate that ALL methods in WorldManager are properly overrided
        for (Method method : WorldManager.class.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            SafeMethod<?> commonMethod = new SafeMethod<Void>(method);
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
