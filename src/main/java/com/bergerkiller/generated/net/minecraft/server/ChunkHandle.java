package com.bergerkiller.generated.net.minecraft.server;

import org.bukkit.block.BlockState;
import org.bukkit.Chunk;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.Map;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

public class ChunkHandle extends Template.Handle {
    public static final ChunkClass T = new ChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkHandle.class, "net.minecraft.server.Chunk");


    /* ============================================================================== */

    public static ChunkHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ChunkHandle handle = new ChunkHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public BlockData getBlockData(IntVector3 blockposition) {
        return T.getBlockData.invoke(instance, blockposition);
    }

    public void addEntity(EntityHandle entity) {
        T.addEntity.invoke(instance, entity);
    }

    public int getHeight(int x, int z) {
        return T.getHeight.invoke(instance, x, z);
    }

    public int getBrightness(EnumSkyBlockHandle enumskyblock, IntVector3 position) {
        return T.getBrightness.invoke(instance, enumskyblock, position);
    }

    public static ChunkHandle fromBukkit(org.bukkit.Chunk chunk) {
        if (chunk != null) {
            return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toChunkHandle(chunk));
        } else {
            return null;
        }
    }

    public Map<IntVector3, BlockState> getTileEntities() {
        return T.tileEntities.get(instance);
    }

    public void setTileEntities(Map<IntVector3, BlockState> value) {
        T.tileEntities.set(instance, value);
    }

    public Object getEntitySlices() {
        return T.entitySlices.get(instance);
    }

    public void setEntitySlices(Object value) {
        T.entitySlices.set(instance, value);
    }

    public Chunk getBukkitChunk() {
        return T.bukkitChunk.get(instance);
    }

    public void setBukkitChunk(Chunk value) {
        T.bukkitChunk.set(instance, value);
    }

    public static final class ChunkClass extends Template.Class<ChunkHandle> {
        public final Template.Field.Converted<Map<IntVector3, BlockState>> tileEntities = new Template.Field.Converted<Map<IntVector3, BlockState>>();
        public final Template.Field.Converted<Object> entitySlices = new Template.Field.Converted<Object>();
        public final Template.Field<Chunk> bukkitChunk = new Template.Field<Chunk>();

        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> addEntity = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> getHeight = new Template.Method<Integer>();
        public final Template.Method.Converted<Integer> getBrightness = new Template.Method.Converted<Integer>();

    }
}
