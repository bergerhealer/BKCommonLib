package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.Server;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class NMSWorld {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("World");

    private static final MethodAccessor<Server> getServer  = WorldHandle.T.getServer.raw.toMethodAccessor();

    public static final FieldAccessor<World> bukkitWorld   =  WorldHandle.T.bukkitWorld.toFieldAccessor();
    public static final TranslatorFieldAccessor<IntHashMap<Object>> entitiesById = T.selectField("protected final IntHashMap<Entity> entitiesById").translate(DuplexConversion.intHashMap);

    public static final FieldAccessor<Object> worldProvider = WorldHandle.T.worldProvider.raw.toFieldAccessor();
    public static final FieldAccessor<Object> navigationListener = WorldHandle.T.navigationListener.raw.toFieldAccessor();
    public static final FieldAccessor<List<Object>> accessList = (FieldAccessor) WorldHandle.T.accessList.raw.toFieldAccessor();

    public static final FieldAccessor<List<Object>> players = (FieldAccessor) WorldHandle.T.players.raw.toFieldAccessor();

    public static final MethodAccessor<Boolean> getBlockCollisions = WorldHandle.T.getBlockCollisions.raw.toMethodAccessor();

    //public static final FieldAccessor<List> entityRemovalList = TEMPLATE.getField("h"); TODO: Disabling it for now to support PaperSpigot. Fixing it later.

    public static final MethodAccessor<List<?>> getEntities = WorldHandle.T.getEntities.raw.toMethodAccessor();

    private static final MethodAccessor<Boolean> isChunkLoaded = WorldHandle.T.isChunkLoaded.toMethodAccessor();

    public static final int UPDATE_PHYSICS = 0x1; // flag specifying block physics should occur after the change
    public static final int UPDATE_NOTIFY = 0x2; // flag specifying the change should be updated to players
    public static final int UPDATE_DEFAULT = (UPDATE_PHYSICS | UPDATE_NOTIFY); // default flags used when updating block types

    public static boolean isChunkLoaded(Object worldHandle, int chunkX, int chunkZ) {
        return isChunkLoaded.invoke(worldHandle, chunkX, chunkZ, true);
    }

    public static Server getServer(Object worldHandle) {
        return getServer.invoke(worldHandle);
    }

    public static boolean updateBlock(Object worldHandle, int x, int y, int z, BlockData data, int updateFlags) {
        return (Boolean) WorldHandle.T.setBlockData.raw.invoke(worldHandle, NMSVector.newPosition(x, y, z), data.getData(), updateFlags);
    }

    public static List<Object> getTileList(org.bukkit.World world) {
        if (WorldHandle.T.tileEntityList.isAvailable()) {
            return new ArrayList<Object>((List<Object>) WorldHandle.T.tileEntityList.raw.get(HandleConversion.toWorldHandle(world)));
        } else {
            // Go by all the chunks and add their tile entities (slower!)
            //TODO: Maybe do this in a smart way so no new list has to be allocated?
            //      Iterate the chunks while iterating the list, for example.
            ArrayList<Object> tiles = new ArrayList<Object>();
            for (org.bukkit.Chunk chunk : WorldUtil.getChunks(world)) {
                Object chunkTileMap = ChunkHandle.T.tileEntities.raw.get(HandleConversion.toChunkHandle(chunk));
                tiles.addAll(((Map<?, Object>) chunkTileMap).values());
            }
            return tiles;
        }
    }
}
