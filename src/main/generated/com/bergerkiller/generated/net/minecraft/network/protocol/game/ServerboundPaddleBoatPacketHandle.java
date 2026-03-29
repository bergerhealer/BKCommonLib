package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket")
public abstract class ServerboundPaddleBoatPacketHandle extends Template.Handle {
    /** @see ServerboundPaddleBoatPacketClass */
    public static final ServerboundPaddleBoatPacketClass T = Template.Class.create(ServerboundPaddleBoatPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundPaddleBoatPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isLeftPaddle();
    public abstract void setLeftPaddle(boolean value);
    public abstract boolean isRightPaddle();
    public abstract void setRightPaddle(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundPaddleBoatPacketClass extends Template.Class<ServerboundPaddleBoatPacketHandle> {
        public final Template.Field.Boolean leftPaddle = new Template.Field.Boolean();
        public final Template.Field.Boolean rightPaddle = new Template.Field.Boolean();

    }

}

