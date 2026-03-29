package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

import org.bukkit.entity.Player;

/**
 * Deprecated: use ServerPlayerHandle instead
 */
@Deprecated
public class NMSEntityPlayer extends NMSEntityHuman {
    public static final ClassTemplate<?> T = ClassTemplate.create(ServerPlayerHandle.T.getType());

    public static final FieldAccessor<Object> playerConnection = ServerPlayerHandle.T.playerConnection.raw.toFieldAccessor();

    public static Object getNetworkManager(Player player) {
        ServerGamePacketListenerImplHandle conn = ServerPlayerHandle.T.playerConnection.get(HandleConversion.toEntityHandle(player));
        return conn == null ? null : conn.getNetworkManager();
    }
}
