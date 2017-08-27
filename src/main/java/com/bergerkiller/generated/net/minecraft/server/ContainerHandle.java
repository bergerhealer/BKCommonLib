package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Container</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class ContainerHandle extends Template.Handle {
    /** @See {@link ContainerClass} */
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

    public List<ItemStack> getOldItems() {
        return T.oldItems.get(instance);
    }

    public void setOldItems(List<ItemStack> value) {
        T.oldItems.set(instance, value);
    }

    public List<SlotHandle> getSlots() {
        return T.slots.get(instance);
    }

    public void setSlots(List<SlotHandle> value) {
        T.slots.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.Container</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ContainerClass extends Template.Class<ContainerHandle> {
        public final Template.Field.Converted<List<ItemStack>> oldItems = new Template.Field.Converted<List<ItemStack>>();
        public final Template.Field.Converted<List<SlotHandle>> slots = new Template.Field.Converted<List<SlotHandle>>();

        public final Template.Method<InventoryView> getBukkitView = new Template.Method<InventoryView>();

    }

}

