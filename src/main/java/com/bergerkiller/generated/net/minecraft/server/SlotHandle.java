package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Slot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class SlotHandle extends Template.Handle {
    /** @See {@link SlotClass} */
    public static final SlotClass T = new SlotClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SlotHandle.class, "net.minecraft.server.Slot");

    /* ============================================================================== */

    public static SlotHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        SlotHandle handle = new SlotHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public ItemStack getItem() {
        return T.getItem.invoke(instance);
    }

    /**
     * Stores class members for <b>net.minecraft.server.Slot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SlotClass extends Template.Class<SlotHandle> {
        public final Template.Method.Converted<ItemStack> getItem = new Template.Method.Converted<ItemStack>();

    }

}

