package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket")
public abstract class ClientboundTakeItemEntityPacketHandle extends PacketHandle {
    /** @see ClientboundTakeItemEntityPacketClass */
    public static final ClientboundTakeItemEntityPacketClass T = Template.Class.create(ClientboundTakeItemEntityPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundTakeItemEntityPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getCollectedItemId();
    public abstract void setCollectedItemId(int value);
    public abstract int getCollectorEntityId();
    public abstract void setCollectorEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundTakeItemEntityPacketClass extends Template.Class<ClientboundTakeItemEntityPacketHandle> {
        public final Template.Field.Integer collectedItemId = new Template.Field.Integer();
        public final Template.Field.Integer collectorEntityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer amount = new Template.Field.Integer();

    }

}

