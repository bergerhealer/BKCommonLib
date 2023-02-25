package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInHeldItemSlot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInHeldItemSlot")
public abstract class PacketPlayInHeldItemSlotHandle extends PacketHandle {
    /** @see PacketPlayInHeldItemSlotClass */
    public static final PacketPlayInHeldItemSlotClass T = Template.Class.create(PacketPlayInHeldItemSlotClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInHeldItemSlotHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getItemInHandIndex();
    public abstract void setItemInHandIndex(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInHeldItemSlot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInHeldItemSlotClass extends Template.Class<PacketPlayInHeldItemSlotHandle> {
        public final Template.Field.Integer itemInHandIndex = new Template.Field.Integer();

    }

}

