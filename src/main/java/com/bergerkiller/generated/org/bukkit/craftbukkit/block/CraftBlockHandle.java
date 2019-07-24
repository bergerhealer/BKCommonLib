package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.block.Block;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.block.CraftBlock</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftBlockHandle extends Template.Handle {
    /** @See {@link CraftBlockClass} */
    public static final CraftBlockClass T = new CraftBlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftBlockHandle.class, "org.bukkit.craftbukkit.block.CraftBlock", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static CraftBlockHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getBlockPosition(Block block) {
        return T.getBlockPosition.invoke(block);
    }

    public static Block createBlockAtTileEntity(Object nmsTileEntity) {
        return T.createBlockAtTileEntity.invoke(nmsTileEntity);
    }

    public abstract BlockData getBlockData();
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.block.CraftBlock</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftBlockClass extends Template.Class<CraftBlockHandle> {
        public final Template.StaticMethod<Object> getBlockPosition = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<Block> createBlockAtTileEntity = new Template.StaticMethod<Block>();

        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();

    }

}

