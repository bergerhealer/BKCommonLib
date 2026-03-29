package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.network.ConnectionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import io.netty.channel.Channel;

/**
 * Deprecated: use ConnectionHandle (or not at all)
 */
@Deprecated
public class NMSNetworkManager {
    public static final ClassTemplate<?> T = ClassTemplate.create(ConnectionHandle.T.getType());

    //	public static final FieldAccessor<Queue<Object>> highPriorityQueue = TEMPLATE.getField("l");

    public static final FieldAccessor<Channel> channel = ConnectionHandle.T.channel.toFieldAccessor();

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
    public static final MethodAccessor<Boolean> getIsOpen = ConnectionHandle.T.isConnected.toMethodAccessor();
}
