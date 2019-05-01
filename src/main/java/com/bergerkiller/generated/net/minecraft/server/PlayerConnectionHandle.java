package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;

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

    public abstract void sendPacket(CommonPacket packet);
    public abstract void sendPos(double x, double y, double z);
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

