package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket")
public abstract class ClientboundSetDisplayObjectivePacketHandle extends PacketHandle {
    /** @see ClientboundSetDisplayObjectivePacketClass */
    public static final ClientboundSetDisplayObjectivePacketClass T = Template.Class.create(ClientboundSetDisplayObjectivePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetDisplayObjectivePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract DisplaySlot getDisplay();
    public abstract void setDisplay(DisplaySlot value);
    public abstract String getName();
    public abstract void setName(String value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetDisplayObjectivePacketClass extends Template.Class<ClientboundSetDisplayObjectivePacketHandle> {
        public final Template.Field.Converted<DisplaySlot> display = new Template.Field.Converted<DisplaySlot>();
        public final Template.Field<String> name = new Template.Field<String>();

    }

}

