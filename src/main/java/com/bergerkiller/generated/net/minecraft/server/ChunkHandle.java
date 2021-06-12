package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.EnumSkyBlockHandle;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import java.util.Collection;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Chunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.Chunk")
public abstract class ChunkHandle extends Template.Handle {
    /** @See {@link ChunkClass} */
    public static final ChunkClass T = Template.Class.create(ChunkClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getLocX();
    public abstract int getLocZ();
    public abstract List<Integer> getLoadedSectionCoordinates();
    public abstract ChunkSection[] getSections();
    public abstract ChunkSection getSection(int cy);
    public abstract Collection<?> getRawTileEntities();
    public abstract Collection<BlockState> getTileEntities();
    public abstract BlockData getBlockData(IntVector3 blockposition);
    public abstract BlockData getBlockDataAtCoord(int x, int y, int z);
    public abstract BlockData setBlockData(IntVector3 blockposition, BlockData iblockdata, int updateFlags);
    public abstract void addEntity(EntityHandle entity);
    public abstract HeightMap getLightHeightMap(boolean initialize);
    public abstract int getBrightness(EnumSkyBlockHandle enumskyblock, IntVector3 position);
    public abstract int getTopSliceY();
    public abstract void addEntities();
    public abstract boolean checkCanSave(boolean isNotAutosave);
    public abstract void markDirty();
    public abstract void markEntitiesDirty();

    public static ChunkHandle fromBukkit(org.bukkit.Chunk chunk) {
        if (chunk != null) {
            return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toChunkHandle(chunk));
        } else {
            return null;
        }
    }
    public abstract WorldHandle getWorld();
    public abstract void setWorld(WorldHandle value);
    public abstract List<Object>[] getEntitySlices();
    public abstract void setEntitySlices(List<Object>[] value);
    public abstract Chunk getBukkitChunk();
    public abstract void setBukkitChunk(Chunk value);
    /**
     * Stores class members for <b>net.minecraft.server.Chunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkClass extends Template.Class<ChunkHandle> {
        public final Template.Field.Converted<WorldHandle> world = new Template.Field.Converted<WorldHandle>();
        public final Template.Field.Converted<List<Object>[]> entitySlices = new Template.Field.Converted<List<Object>[]>();
        public final Template.Field<Chunk> bukkitChunk = new Template.Field<Chunk>();

        public final Template.Method<Integer> getLocX = new Template.Method<Integer>();
        public final Template.Method<Integer> getLocZ = new Template.Method<Integer>();
        public final Template.Method<List<Integer>> getLoadedSectionCoordinates = new Template.Method<List<Integer>>();
        public final Template.Method.Converted<ChunkSection[]> getSections = new Template.Method.Converted<ChunkSection[]>();
        public final Template.Method.Converted<ChunkSection> getSection = new Template.Method.Converted<ChunkSection>();
        public final Template.Method<Collection<?>> getRawTileEntities = new Template.Method<Collection<?>>();
        public final Template.Method<Collection<BlockState>> getTileEntities = new Template.Method<Collection<BlockState>>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<BlockData> getBlockDataAtCoord = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<BlockData> setBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> addEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<HeightMap> getLightHeightMap = new Template.Method.Converted<HeightMap>();
        public final Template.Method.Converted<Integer> getBrightness = new Template.Method.Converted<Integer>();
        public final Template.Method<Integer> getTopSliceY = new Template.Method<Integer>();
        public final Template.Method<Void> addEntities = new Template.Method<Void>();
        public final Template.Method<Boolean> checkCanSave = new Template.Method<Boolean>();
        public final Template.Method<Void> markDirty = new Template.Method<Void>();
        public final Template.Method<Void> markEntitiesDirty = new Template.Method<Void>();

    }

}

