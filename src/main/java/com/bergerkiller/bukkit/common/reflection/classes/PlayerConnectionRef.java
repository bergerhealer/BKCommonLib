package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class PlayerConnectionRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("PlayerConnection");
    public static final FieldAccessor<Object> networkManager = TEMPLATE.getField("networkManager");
<<<<<<< HEAD
    public static final FieldAccessor<Boolean> checkMovement = TEMPLATE.getField("B");
=======
    public static final FieldAccessor<Boolean> checkMovement = TEMPLATE.getField("checkMovement");
>>>>>>> 6c6809c31fa3f2895f50a974cd9b182317b26eb3
    private static final MethodAccessor<Void> sendPacket = TEMPLATE.getMethod("sendPacket", PacketType.DEFAULT.getType());

    public static void sendPacket(Object instance, Object packet) {
        sendPacket.invoke(instance, packet);
    }
}
