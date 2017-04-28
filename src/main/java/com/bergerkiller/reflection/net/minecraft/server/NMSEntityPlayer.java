package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

import org.bukkit.entity.Player;

public class NMSEntityPlayer extends NMSEntityHuman {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityPlayer");

    public static final FieldAccessor<Object> playerConnection = T.selectField("public PlayerConnection playerConnection");

    public static Object getNetworkManager(Player player) {
        Object conn = playerConnection.get(Conversion.toEntityHandle.convert(player));
        return conn == null ? null : NMSPlayerConnection.networkManager.get(conn);
    }
}
