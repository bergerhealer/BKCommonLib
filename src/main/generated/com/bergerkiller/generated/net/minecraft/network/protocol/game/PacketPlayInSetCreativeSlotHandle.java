package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInSetCreativeSlot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInSetCreativeSlot")
public abstract class PacketPlayInSetCreativeSlotHandle extends PacketHandle {
    /** @see PacketPlayInSetCreativeSlotClass */
    public static final PacketPlayInSetCreativeSlotClass T = Template.Class.create(PacketPlayInSetCreativeSlotClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInSetCreativeSlotHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getSlot();
    public abstract void setSlot(int value);
    public abstract ItemStack getItem();
    public abstract void setItem(ItemStack value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInSetCreativeSlot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInSetCreativeSlotClass extends Template.Class<PacketPlayInSetCreativeSlotHandle> {
        public final Template.Field.Converted<Integer> slot = new Template.Field.Converted<Integer>();
        public final Template.Field.Converted<ItemStack> item = new Template.Field.Converted<ItemStack>();

    }

}

