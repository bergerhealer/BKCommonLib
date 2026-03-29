package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundCooldownPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundCooldownPacket")
public abstract class ClientboundCooldownPacketHandle extends PacketHandle {
    /** @see ClientboundCooldownPacketClass */
    public static final ClientboundCooldownPacketClass T = Template.Class.create(ClientboundCooldownPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundCooldownPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getCooldown();
    public abstract void setCooldown(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundCooldownPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundCooldownPacketClass extends Template.Class<ClientboundCooldownPacketHandle> {
        public final Template.Field.Integer cooldown = new Template.Field.Integer();

    }

}

