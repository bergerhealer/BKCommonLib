package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutHeldItemSlot</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutHeldItemSlot")
public abstract class PacketPlayOutHeldItemSlotHandle extends PacketHandle {
    /** @see PacketPlayOutHeldItemSlotClass */
    public static final PacketPlayOutHeldItemSlotClass T = Template.Class.create(PacketPlayOutHeldItemSlotClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutHeldItemSlotHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getItemInHandIndex();
    public abstract void setItemInHandIndex(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutHeldItemSlot</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutHeldItemSlotClass extends Template.Class<PacketPlayOutHeldItemSlotHandle> {
        public final Template.Field.Integer itemInHandIndex = new Template.Field.Integer();

    }

}

