package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInSetCreativeSlot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInSetCreativeSlotHandle extends PacketHandle {
    /** @See {@link PacketPlayInSetCreativeSlotClass} */
    public static final PacketPlayInSetCreativeSlotClass T = new PacketPlayInSetCreativeSlotClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInSetCreativeSlotHandle.class, "net.minecraft.server.PacketPlayInSetCreativeSlot");

    /* ============================================================================== */

    public static PacketPlayInSetCreativeSlotHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayInSetCreativeSlotHandle handle = new PacketPlayInSetCreativeSlotHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getSlot() {
        return T.slot.getInteger(instance);
    }

    public void setSlot(int value) {
        T.slot.setInteger(instance, value);
    }

    public ItemStack getItem() {
        return T.item.get(instance);
    }

    public void setItem(ItemStack value) {
        T.item.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInSetCreativeSlot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInSetCreativeSlotClass extends Template.Class<PacketPlayInSetCreativeSlotHandle> {
        public final Template.Field.Integer slot = new Template.Field.Integer();
        public final Template.Field.Converted<ItemStack> item = new Template.Field.Converted<ItemStack>();

    }

}

