package com.bergerkiller.generated.net.minecraft.server;

import io.netty.channel.Channel;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

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

    public Channel getChannel() {
        return T.channel.get(instance);
    }

    public void setChannel(Channel value) {
        T.channel.set(instance, value);
    }

    public static final class NetworkManagerClass extends Template.Class<NetworkManagerHandle> {
        public final Template.Field<Channel> channel = new Template.Field<Channel>();

        public final Template.Method<Boolean> isConnected = new Template.Method<Boolean>();

    }

}

