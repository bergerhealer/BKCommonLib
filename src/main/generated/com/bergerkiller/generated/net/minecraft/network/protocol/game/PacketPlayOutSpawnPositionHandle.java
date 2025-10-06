package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle.RespawnConfigHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition")
public abstract class PacketPlayOutSpawnPositionHandle extends PacketHandle {
    /** @see PacketPlayOutSpawnPositionClass */
    public static final PacketPlayOutSpawnPositionClass T = Template.Class.create(PacketPlayOutSpawnPositionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutSpawnPositionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutSpawnPositionHandle createNew(RespawnConfigHandle spawnConfig) {
        return T.createNew.invoke(spawnConfig);
    }

    public abstract RespawnConfigHandle getSpawn();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSpawnPositionClass extends Template.Class<PacketPlayOutSpawnPositionHandle> {
        public final Template.StaticMethod.Converted<PacketPlayOutSpawnPositionHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutSpawnPositionHandle>();

        public final Template.Method.Converted<RespawnConfigHandle> getSpawn = new Template.Method.Converted<RespawnConfigHandle>();

    }

}

