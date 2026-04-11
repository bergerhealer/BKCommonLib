package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundAttackPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundAttackPacket")
public abstract class ServerboundAttackPacketHandle extends PacketHandle {
    /** @see ServerboundAttackPacketClass */
    public static final ServerboundAttackPacketClass T = Template.Class.create(ServerboundAttackPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundAttackPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ServerboundAttackPacketHandle createNew(int attackedEntityId) {
        return T.createNew.invoke(attackedEntityId);
    }

    public static boolean isAttackInteractionPacket(Object interactPacket) {
        return T.isAttackInteractionPacket.invoke(interactPacket);
    }

    public abstract int getEntityId();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundAttackPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundAttackPacketClass extends Template.Class<ServerboundAttackPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundAttackPacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundAttackPacketHandle>();
        public final Template.StaticMethod.Converted<Boolean> isAttackInteractionPacket = new Template.StaticMethod.Converted<Boolean>();

        public final Template.Method<Integer> getEntityId = new Template.Method<Integer>();

    }

}

