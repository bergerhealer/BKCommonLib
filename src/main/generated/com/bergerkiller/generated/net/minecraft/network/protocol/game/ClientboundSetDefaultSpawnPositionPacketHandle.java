package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle.RespawnConfigHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket")
public abstract class ClientboundSetDefaultSpawnPositionPacketHandle extends PacketHandle {
    /** @see ClientboundSetDefaultSpawnPositionPacketClass */
    public static final ClientboundSetDefaultSpawnPositionPacketClass T = Template.Class.create(ClientboundSetDefaultSpawnPositionPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetDefaultSpawnPositionPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundSetDefaultSpawnPositionPacketHandle createNew(RespawnConfigHandle spawnConfig) {
        return T.createNew.invoke(spawnConfig);
    }

    public abstract RespawnConfigHandle getSpawn();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetDefaultSpawnPositionPacketClass extends Template.Class<ClientboundSetDefaultSpawnPositionPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundSetDefaultSpawnPositionPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundSetDefaultSpawnPositionPacketHandle>();

        public final Template.Method.Converted<RespawnConfigHandle> getSpawn = new Template.Method.Converted<RespawnConfigHandle>();

    }

}

