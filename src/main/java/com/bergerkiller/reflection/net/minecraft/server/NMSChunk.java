package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Chunk;
import net.minecraft.server.v1_11_R1.EnumSkyBlock;
import net.minecraft.server.v1_11_R1.IBlockData;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;

import java.util.List;
import java.util.Map;

public class NMSChunk {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("Chunk");
    public static final FieldAccessor<Integer> x = T.selectField("public final int locX");
    public static final FieldAccessor<Integer> z = T.selectField("public final int locZ");
    public static final TranslatorFieldAccessor<ChunkSection[]> sections = T.selectField("private final ChunkSection[] sections").translate(DuplexConversion.chunkSectionArray);

    public static final MethodAccessor<byte[]> biomeData = T.selectMethod("public byte[] getBiomeIndex()");
    private static final MethodAccessor<Void> addEntities = T.selectMethod("public void addEntities()");
    private static final MethodAccessor<Void> loadNeighbours = T.selectMethod("public void loadNearby(IChunkProvider provider, ChunkGenerator generator, boolean newChunk)");
    private static final MethodAccessor<Boolean> needsSaving = T.selectMethod("public boolean a(boolean)");
    
    
    public static final TranslatorFieldAccessor<World> world = T.selectField("public final World world").translate(DuplexConversion.world);
    
    public static final FieldAccessor<Map<?, ?>> tileEntities = T.selectField("public final Map<BlockPosition, TileEntity> tileEntities");
    
    // Note: on Spigot it is a List[], on CraftBukkit it is a EntitySlice[]!!!
    public static final FieldAccessor<List<Object>[]> entitySlices = T.selectField("public final List<Entity>[] entitySlices");
    
    public static final FieldAccessor<Object> worldProvider = new SafeField<Object>(CommonUtil.getNMSClass("World"), "worldProvider",CommonUtil.getNMSClass("WorldProvider"));
    public static final FieldAccessor<Boolean> hasSkyLight = new SafeField<Boolean>(CommonUtil.getNMSClass("WorldProvider"), "e", boolean.class);

    public static final int XZ_MASK = 0xf;
    public static final int Y_MASK = 0xff;
    
    public static void loadNeighbours(Object chunkHandle, Object chunkProvider1, Object chunkProvider2, int x, int z) {
        loadNeighbours.invoke(chunkHandle, chunkProvider1, chunkProvider2, x, z);
    }

    public static void addEntities(Object chunkHandle) {
        addEntities.invoke(chunkHandle);
    }

    /**
     * Whether saving is needed for a chunk
     *
     * @param chunkHandle to check
     * @return True if the chunk needs saving, False if not
     */
    public static boolean needsSaving(Object chunkHandle) {
        return needsSaving.invoke(chunkHandle, false);
    }

    /**
     * Gets the y-coordinate of the highest chunk section
     *
     * @param chunkHandle to get it from
     * @return chunk section highest y-position
     */
    public static int getTopSectionY(Object chunkHandle) {
        return ((Chunk) chunkHandle).g();
    }

    public static int getHeight(Object chunkHandle, int x, int z) {
        return ((Chunk) chunkHandle).b(x & XZ_MASK, z & XZ_MASK);
    }

    public static int getBlockLight(Object chunkHandle, int x, int y, int z) {
        return getBrightness(((Chunk) chunkHandle), x, y, z, EnumSkyBlock.BLOCK);
    }

    public static int getSkyLight(Object chunkHandle, int x, int y, int z) {
        return getBrightness(((Chunk) chunkHandle), x, y, z, EnumSkyBlock.SKY);
    }

    private static int getBrightness(Object chunkHandle, int x, int y, int z, EnumSkyBlock mode) {
        if (y < 0) {
            return 0;
        } else if (y >= ((Chunk) chunkHandle).world.getWorld().getMaxHeight()) {
            return mode.c;
        }
        return ((Chunk) chunkHandle).getBrightness(mode, new BlockPosition(x & XZ_MASK, y, z & XZ_MASK));
    }

	public static boolean setBlockData(Object chunkHandle, int x, int y, int z, BlockData data) {
        return ((Chunk) chunkHandle).a(new BlockPosition(x & XZ_MASK, y, z & XZ_MASK), (IBlockData) data.getData()) != null;
    }

    public static BlockData getBlockData(Object chunkHandle, int x, int y, int z) {
        BlockPosition pos = new BlockPosition(x & XZ_MASK, y, z & XZ_MASK);
        return  BlockData.fromBlockData(((Chunk) chunkHandle).getBlockData(pos));
    }

    public static Object getType(Object chunkHandle, int x, int y, int z) {
        return ((Chunk) chunkHandle).world.getType(new BlockPosition(x & XZ_MASK, y, z & XZ_MASK));
    }

    @Deprecated
    public static int getTypeId(Object chunkHandle, int x, int y, int z) {
        return CraftMagicNumbers.getId((Block) getType(chunkHandle, x, y, z));
    }
}
