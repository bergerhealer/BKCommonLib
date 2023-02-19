package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.List;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket")
public abstract class ClientboundPlayerInfoRemovePacketHandle extends PacketHandle {
    /** @See {@link ClientboundPlayerInfoRemovePacketClass} */
    public static final ClientboundPlayerInfoRemovePacketClass T = Template.Class.create(ClientboundPlayerInfoRemovePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundPlayerInfoRemovePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundPlayerInfoRemovePacketHandle createNew(List<UUID> profileIds) {
        return T.createNew.invoke(profileIds);
    }

    public abstract List<UUID> getProfileIds();
    public abstract void setProfileIds(List<UUID> uuids);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundPlayerInfoRemovePacketClass extends Template.Class<ClientboundPlayerInfoRemovePacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundPlayerInfoRemovePacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundPlayerInfoRemovePacketHandle>();

        public final Template.Method<List<UUID>> getProfileIds = new Template.Method<List<UUID>>();
        public final Template.Method<Void> setProfileIds = new Template.Method<Void>();

    }

}

