package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket")
public abstract class ServerboundContainerButtonClickPacketHandle extends PacketHandle {
    /** @see ServerboundContainerButtonClickPacketClass */
    public static final ServerboundContainerButtonClickPacketClass T = Template.Class.create(ServerboundContainerButtonClickPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundContainerButtonClickPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract int getButtonId();
    public abstract void setButtonId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundContainerButtonClickPacketClass extends Template.Class<ServerboundContainerButtonClickPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Integer buttonId = new Template.Field.Integer();

    }

}

