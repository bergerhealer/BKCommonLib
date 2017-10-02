package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutWindowItems</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutWindowItemsHandle extends PacketHandle {
    /** @See {@link PacketPlayOutWindowItemsClass} */
    public static final PacketPlayOutWindowItemsClass T = new PacketPlayOutWindowItemsClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutWindowItemsHandle.class, "net.minecraft.server.PacketPlayOutWindowItems");

    /* ============================================================================== */

    public static PacketPlayOutWindowItemsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public int getWindowId() {
        return T.windowId.getInteger(getRaw());
    }

    public void setWindowId(int value) {
        T.windowId.setInteger(getRaw(), value);
    }

    public List<ItemStack> getItems() {
        return T.items.get(getRaw());
    }

    public void setItems(List<ItemStack> value) {
        T.items.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutWindowItems</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutWindowItemsClass extends Template.Class<PacketPlayOutWindowItemsHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<List<ItemStack>> items = new Template.Field.Converted<List<ItemStack>>();

    }

}

