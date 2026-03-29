package com.bergerkiller.generated.net.minecraft.world.level.chunk;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.chunk.PalettedContainer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.chunk.PalettedContainer")
public abstract class PalettedContainerHandle extends Template.Handle {
    /** @see PalettedContainerClass */
    public static final PalettedContainerClass T = Template.Class.create(PalettedContainerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PalettedContainerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockData getBlockData(int x, int y, int z);
    public abstract void setBlockData(int x, int y, int z, BlockData data);
    /**
     * Stores class members for <b>net.minecraft.world.level.chunk.PalettedContainer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PalettedContainerClass extends Template.Class<PalettedContainerHandle> {
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> setBlockData = new Template.Method.Converted<Void>();

    }

}

