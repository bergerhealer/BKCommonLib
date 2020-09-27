package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.block.Block;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ChunkSection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.ChunkSection")
public abstract class ChunkSectionHandle extends Template.Handle {
    /** @See {@link ChunkSectionClass} */
    public static final ChunkSectionClass T = Template.Class.create(ChunkSectionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChunkSectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isEmpty();
    public abstract int getYPosition();
    public abstract DataPaletteBlockHandle getBlockPalette();
    public abstract BlockData getBlockData(int x, int y, int z);
    public abstract void setBlockData(int x, int y, int z, BlockData data);
    public abstract void setBlockDataAtBlock(Block block, BlockData data);
    /**
     * Stores class members for <b>net.minecraft.server.ChunkSection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkSectionClass extends Template.Class<ChunkSectionHandle> {
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method<Integer> getYPosition = new Template.Method<Integer>();
        public final Template.Method.Converted<DataPaletteBlockHandle> getBlockPalette = new Template.Method.Converted<DataPaletteBlockHandle>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> setBlockData = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> setBlockDataAtBlock = new Template.Method.Converted<Void>();

    }

}

