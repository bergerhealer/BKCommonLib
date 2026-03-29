package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundContainerClosePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundContainerClosePacket")
public abstract class ServerboundContainerClosePacketHandle extends PacketHandle {
    /** @see ServerboundContainerClosePacketClass */
    public static final ServerboundContainerClosePacketClass T = Template.Class.create(ServerboundContainerClosePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundContainerClosePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundContainerClosePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundContainerClosePacketClass extends Template.Class<ServerboundContainerClosePacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();

    }

}

