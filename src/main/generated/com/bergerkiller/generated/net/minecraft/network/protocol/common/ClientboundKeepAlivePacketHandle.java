package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.common.ClientboundKeepAlivePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.common.ClientboundKeepAlivePacket")
public abstract class ClientboundKeepAlivePacketHandle extends Template.Handle {
    /** @see ClientboundKeepAlivePacketClass */
    public static final ClientboundKeepAlivePacketClass T = Template.Class.create(ClientboundKeepAlivePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundKeepAlivePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundKeepAlivePacketHandle createNew(long key) {
        return T.createNew.invoke(key);
    }

    public abstract long getKey();
    public abstract void setKey(long key);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.common.ClientboundKeepAlivePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundKeepAlivePacketClass extends Template.Class<ClientboundKeepAlivePacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundKeepAlivePacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundKeepAlivePacketHandle>();

        public final Template.Method<Long> getKey = new Template.Method<Long>();
        public final Template.Method<Void> setKey = new Template.Method<Void>();

    }

}

