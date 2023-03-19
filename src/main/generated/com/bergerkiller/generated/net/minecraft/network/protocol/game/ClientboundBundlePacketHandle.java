package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundBundlePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundBundlePacket")
public abstract class ClientboundBundlePacketHandle extends PacketHandle {
    /** @see ClientboundBundlePacketClass */
    public static final ClientboundBundlePacketClass T = Template.Class.create(ClientboundBundlePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundBundlePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundBundlePacketHandle createNew(Iterable<Object> rawPackets) {
        return T.createNew.invoke(rawPackets);
    }

    public abstract Iterable<Object> subPackets();
    public abstract void setSubPackets(Iterable<Object> packets);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundBundlePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundBundlePacketClass extends Template.Class<ClientboundBundlePacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundBundlePacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundBundlePacketHandle>();

        public final Template.Method.Converted<Iterable<Object>> subPackets = new Template.Method.Converted<Iterable<Object>>();
        public final Template.Method<Void> setSubPackets = new Template.Method<Void>();

    }

}

