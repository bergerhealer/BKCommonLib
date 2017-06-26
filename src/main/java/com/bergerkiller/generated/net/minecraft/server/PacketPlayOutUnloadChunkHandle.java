package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutUnloadChunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class PacketPlayOutUnloadChunkHandle extends PacketHandle {
    /** @See {@link PacketPlayOutUnloadChunkClass} */
    public static final PacketPlayOutUnloadChunkClass T = new PacketPlayOutUnloadChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutUnloadChunkHandle.class, "net.minecraft.server.PacketPlayOutUnloadChunk");

    /* ============================================================================== */

    public static PacketPlayOutUnloadChunkHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutUnloadChunkHandle handle = new PacketPlayOutUnloadChunkHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getCx() {
        return T.cx.getInteger(instance);
    }

    public void setCx(int value) {
        T.cx.setInteger(instance, value);
    }

    public int getCz() {
        return T.cz.getInteger(instance);
    }

    public void setCz(int value) {
        T.cz.setInteger(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutUnloadChunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutUnloadChunkClass extends Template.Class<PacketPlayOutUnloadChunkHandle> {
        public final Template.Field.Integer cx = new Template.Field.Integer();
        public final Template.Field.Integer cz = new Template.Field.Integer();

    }

}

