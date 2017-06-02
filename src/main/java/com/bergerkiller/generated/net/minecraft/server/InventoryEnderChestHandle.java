package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class InventoryEnderChestHandle extends IInventoryHandle {
    public static final InventoryEnderChestClass T = new InventoryEnderChestClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(InventoryEnderChestHandle.class, "net.minecraft.server.InventoryEnderChest");

    /* ============================================================================== */

    public static InventoryEnderChestHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        InventoryEnderChestHandle handle = new InventoryEnderChestHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void loadFromNBT(CommonTagList nbttaglist) {
        T.loadFromNBT.invoke(instance, nbttaglist);
    }

    public CommonTagList saveToNBT() {
        return T.saveToNBT.invoke(instance);
    }

    public static final class InventoryEnderChestClass extends Template.Class<InventoryEnderChestHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted<CommonTagList>();

    }

}

