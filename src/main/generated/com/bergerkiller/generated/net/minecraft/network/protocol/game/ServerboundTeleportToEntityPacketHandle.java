package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket")
public abstract class ServerboundTeleportToEntityPacketHandle extends PacketHandle {
    /** @see ServerboundTeleportToEntityPacketClass */
    public static final ServerboundTeleportToEntityPacketClass T = Template.Class.create(ServerboundTeleportToEntityPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundTeleportToEntityPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ServerboundTeleportToEntityPacketHandle createNew(UUID uuid) {
        return T.constr_uuid.newInstance(uuid);
    }

    /* ============================================================================== */

    public abstract UUID getUuid();
    public abstract void setUuid(UUID value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundTeleportToEntityPacketClass extends Template.Class<ServerboundTeleportToEntityPacketHandle> {
        public final Template.Constructor.Converted<ServerboundTeleportToEntityPacketHandle> constr_uuid = new Template.Constructor.Converted<ServerboundTeleportToEntityPacketHandle>();

        public final Template.Field<UUID> uuid = new Template.Field<UUID>();

    }

}

