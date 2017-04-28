package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class NMSPlayerConnection {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerConnection");
    
    public static final FieldAccessor<Object> networkManager = T.nextField("public final NetworkManager networkManager");
    
    private static final MethodAccessor<Void> sendPacket = T.selectMethod("public void sendPacket(Packet<?> packet)");

    public static void sendPacket(Object instance, Object packet) {
        sendPacket.invoke(instance, packet);
    }
}
