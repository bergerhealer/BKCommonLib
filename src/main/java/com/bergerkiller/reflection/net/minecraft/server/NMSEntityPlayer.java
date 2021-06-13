package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.network.PlayerConnectionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

import org.bukkit.entity.Player;

/**
 * Deprecated: use EntityPlayerHandle instead
 */
@Deprecated
public class NMSEntityPlayer extends NMSEntityHuman {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityPlayer");

    public static final FieldAccessor<Object> playerConnection = EntityPlayerHandle.T.playerConnection.raw.toFieldAccessor();

    public static Object getNetworkManager(Player player) {
        PlayerConnectionHandle conn = EntityPlayerHandle.T.playerConnection.get(Conversion.toEntityHandle.convert(player));
        return conn == null ? null : conn.getNetworkManager();
    }
}
