package com.bergerkiller.generated.net.minecraft.server;

import java.util.Queue;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import io.netty.channel.Channel;

public class NetworkManagerHandle extends Template.Handle {
    public static final NetworkManagerClass T = new NetworkManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NetworkManagerHandle.class, "net.minecraft.server.NetworkManager");

    /* ============================================================================== */

    public static NetworkManagerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        NetworkManagerHandle handle = new NetworkManagerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public boolean isConnected() {
        return T.isConnected.invoke(instance);
    }

    public Queue<Object> getQueue() {
        return T.queue.get(instance);
    }

    public void setQueue(Queue<Object> value) {
        T.queue.set(instance, value);
    }

    public Channel getChannel() {
        return T.channel.get(instance);
    }

    public void setChannel(Channel value) {
        T.channel.set(instance, value);
    }

    public static final class NetworkManagerClass extends Template.Class<NetworkManagerHandle> {
        public final Template.Field.Converted<Queue<Object>> queue = new Template.Field.Converted<Queue<Object>>();
        public final Template.Field<Channel> channel = new Template.Field<Channel>();

        public final Template.Method<Boolean> isConnected = new Template.Method<Boolean>();

    }

}

