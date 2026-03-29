package com.bergerkiller.generated.net.minecraft.world.level.chunk;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.block.Block;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.chunk.LevelChunkSection</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.chunk.LevelChunkSection")
public abstract class LevelChunkSectionHandle extends Template.Handle {
    /** @see LevelChunkSectionClass */
    public static final LevelChunkSectionClass T = Template.Class.create(LevelChunkSectionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static LevelChunkSectionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isEmpty();
    public abstract PalettedContainerHandle getBlockPalette();
    public abstract BlockData getBlockData(int x, int y, int z);
    public abstract void setBlockData(int x, int y, int z, BlockData data);
    public abstract void setBlockDataAtBlock(Block block, BlockData data);
    /**
     * Stores class members for <b>net.minecraft.world.level.chunk.LevelChunkSection</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LevelChunkSectionClass extends Template.Class<LevelChunkSectionHandle> {
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method.Converted<PalettedContainerHandle> getBlockPalette = new Template.Method.Converted<PalettedContainerHandle>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> setBlockData = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> setBlockDataAtBlock = new Template.Method.Converted<Void>();

    }

}

