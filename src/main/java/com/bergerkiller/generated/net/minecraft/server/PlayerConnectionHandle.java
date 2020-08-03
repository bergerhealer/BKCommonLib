package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerConnection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PlayerConnection")
public abstract class PlayerConnectionHandle extends Template.Handle {
    /** @See {@link PlayerConnectionClass} */
    public static final PlayerConnectionClass T = Template.Class.create(PlayerConnectionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PlayerConnectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void sendPacket(Object packet);
    public abstract void sendPos(double x, double y, double z);

    private static final QueuePacketMethod defaultQueuePacketMethod = NetworkManagerHandle::queuePacketUnsafe;
    private static final java.util.Map<Class<?>, QueuePacketMethod> queuePacketMethods = new java.util.concurrent.ConcurrentHashMap<Class<?>, QueuePacketMethod>(5, 0.75f, 2);

    private static interface QueuePacketMethod {
        boolean queuePacket(Object networkManager, Object packet);
    }

    static {
        queuePacketMethods.put(NetworkManagerHandle.T.getType(), defaultQueuePacketMethod);
    }

    private static QueuePacketMethod findPacketMethod(Class<?> networkManagerType) throws Throwable {
        String typeName = networkManagerType.getName();

        if (typeName.startsWith("com.denizenscript.denizen.nms.") && typeName.endsWith("DenizenNetworkManagerImpl")) {
            final com.bergerkiller.mountiplex.reflection.util.FastField<Object> oldManagerField = new com.bergerkiller.mountiplex.reflection.util.FastField<Object>();
            oldManagerField.init(networkManagerType.getDeclaredField("oldManager"));
            oldManagerField.forceInitialization();
            return (networkManager, packet) -> {
                Object oldManager = oldManagerField.get(networkManager);
                return queuePacket(oldManager, packet);
            };
        } else if (typeName.startsWith("com.denizenscript.denizen.nms.") && typeName.endsWith("FakeNetworkManagerImpl")) {
            return defaultQueuePacketMethod;
        }

        return null;
    }

    private static boolean queuePacket(Object networkManager, Object packet) {
        if (networkManager != null) {
            QueuePacketMethod method = queuePacketMethods.get(networkManager.getClass());
            if (method == null) {
                try {
                    method = findPacketMethod(networkManager.getClass());
                } catch (Throwable t) {
                }
                if (method != null) {
                    queuePacketMethods.put(networkManager.getClass(), method);
                } else {
                    queuePacketMethods.put(networkManager.getClass(), (n, p) -> {return false;});
                    com.bergerkiller.bukkit.common.Logging.LOGGER_NETWORK.warning("Unsupported NetworkManager detected: " + networkManager.getClass().getName());
                    return false;
                }
            }
            if (method.queuePacket(networkManager, packet)) {
                return true;
            }
        }

        return false;
    }

    public void queuePacket(Object packet) {
        if (!queuePacket(getNetworkManager(), packet)) {
            com.bergerkiller.bukkit.common.utils.CommonUtil.nextTick(() -> sendPacket(packet));
        }
    }


    public boolean isConnected() {
        return com.bergerkiller.generated.net.minecraft.server.NetworkManagerHandle.T.isConnected.invoke(getNetworkManager()).booleanValue();
    }


    public static PlayerConnectionHandle forPlayer(org.bukkit.entity.Player player) {
        Object handle = com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(player);
        if (!EntityPlayerHandle.T.isType(handle)) return null; // Check not NPC player

        final PlayerConnectionHandle connection = EntityPlayerHandle.T.playerConnection.get(handle);
        if (connection == null || !connection.isConnected()) {
            return null; // No PlayerConnection instance or not connected
        }
        return connection;
    }
    public abstract Object getNetworkManager();
    public abstract void setNetworkManager(Object value);
    /**
     * Stores class members for <b>net.minecraft.server.PlayerConnection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerConnectionClass extends Template.Class<PlayerConnectionHandle> {
        public final Template.Field.Converted<Object> networkManager = new Template.Field.Converted<Object>();

        public final Template.Method.Converted<Void> sendPacket = new Template.Method.Converted<Void>();
        public final Template.Method<Void> sendPos = new Template.Method<Void>();

    }

}

