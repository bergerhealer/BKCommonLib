package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import org.bukkit.Server;
import org.bukkit.World;

import java.util.List;

@Deprecated
public class NMSWorld {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("World");

    private static final MethodAccessor<Server> getServer  = WorldHandle.T.getServer.raw.toMethodAccessor();

    public static final FieldAccessor<World> bukkitWorld   =  WorldHandle.T.bukkitWorld.toFieldAccessor();

    public static final FieldAccessor<Object> worldProvider = WorldHandle.T.worldProvider.raw.toFieldAccessor();

    /**
     * This field is only available >= MC 1.10.2
     */
    @Deprecated
    public static final FieldAccessor<Object> navigationListener = WorldHandle.T.navigationListener.raw.toFieldAccessor();

    public static final MethodAccessor<Boolean> getBlockCollisions = WorldHandle.T.getBlockCollisions.raw.toMethodAccessor();

    //public static final FieldAccessor<List> entityRemovalList = TEMPLATE.getField("h"); TODO: Disabling it for now to support PaperSpigot. Fixing it later.

    public static final MethodAccessor<List<?>> getEntities = WorldHandle.T.getNearbyEntities.raw.toMethodAccessor();

    public static final int UPDATE_PHYSICS = 0x1; // flag specifying block physics should occur after the change
    public static final int UPDATE_NOTIFY = 0x2; // flag specifying the change should be updated to players
    public static final int UPDATE_DEFAULT = (UPDATE_PHYSICS | UPDATE_NOTIFY); // default flags used when updating block types

    public static Server getServer(Object worldHandle) {
        return getServer.invoke(worldHandle);
    }

    public static boolean updateBlock(Object worldHandle, int x, int y, int z, BlockData data, int updateFlags) {
        return (Boolean) WorldHandle.T.setBlockData.raw.invoke(worldHandle, NMSVector.newPosition(x, y, z), data.getData(), updateFlags);
    }
}
