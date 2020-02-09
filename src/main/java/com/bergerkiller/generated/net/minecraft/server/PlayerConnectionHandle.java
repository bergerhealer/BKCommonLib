package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerConnection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PlayerConnectionHandle extends Template.Handle {
    /** @See {@link PlayerConnectionClass} */
    public static final PlayerConnectionClass T = new PlayerConnectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerConnectionHandle.class, "net.minecraft.server.PlayerConnection", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PlayerConnectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void sendPacket(Object packet);
    public abstract void queuePacket(Object packet);
    public abstract void sendPos(double x, double y, double z);

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
        public final Template.Method<Void> queuePacket = new Template.Method<Void>();
        public final Template.Method<Void> sendPos = new Template.Method<Void>();

    }

}

