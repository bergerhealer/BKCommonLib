package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInBoatMove</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInBoatMove")
public abstract class PacketPlayInBoatMoveHandle extends Template.Handle {
    /** @See {@link PacketPlayInBoatMoveClass} */
    public static final PacketPlayInBoatMoveClass T = Template.Class.create(PacketPlayInBoatMoveClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInBoatMoveHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isRightPaddle();
    public abstract void setRightPaddle(boolean value);
    public abstract boolean isLeftPaddle();
    public abstract void setLeftPaddle(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInBoatMove</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInBoatMoveClass extends Template.Class<PacketPlayInBoatMoveHandle> {
        public final Template.Field.Boolean rightPaddle = new Template.Field.Boolean();
        public final Template.Field.Boolean leftPaddle = new Template.Field.Boolean();

    }

}

