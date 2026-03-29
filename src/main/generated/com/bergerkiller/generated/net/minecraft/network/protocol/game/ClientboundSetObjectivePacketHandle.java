package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetObjectivePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket")
public abstract class ClientboundSetObjectivePacketHandle extends PacketHandle {
    /** @see ClientboundSetObjectivePacketClass */
    public static final ClientboundSetObjectivePacketClass T = Template.Class.create(ClientboundSetObjectivePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetObjectivePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getName();
    public abstract void setName(String value);
    public abstract ChatText getDisplayName();
    public abstract void setDisplayName(ChatText value);
    public abstract Object getCriteria();
    public abstract void setCriteria(Object value);
    public abstract int getAction();
    public abstract void setAction(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetObjectivePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetObjectivePacketClass extends Template.Class<ClientboundSetObjectivePacketHandle> {
        public final Template.Field<String> name = new Template.Field<String>();
        public final Template.Field.Converted<ChatText> displayName = new Template.Field.Converted<ChatText>();
        public final Template.Field.Converted<Object> criteria = new Template.Field.Converted<Object>();
        public final Template.Field.Integer action = new Template.Field.Integer();

    }

}

