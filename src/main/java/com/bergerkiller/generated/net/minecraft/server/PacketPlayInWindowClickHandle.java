package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.InventoryClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInWindowClick</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInWindowClickHandle extends PacketHandle {
    /** @See {@link PacketPlayInWindowClickClass} */
    public static final PacketPlayInWindowClickClass T = new PacketPlayInWindowClickClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInWindowClickHandle.class, "net.minecraft.server.PacketPlayInWindowClick");

    /* ============================================================================== */

    public static PacketPlayInWindowClickHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public int getWindowId() {
        return T.windowId.getInteger(getRaw());
    }

    public void setWindowId(int value) {
        T.windowId.setInteger(getRaw(), value);
    }

    public int getSlot() {
        return T.slot.getInteger(getRaw());
    }

    public void setSlot(int value) {
        T.slot.setInteger(getRaw(), value);
    }

    public int getButton() {
        return T.button.getInteger(getRaw());
    }

    public void setButton(int value) {
        T.button.setInteger(getRaw(), value);
    }

    public short getAction() {
        return T.action.getShort(getRaw());
    }

    public void setAction(short value) {
        T.action.setShort(getRaw(), value);
    }

    public ItemStack getItem() {
        return T.item.get(getRaw());
    }

    public void setItem(ItemStack value) {
        T.item.set(getRaw(), value);
    }

    public InventoryClickType getMode() {
        return T.mode.get(getRaw());
    }

    public void setMode(InventoryClickType value) {
        T.mode.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInWindowClick</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInWindowClickClass extends Template.Class<PacketPlayInWindowClickHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer slot = new Template.Field.Integer();
        public final Template.Field.Integer button = new Template.Field.Integer();
        public final Template.Field.Short action = new Template.Field.Short();
        public final Template.Field.Converted<ItemStack> item = new Template.Field.Converted<ItemStack>();
        public final Template.Field.Converted<InventoryClickType> mode = new Template.Field.Converted<InventoryClickType>();

    }

}

