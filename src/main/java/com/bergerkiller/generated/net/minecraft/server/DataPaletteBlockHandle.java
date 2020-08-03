package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DataPaletteBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.DataPaletteBlock")
public abstract class DataPaletteBlockHandle extends Template.Handle {
    /** @See {@link DataPaletteBlockClass} */
    public static final DataPaletteBlockClass T = Template.Class.create(DataPaletteBlockClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DataPaletteBlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockData getBlockData(int x, int y, int z);
    public abstract void setBlockData(int x, int y, int z, BlockData data);
    /**
     * Stores class members for <b>net.minecraft.server.DataPaletteBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataPaletteBlockClass extends Template.Class<DataPaletteBlockHandle> {
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> setBlockData = new Template.Method.Converted<Void>();

    }

}

