package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket")
public abstract class ServerboundSetCarriedItemPacketHandle extends PacketHandle {
    /** @see ServerboundSetCarriedItemPacketClass */
    public static final ServerboundSetCarriedItemPacketClass T = Template.Class.create(ServerboundSetCarriedItemPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundSetCarriedItemPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getItemInHandIndex();
    public abstract void setItemInHandIndex(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundSetCarriedItemPacketClass extends Template.Class<ServerboundSetCarriedItemPacketHandle> {
        public final Template.Field.Integer itemInHandIndex = new Template.Field.Integer();

    }

}

