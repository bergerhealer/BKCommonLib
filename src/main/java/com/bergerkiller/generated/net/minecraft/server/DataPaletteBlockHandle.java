package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DataPaletteBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class DataPaletteBlockHandle extends Template.Handle {
    /** @See {@link DataPaletteBlockClass} */
    public static final DataPaletteBlockClass T = new DataPaletteBlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DataPaletteBlockHandle.class, "net.minecraft.server.DataPaletteBlock");

    /* ============================================================================== */

    public static DataPaletteBlockHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        DataPaletteBlockHandle handle = new DataPaletteBlockHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public NibbleArrayHandle exportData(byte[] abyte, NibbleArrayHandle nibblearray) {
        return T.exportData.invoke(instance, abyte, nibblearray);
    }

    public BlockData getBlockData(int x, int y, int z) {
        return T.getBlockData.invoke(instance, x, y, z);
    }

    public void setBlockData(int x, int y, int z, BlockData data) {
        T.setBlockData.invoke(instance, x, y, z, data);
    }

    /**
     * Stores class members for <b>net.minecraft.server.DataPaletteBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataPaletteBlockClass extends Template.Class<DataPaletteBlockHandle> {
        public final Template.Method.Converted<NibbleArrayHandle> exportData = new Template.Method.Converted<NibbleArrayHandle>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> setBlockData = new Template.Method.Converted<Void>();

    }

}

