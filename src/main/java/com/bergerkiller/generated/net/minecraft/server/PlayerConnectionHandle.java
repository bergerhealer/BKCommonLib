package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class PlayerConnectionHandle extends Template.Handle {
    public static final PlayerConnectionClass T = new PlayerConnectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerConnectionHandle.class, "net.minecraft.server.PlayerConnection");


    /* ============================================================================== */

    public static PlayerConnectionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PlayerConnectionHandle handle = new PlayerConnectionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void sendPacket(CommonPacket packet) {
        T.sendPacket.invoke(instance, packet);
    }

    public Object getNetworkManager() {
        return T.networkManager.get(instance);
    }

    public void setNetworkManager(Object value) {
        T.networkManager.set(instance, value);
    }

    public static final class PlayerConnectionClass extends Template.Class<PlayerConnectionHandle> {
        public final Template.Field.Converted<Object> networkManager = new Template.Field.Converted<Object>();

        public final Template.Method.Converted<Void> sendPacket = new Template.Method.Converted<Void>();

    }
}
