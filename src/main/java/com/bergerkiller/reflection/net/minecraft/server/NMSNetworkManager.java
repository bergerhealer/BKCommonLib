package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

import io.netty.channel.Channel;

import java.util.Queue;

public class NMSNetworkManager {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("NetworkManager");

    static {
    	// Note: no proper name to hook from. h might be subject to change!
    	T.nextField("private final EnumProtocolDirection h");
    }

    public static final FieldAccessor<Queue<Object>> queue = T.nextFieldSignature("private final Queue<NetworkManager.QueuedPacket> i");

    //	public static final FieldAccessor<Queue<Object>> highPriorityQueue = TEMPLATE.getField("l");

    public static final FieldAccessor<Channel> channel = T.nextField("public io.netty.channel.Channel channel");

    /*
     # public boolean ##METHODNAME##() {
     *     return this.channel != null && this.channel.isOpen();
     * }
     * 
     * public void sendPacket(Packet<?> packet) {
     #     if (this.##METHODNAME##()) {
     *         this.m();
     *         this.a(packet, (GenericFutureListener[]) null);
     *     } else {
     *         ...
     *     }
     *     ...
     * }
     */
    public static final MethodAccessor<Boolean> getIsOpen = T.selectMethod("public boolean isConnected()");
}
