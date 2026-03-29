package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket")
public abstract class ClientboundUpdateAdvancementsPacketHandle extends PacketHandle {
    /** @see ClientboundUpdateAdvancementsPacketClass */
    public static final ClientboundUpdateAdvancementsPacketClass T = Template.Class.create(ClientboundUpdateAdvancementsPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundUpdateAdvancementsPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isInitial();
    public abstract void setInitial(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundUpdateAdvancementsPacketClass extends Template.Class<ClientboundUpdateAdvancementsPacketHandle> {
        public final Template.Field.Boolean initial = new Template.Field.Boolean();

    }

}

