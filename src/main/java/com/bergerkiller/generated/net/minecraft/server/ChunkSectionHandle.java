package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ChunkSection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class ChunkSectionHandle extends Template.Handle {
    /** @See {@link ChunkSectionClass} */
    public static final ChunkSectionClass T = new ChunkSectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkSectionHandle.class, "net.minecraft.server.ChunkSection");

    /* ============================================================================== */

    public static ChunkSectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ChunkSectionHandle createNew(int y, boolean hasSkyLight) {
        return T.constr_y_hasSkyLight.newInstance(y, hasSkyLight);
    }

    /* ============================================================================== */

    public boolean isEmpty() {
        return T.isEmpty.invoke(getRaw());
    }

    public NibbleArrayHandle getBlockLightArray() {
        return T.getBlockLightArray.invoke(getRaw());
    }

    public NibbleArrayHandle getSkyLightArray() {
        return T.getSkyLightArray.invoke(getRaw());
    }

    public DataPaletteBlockHandle getBlockPalette() {
        return T.getBlockPalette.invoke(getRaw());
    }

    public BlockData getBlockData(int x, int y, int z) {
        return T.getBlockData.invoke(getRaw(), x, y, z);
    }

    public void setBlockData(int x, int y, int z, BlockData data) {
        T.setBlockData.invoke(getRaw(), x, y, z, data);
    }

    public void setSkyLight(int x, int y, int z, int level) {
        T.setSkyLight.invoke(getRaw(), x, y, z, level);
    }

    public void setBlockLight(int x, int y, int z, int level) {
        T.setBlockLight.invoke(getRaw(), x, y, z, level);
    }

    public int getSkyLight(int x, int y, int z) {
        return T.getSkyLight.invoke(getRaw(), x, y, z);
    }

    public int getBlockLight(int x, int y, int z) {
        return T.getBlockLight.invoke(getRaw(), x, y, z);
    }

    public NibbleArrayHandle getBlockLight() {
        return T.blockLight.get(getRaw());
    }

    public void setBlockLight(NibbleArrayHandle value) {
        T.blockLight.set(getRaw(), value);
    }

    public NibbleArrayHandle getSkyLight() {
        return T.skyLight.get(getRaw());
    }

    public void setSkyLight(NibbleArrayHandle value) {
        T.skyLight.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.ChunkSection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkSectionClass extends Template.Class<ChunkSectionHandle> {
        public final Template.Constructor.Converted<ChunkSectionHandle> constr_y_hasSkyLight = new Template.Constructor.Converted<ChunkSectionHandle>();

        public final Template.Field.Converted<NibbleArrayHandle> blockLight = new Template.Field.Converted<NibbleArrayHandle>();
        public final Template.Field.Converted<NibbleArrayHandle> skyLight = new Template.Field.Converted<NibbleArrayHandle>();

        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method.Converted<NibbleArrayHandle> getBlockLightArray = new Template.Method.Converted<NibbleArrayHandle>();
        public final Template.Method.Converted<NibbleArrayHandle> getSkyLightArray = new Template.Method.Converted<NibbleArrayHandle>();
        public final Template.Method.Converted<DataPaletteBlockHandle> getBlockPalette = new Template.Method.Converted<DataPaletteBlockHandle>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> setBlockData = new Template.Method.Converted<Void>();
        public final Template.Method<Void> setSkyLight = new Template.Method<Void>();
        public final Template.Method<Void> setBlockLight = new Template.Method<Void>();
        public final Template.Method<Integer> getSkyLight = new Template.Method<Integer>();
        public final Template.Method<Integer> getBlockLight = new Template.Method<Integer>();

    }

}

