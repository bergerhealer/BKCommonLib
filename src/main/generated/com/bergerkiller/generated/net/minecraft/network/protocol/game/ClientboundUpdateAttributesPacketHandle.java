package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeInstanceHandle;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket")
public abstract class ClientboundUpdateAttributesPacketHandle extends PacketHandle {
    /** @see ClientboundUpdateAttributesPacketClass */
    public static final ClientboundUpdateAttributesPacketClass T = Template.Class.create(ClientboundUpdateAttributesPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundUpdateAttributesPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundUpdateAttributesPacketHandle createNew(int entityId, Collection<AttributeInstanceHandle> attributes) {
        return T.createNew.invoke(entityId, attributes);
    }

    public static ClientboundUpdateAttributesPacketHandle createZeroMaxHealth(int entityId) {
        return T.createZeroMaxHealth.invoke(entityId);
    }

    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundUpdateAttributesPacketClass extends Template.Class<ClientboundUpdateAttributesPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<ClientboundUpdateAttributesPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundUpdateAttributesPacketHandle>();
        public final Template.StaticMethod.Converted<ClientboundUpdateAttributesPacketHandle> createZeroMaxHealth = new Template.StaticMethod.Converted<ClientboundUpdateAttributesPacketHandle>();

    }

}

