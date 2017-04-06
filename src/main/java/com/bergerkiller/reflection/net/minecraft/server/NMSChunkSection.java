package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.ChunkSection;
import net.minecraft.server.v1_11_R1.NibbleArray;

public class NMSChunkSection {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ChunkSection");
    public static final FieldAccessor<Object> skyLight   = T.selectField("private NibbleArray skyLight");
    public static final FieldAccessor<Object> blockLight = T.selectField("private NibbleArray emittedLight");

    public static final MethodAccessor<Boolean> isEmpty  = T.selectMethod("public boolean a()");

    public static final MethodAccessor<Object> getBlockLightNibble = T.selectMethod("public NibbleArray getEmittedLightArray()");
    public static final MethodAccessor<Object> getSkyLightNibble   = T.selectMethod("public NibbleArray getSkyLightArray()");
    public static final MethodAccessor<Object> getBlockData        = T.selectMethod("public DataPaletteBlock getBlocks()");

    public static final MethodAccessor<Block> getTypeBlock = T.selectMethod("public IBlockData getType(int x, int y, int z)");
    private static final MethodAccessor<Void>  setTypeBlock = T.selectMethod("public void setType(int, int, int, IBlockData)");
    private static final MethodAccessor<Integer> getData   = T.selectMethod("public int c(int, int, int)");

    private static final MethodAccessor<Integer> getSkyLight   = T.selectMethod("public int b(int, int, int)");
    private static final MethodAccessor<Void> setSkyLight      = T.selectMethod("public void a(int, int, int, int)");
    private static final MethodAccessor<Integer> getBlockLight = T.selectMethod("public int c(int, int, int)");
    private static final MethodAccessor<Void> setBlockLight    = T.selectMethod("public void b(int, int, int, int)");

    /**
     * Converts the block data in a Chunk Section into raw data
     * 
     * @param section to read block data from
     * @return chunk section data
     */
    public static ChunkSectionBlockData exportBlockData(Object section) {
        ChunkSectionBlockData data = new ChunkSectionBlockData();
        exportBlockData(section, data);
        return data;
    }

    /**
     * Converts th block data in a Chunk Section into raw data
     * 
     * @param section to read block data from
     * @param data to write the data to
     */
    public static void exportBlockData(Object section, ChunkSectionBlockData data) {
        ((ChunkSection) section).getBlocks().exportData(data.blockIds, (NibbleArray) data.blockData);
    }

    public static class ChunkSectionBlockData {
        public final byte[] blockIds;
        public final Object blockData;

        public ChunkSectionBlockData() {
            this.blockIds = new byte[4096];
            this.blockData = new NibbleArray();
        }
    }

    //public static final MethodAccessor<IBlockData> getBlockIds = TEMPLATE.getMethod("getType");
    // No longer avaiable. Sry
//	public static final MethodAccessor<Object> getExtBlockIds = TEMPLATE.getMethod("getExtendedIdArray");
//	public static final MethodAccessor<Object> getBlockData = TEMPLATE.getMethod("getDataArray");
    // No longer avaiable
// private static final MethodAccessor<Void> setData = TEMPLATE.getMethod("setData", int.class, int.class, int.class, int.class);
    
    public static int getTypeId(Object section, int x, int y, int z) {
    	throw new RuntimeException("BROKEN!");
        //return NMSBlock.id.get(blocks.invoke(section, x & 0xf, y & 0xf, z & 0xf));
    }

    public static void setTypeId(Object section, int x, int y, int z, int typeId) {
        setTypeBlock.invoke(section, x & 0xf, y & 0xf, z & 0xf, CommonNMS.getBlock(typeId).getBlockData());
    }

    public static int getData(Object section, int x, int y, int z) {
        return getData.invoke(section, z & 0xf, y & 0xf, z & 0xf);
    }

    public static void setData(Object section, int x, int y, int z, int data) {
    	throw new RuntimeException("BROKEN!");
        //setTypeBlock.invoke(section, x & 0xf, y & 0xf, z & 0xf, blocks.invoke(section, x, y, z).fromLegacyData(data));
    }

    public static int getSkyLight(Object section, int x, int y, int z) {
        return getSkyLight.invoke(section, x & 0xf, y & 0xf, z & 0xf);
    }

    public static void setSkyLight(Object section, int x, int y, int z, int level) {
        setSkyLight.invoke(section, x & 0xf, y & 0xf, z & 0xf, level);
    }

    public static int getBlockLight(Object section, int x, int y, int z) {
        return getBlockLight.invoke(section, x & 0xf, y & 0xf, z & 0xf);
    }

    public static void setBlockLight(Object section, int x, int y, int z, int level) {
        setBlockLight.invoke(section, x & 0xf, y & 0xf, z & 0xf, level);
    }
}
