package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class CraftBlockStateHandle extends Template.Handle {
    public static final CraftBlockStateClass T = new CraftBlockStateClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftBlockStateHandle.class, "org.bukkit.craftbukkit.block.CraftBlockState");


    /* ============================================================================== */

    public static CraftBlockStateHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftBlockStateHandle handle = new CraftBlockStateHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final BlockState createNew(Block block) {
        return T.constr_block.newInstance(block);
    }

    /* ============================================================================== */

    public static final class CraftBlockStateClass extends Template.Class<CraftBlockStateHandle> {
        public final Template.Constructor.Converted<BlockState> constr_block = new Template.Constructor.Converted<BlockState>();

    }
}
