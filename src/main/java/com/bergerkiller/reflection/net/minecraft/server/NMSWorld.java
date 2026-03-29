package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import org.bukkit.Server;
import org.bukkit.World;

import java.util.List;

@Deprecated
public class NMSWorld {
    public static final ClassTemplate<?> T = ClassTemplate.create(LevelHandle.T.getType());

    private static final MethodAccessor<Server> getServer  = LevelHandle.T.getServer.raw.toMethodAccessor();

    public static final FieldAccessor<World> bukkitWorld   =  LevelHandle.T.bukkitWorld.toFieldAccessor();

    public static final MethodAccessor<Boolean> getBlockCollisions = LevelHandle.T.getBlockCollisions.raw.toMethodAccessor();

    //public static final FieldAccessor<List> entityRemovalList = TEMPLATE.getField("h"); TODO: Disabling it for now to support PaperSpigot. Fixing it later.

    public static final MethodAccessor<List<?>> getEntities = LevelHandle.T.getNearbyEntities.raw.toMethodAccessor();

    public static final int UPDATE_PHYSICS = 0x1; // flag specifying block physics should occur after the change
    public static final int UPDATE_NOTIFY = 0x2; // flag specifying the change should be updated to players
    public static final int UPDATE_DEFAULT = (UPDATE_PHYSICS | UPDATE_NOTIFY); // default flags used when updating block types

    public static Server getServer(Object worldHandle) {
        return getServer.invoke(worldHandle);
    }

    public static boolean updateBlock(Object worldHandle, int x, int y, int z, BlockData data, int updateFlags) {
        Object blockPosition = BlockPosHandle.T.constr_x_y_z.raw.newInstance(x, y, z);
        return (Boolean) LevelHandle.T.setBlockData.raw.invoke(worldHandle,blockPosition, data.getData(), updateFlags);
    }
}
