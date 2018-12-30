package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ChunkSection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ChunkSectionHandle extends Template.Handle {
    /** @See {@link ChunkSectionClass} */
    public static final ChunkSectionClass T = new ChunkSectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkSectionHandle.class, "net.minecraft.server.ChunkSection", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static ChunkSectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ChunkSectionHandle createNew(int y, boolean hasSkyLight) {
        return T.constr_y_hasSkyLight.newInstance(y, hasSkyLight);
    }

    /* ============================================================================== */

    public abstract boolean isEmpty();
    public abstract int getYPosition();
    public abstract NibbleArrayHandle getBlockLightArray();
    public abstract NibbleArrayHandle getSkyLightArray();
    public abstract DataPaletteBlockHandle getBlockPalette();
    public abstract BlockData getBlockData(int x, int y, int z);
    public abstract void setBlockData(int x, int y, int z, BlockData data);
    public abstract void setSkyLight(int x, int y, int z, int level);
    public abstract void setBlockLight(int x, int y, int z, int level);
    public abstract int getSkyLight(int x, int y, int z);
    public abstract int getBlockLight(int x, int y, int z);
    public abstract NibbleArrayHandle getBlockLight();
    public abstract void setBlockLight(NibbleArrayHandle value);
    public abstract NibbleArrayHandle getSkyLight();
    public abstract void setSkyLight(NibbleArrayHandle value);
    /**
     * Stores class members for <b>net.minecraft.server.ChunkSection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkSectionClass extends Template.Class<ChunkSectionHandle> {
        public final Template.Constructor.Converted<ChunkSectionHandle> constr_y_hasSkyLight = new Template.Constructor.Converted<ChunkSectionHandle>();

        public final Template.Field.Converted<NibbleArrayHandle> blockLight = new Template.Field.Converted<NibbleArrayHandle>();
        public final Template.Field.Converted<NibbleArrayHandle> skyLight = new Template.Field.Converted<NibbleArrayHandle>();

        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method<Integer> getYPosition = new Template.Method<Integer>();
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

