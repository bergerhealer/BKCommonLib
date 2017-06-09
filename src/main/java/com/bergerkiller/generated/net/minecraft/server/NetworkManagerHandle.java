package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import io.netty.channel.Channel;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.NetworkManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class NetworkManagerHandle extends Template.Handle {
    /** @See {@link NetworkManagerClass} */
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

    /**
     * Stores class members for <b>net.minecraft.server.NetworkManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NetworkManagerClass extends Template.Class<NetworkManagerHandle> {
        public final Template.Field<Channel> channel = new Template.Field<Channel>();

        public final Template.Method<Boolean> isConnected = new Template.Method<Boolean>();

    }

}

