package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutWindowItems</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayOutWindowItems")
public abstract class PacketPlayOutWindowItemsHandle extends PacketHandle {
    /** @See {@link PacketPlayOutWindowItemsClass} */
    public static final PacketPlayOutWindowItemsClass T = Template.Class.create(PacketPlayOutWindowItemsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutWindowItemsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract List<ItemStack> getItems();
    public abstract void setItems(List<ItemStack> value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutWindowItems</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutWindowItemsClass extends Template.Class<PacketPlayOutWindowItemsHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<List<ItemStack>> items = new Template.Field.Converted<List<ItemStack>>();

    }

}

