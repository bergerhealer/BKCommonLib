package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Optional;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket")
public abstract class ClientboundResourcePackPopPacketHandle extends Template.Handle {
    /** @see ClientboundResourcePackPopPacketClass */
    public static final ClientboundResourcePackPopPacketClass T = Template.Class.create(ClientboundResourcePackPopPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundResourcePackPopPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Optional<UUID> getId();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundResourcePackPopPacketClass extends Template.Class<ClientboundResourcePackPopPacketHandle> {
        public final Template.Method<Optional<UUID>> getId = new Template.Method<Optional<UUID>>();

    }

}

