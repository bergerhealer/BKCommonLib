package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.common.ServerboundKeepAlivePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.common.ServerboundKeepAlivePacket")
public abstract class ServerboundKeepAlivePacketHandle extends Template.Handle {
    /** @see ServerboundKeepAlivePacketClass */
    public static final ServerboundKeepAlivePacketClass T = Template.Class.create(ServerboundKeepAlivePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundKeepAlivePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract long getKey();
    public abstract void setKey(long key);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.common.ServerboundKeepAlivePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundKeepAlivePacketClass extends Template.Class<ServerboundKeepAlivePacketHandle> {
        public final Template.Method<Long> getKey = new Template.Method<Long>();
        public final Template.Method<Void> setKey = new Template.Method<Void>();

    }

}

