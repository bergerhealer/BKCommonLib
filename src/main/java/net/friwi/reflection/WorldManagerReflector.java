package net.friwi.reflection;

import com.bergerkiller.bukkit.common.internal.CommonWorldListener;
import net.minecraft.server.v1_9_R1.WorldManager;
import net.minecraft.server.v1_9_R1.WorldServer;

import java.lang.reflect.Field;

public class WorldManagerReflector {

    public static WorldServer get(CommonWorldListener l) {
        try {
            Field f = WorldManager.class.getDeclaredField("world");
            f.setAccessible(true);
            return (WorldServer) f.get(l);
        } catch (Exception e) {
            System.out.println("Could not get WorldServer object from CommonWorldListener");
            return null;
        }
    }
}
