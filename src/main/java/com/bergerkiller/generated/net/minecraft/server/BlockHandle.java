package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

public class BlockHandle extends Template.Handle {
    public static final BlockClass T = new BlockClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockHandle.class, "net.minecraft.server.Block");


    /* ============================================================================== */

    public static BlockHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BlockHandle handle = new BlockHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void entityHitVertical(WorldHandle world, EntityHandle entity) {
        T.entityHitVertical.invoke(instance, world, entity);
    }

    public BlockData getBlockData() {
        return T.getBlockData.invoke(instance);
    }

    public static final class BlockClass extends Template.Class<BlockHandle> {
        public final Template.Method.Converted<Void> entityHitVertical = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();

    }
}
