package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket")
public abstract class ClientboundSwingAnimationPacketHandle extends PacketHandle {
    /** @see ClientboundSwingAnimationPacketClass */
    public static final ClientboundSwingAnimationPacketClass T = Template.Class.create(ClientboundSwingAnimationPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSwingAnimationPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundSwingAnimationPacketHandle createNewWhack(int entityId, HumanHandRole hand) {
        return T.createNewWhack.invoke(entityId, hand);
    }

    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSwingAnimationPacketClass extends Template.Class<ClientboundSwingAnimationPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundSwingAnimationPacketHandle> createNewWhack = new Template.StaticMethod.Converted<ClientboundSwingAnimationPacketHandle>();

    }

}

