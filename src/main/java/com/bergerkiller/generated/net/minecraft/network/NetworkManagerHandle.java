package com.bergerkiller.generated.net.minecraft.network;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import io.netty.channel.Channel;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.NetworkManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.NetworkManager")
public abstract class NetworkManagerHandle extends Template.Handle {
    /** @See {@link NetworkManagerClass} */
    public static final NetworkManagerClass T = Template.Class.create(NetworkManagerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NetworkManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static boolean queuePacketUnsafe(Object networkManager, Object packet) {
        return T.queuePacketUnsafe.invoke(networkManager, packet);
    }

    public abstract boolean isConnected();

    private static final java.lang.reflect.Constructor _queuedPacketConstructor;
    private static final Throwable _queuedPacketConstructorError;
    static {
        java.lang.reflect.Constructor c = null;
        Throwable error = null;
        try {
            Class<?> queuedPacketType = com.bergerkiller.bukkit.common.utils.CommonUtil.getClass("net.minecraft.network.NetworkManager$QueuedPacket");
            Class<?> listenerType = com.bergerkiller.bukkit.common.utils.CommonUtil.getClass("io.netty.util.concurrent.GenericFutureListener");
            if (queuedPacketType == null) {
                throw new IllegalStateException("Class QueuedPacket does not exist");
            }
            if (listenerType == null) {
                throw new IllegalStateException("Class GenericFutureListener does not exist");
            }
            Class<?> packetType = com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle.T.getType();
            if (com.bergerkiller.bukkit.common.internal.CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                c = queuedPacketType.getDeclaredConstructor(packetType, listenerType);
            } else {
                listenerType = com.bergerkiller.bukkit.common.utils.LogicUtil.getArrayType(listenerType);
                c = queuedPacketType.getDeclaredConstructor(packetType, listenerType);
            }
            c.setAccessible(true);
        } catch (Throwable t) {
            error = t;
            com.bergerkiller.bukkit.common.Logging.LOGGER_REFLECTION.log(java.util.logging.Level.SEVERE, "Failed to find queued packet constructor", t);
        }
        _queuedPacketConstructor = c;
        _queuedPacketConstructorError = error;
    }

    public static Object createQueuedPacket(Object packet) {
        try {
            return _queuedPacketConstructor.newInstance(packet, null);
        } catch (Throwable t) {
            if (_queuedPacketConstructorError != null) { // NPE
                throw new UnsupportedOperationException("Failed to find queued packet constructor", _queuedPacketConstructorError);
            } else {
                throw com.bergerkiller.mountiplex.MountiplexUtil.uncheckedRethrow(t);
            }
        }
    }
    public abstract Channel getChannel();
    public abstract void setChannel(Channel value);
    /**
     * Stores class members for <b>net.minecraft.network.NetworkManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NetworkManagerClass extends Template.Class<NetworkManagerHandle> {
        public final Template.Field<Channel> channel = new Template.Field<Channel>();

        public final Template.StaticMethod.Converted<Boolean> queuePacketUnsafe = new Template.StaticMethod.Converted<Boolean>();

        public final Template.Method<Boolean> isConnected = new Template.Method<Boolean>();

    }

}

