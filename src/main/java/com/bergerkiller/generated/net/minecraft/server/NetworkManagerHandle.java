package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import io.netty.channel.Channel;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.NetworkManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class NetworkManagerHandle extends Template.Handle {
    /** @See {@link NetworkManagerClass} */
    public static final NetworkManagerClass T = new NetworkManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NetworkManagerHandle.class, "net.minecraft.server.NetworkManager", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static NetworkManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isConnected();
    public abstract Channel getChannel();
    public abstract void setChannel(Channel value);
    /**
     * Stores class members for <b>net.minecraft.server.NetworkManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NetworkManagerClass extends Template.Class<NetworkManagerHandle> {
        public final Template.Field<Channel> channel = new Template.Field<Channel>();

        public final Template.Method<Boolean> isConnected = new Template.Method<Boolean>();

    }

}

