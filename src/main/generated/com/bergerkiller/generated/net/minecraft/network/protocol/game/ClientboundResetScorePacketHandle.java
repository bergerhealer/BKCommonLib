package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundResetScorePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundResetScorePacket")
public abstract class ClientboundResetScorePacketHandle extends PacketHandle {
    /** @see ClientboundResetScorePacketClass */
    public static final ClientboundResetScorePacketClass T = Template.Class.create(ClientboundResetScorePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundResetScorePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundResetScorePacketHandle createNew(String name, String objectiveName) {
        return T.createNew.invoke(name, objectiveName);
    }

    public abstract String getName();
    public abstract void setName(String value);
    public abstract String getObjName();
    public abstract void setObjName(String value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundResetScorePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundResetScorePacketClass extends Template.Class<ClientboundResetScorePacketHandle> {
        public final Template.Field<String> name = new Template.Field<String>();
        public final Template.Field<String> objName = new Template.Field<String>();

        public final Template.StaticMethod.Converted<ClientboundResetScorePacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundResetScorePacketHandle>();

    }

}

