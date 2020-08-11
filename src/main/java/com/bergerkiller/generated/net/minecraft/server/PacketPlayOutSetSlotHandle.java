package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutSetSlot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayOutSetSlot")
public abstract class PacketPlayOutSetSlotHandle extends PacketHandle {
    /** @See {@link PacketPlayOutSetSlotClass} */
    public static final PacketPlayOutSetSlotClass T = Template.Class.create(PacketPlayOutSetSlotClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutSetSlotHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract int getSlot();
    public abstract void setSlot(int value);
    public abstract ItemStack getItem();
    public abstract void setItem(ItemStack value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutSetSlot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSetSlotClass extends Template.Class<PacketPlayOutSetSlotHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer slot = new Template.Field.Integer();
        public final Template.Field.Converted<ItemStack> item = new Template.Field.Converted<ItemStack>();

    }

}

