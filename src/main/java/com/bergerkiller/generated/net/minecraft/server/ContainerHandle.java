package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.inventory.InventoryView;

public class ContainerHandle extends Template.Handle {
    public static final ContainerClass T = new ContainerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ContainerHandle.class, "net.minecraft.server.Container");


    /* ============================================================================== */

    public static ContainerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ContainerHandle handle = new ContainerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public InventoryView getBukkitView() {
        return T.getBukkitView.invoke(instance);
    }

    public static final class ContainerClass extends Template.Class<ContainerHandle> {
        public final Template.Method<InventoryView> getBukkitView = new Template.Method<InventoryView>();

    }
}
