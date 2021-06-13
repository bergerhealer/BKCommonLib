package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk")
public abstract class PacketPlayOutUnloadChunkHandle extends PacketHandle {
    /** @See {@link PacketPlayOutUnloadChunkClass} */
    public static final PacketPlayOutUnloadChunkClass T = Template.Class.create(PacketPlayOutUnloadChunkClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutUnloadChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getCx();
    public abstract void setCx(int value);
    public abstract int getCz();
    public abstract void setCz(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutUnloadChunkClass extends Template.Class<PacketPlayOutUnloadChunkHandle> {
        public final Template.Field.Integer cx = new Template.Field.Integer();
        public final Template.Field.Integer cz = new Template.Field.Integer();

    }

}

