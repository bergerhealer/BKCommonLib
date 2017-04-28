package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.Server;
import org.bukkit.World;

import java.util.List;

@SuppressWarnings("rawtypes")
public class NMSWorld {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("World");

    private static final MethodAccessor<Server> getServer  = T.selectMethod("public org.bukkit.craftbukkit.CraftServer getServer()");
    public static final FieldAccessor<List> tileEntityList =  T.selectField("public final List<TileEntity> tileEntityList");
    public static final FieldAccessor<World> bukkitWorld   =  T.selectField("private final org.bukkit.craftbukkit.CraftWorld world");
    public static final TranslatorFieldAccessor<IntHashMap<Object>> entitiesById = T.selectField("protected final IntHashMap<Entity> entitiesById").translate(ConversionPairs.intHashMap);

    public static final FieldAccessor<Object> worldProvider = T.nextField("public WorldProvider worldProvider");
    public static final FieldAccessor<Object> navigationListener = T.nextFieldSignature("protected NavigationListener t");
    public static final FieldAccessor<List<Object>> accessList = T.nextFieldSignature("protected List<IWorldAccess> u");

    public static final FieldAccessor<List<Object>> players = T.selectField("public final List<EntityHuman> players");

    public static final MethodAccessor<Boolean> getBlockCollisions = T.selectMethod("private boolean a(Entity entity, AxisAlignedBB axisalignedbb, boolean flag, List<AxisAlignedBB> list)");

    //public static final FieldAccessor<List> entityRemovalList = TEMPLATE.getField("h"); TODO: Disabling it for now to support PaperSpigot. Fixing it later.

    public static final MethodAccessor<Void> applyPhysics = T.selectMethod("public void applyPhysics(BlockPosition blockposition, Block block, boolean flag)");

    public static final MethodAccessor<List<?>> getEntities = T.selectMethod("public List<Entity> getEntities(Entity entity, AxisAlignedBB axisalignedbb)");

    private static final MethodAccessor<Boolean> isChunkLoaded = T.selectMethod("protected boolean isChunkLoaded(int paramInt1, int paramInt2, boolean paramBoolean)");

    public static final int UPDATE_PHYSICS = 0x1; // flag specifying block physics should occur after the change
    public static final int UPDATE_NOTIFY = 0x2; // flag specifying the change should be updated to players
    public static final int UPDATE_DEFAULT = (UPDATE_PHYSICS | UPDATE_NOTIFY); // default flags used when updating block types

    public static boolean isChunkLoaded(Object worldHandle, int chunkX, int chunkZ) {
        return isChunkLoaded.invoke(worldHandle, chunkX, chunkZ, true);
    }

    public static Server getServer(Object worldHandle) {
        return getServer.invoke(worldHandle);
    }

    private static final MethodAccessor<Boolean> setType = T.selectMethod("public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i)");

    public static boolean updateBlock(Object worldHandle, int x, int y, int z, BlockData data, int updateFlags) {
        return setType.invoke(worldHandle, NMSVector.newPosition(x, y, z), data.getData(), updateFlags);
    }
}
