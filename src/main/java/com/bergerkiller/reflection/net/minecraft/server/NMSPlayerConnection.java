package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.PlayerConnectionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

@Deprecated
public class NMSPlayerConnection {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerConnection");

    public static final FieldAccessor<Object> networkManager = PlayerConnectionHandle.T.networkManager.raw.toFieldAccessor();

    public static void sendPacket(Object instance, Object packet) {
        PlayerConnectionHandle.T.sendPacket.invoke(instance, packet);
    }
}
