package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInSpectate</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInSpectate")
public abstract class PacketPlayInSpectateHandle extends PacketHandle {
    /** @see PacketPlayInSpectateClass */
    public static final PacketPlayInSpectateClass T = Template.Class.create(PacketPlayInSpectateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInSpectateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayInSpectateHandle createNew(UUID uuid) {
        return T.constr_uuid.newInstance(uuid);
    }

    /* ============================================================================== */

    public abstract UUID getUuid();
    public abstract void setUuid(UUID value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInSpectate</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInSpectateClass extends Template.Class<PacketPlayInSpectateHandle> {
        public final Template.Constructor.Converted<PacketPlayInSpectateHandle> constr_uuid = new Template.Constructor.Converted<PacketPlayInSpectateHandle>();

        public final Template.Field<UUID> uuid = new Template.Field<UUID>();

    }

}

