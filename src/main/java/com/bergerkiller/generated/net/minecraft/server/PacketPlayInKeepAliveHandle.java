package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInKeepAlive</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayInKeepAlive")
public abstract class PacketPlayInKeepAliveHandle extends PacketHandle {
    /** @See {@link PacketPlayInKeepAliveClass} */
    public static final PacketPlayInKeepAliveClass T = Template.Class.create(PacketPlayInKeepAliveClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInKeepAliveHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract long getKey();
    public abstract void setKey(long key);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInKeepAlive</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInKeepAliveClass extends Template.Class<PacketPlayInKeepAliveHandle> {
        public final Template.Method<Long> getKey = new Template.Method<Long>();
        public final Template.Method<Void> setKey = new Template.Method<Void>();

    }

}

