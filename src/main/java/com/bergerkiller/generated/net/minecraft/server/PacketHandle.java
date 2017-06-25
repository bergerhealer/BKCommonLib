package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Packet</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketHandle extends Template.Handle {
    /** @See {@link PacketClass} */
    public static final PacketClass T = new PacketClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketHandle.class, "net.minecraft.server.Packet");

    /* ============================================================================== */

    public static PacketHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketHandle handle = new PacketHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


    public com.bergerkiller.bukkit.common.protocol.CommonPacket toCommonPacket() {
        return new com.bergerkiller.bukkit.common.protocol.CommonPacket(instance);
    }
    /**
     * Stores class members for <b>net.minecraft.server.Packet</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketClass extends Template.Class<PacketHandle> {
    }

}

