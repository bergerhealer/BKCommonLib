package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket")
public abstract class ClientboundSetHeldSlotPacketHandle extends PacketHandle {
    /** @see ClientboundSetHeldSlotPacketClass */
    public static final ClientboundSetHeldSlotPacketClass T = Template.Class.create(ClientboundSetHeldSlotPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetHeldSlotPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getItemInHandIndex();
    public abstract void setItemInHandIndex(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetHeldSlotPacketClass extends Template.Class<ClientboundSetHeldSlotPacketHandle> {
        public final Template.Field.Integer itemInHandIndex = new Template.Field.Integer();

    }

}

