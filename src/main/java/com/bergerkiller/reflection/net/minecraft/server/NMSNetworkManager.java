package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.NetworkManagerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import io.netty.channel.Channel;

import java.util.Queue;

public class NMSNetworkManager {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("NetworkManager");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final FieldAccessor<Queue<Object>> queue = (FieldAccessor) NetworkManagerHandle.T.queue.raw.toFieldAccessor();

    //	public static final FieldAccessor<Queue<Object>> highPriorityQueue = TEMPLATE.getField("l");

    public static final FieldAccessor<Channel> channel = NetworkManagerHandle.T.channel.toFieldAccessor();

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
    public static final MethodAccessor<Boolean> getIsOpen = NetworkManagerHandle.T.isConnected.toMethodAccessor();
}
