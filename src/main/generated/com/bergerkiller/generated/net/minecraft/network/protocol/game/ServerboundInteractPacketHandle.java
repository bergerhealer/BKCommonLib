package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundInteractPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundInteractPacket")
public abstract class ServerboundInteractPacketHandle extends PacketHandle {
    /** @see ServerboundInteractPacketClass */
    public static final ServerboundInteractPacketClass T = Template.Class.create(ServerboundInteractPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundInteractPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ServerboundInteractPacketHandle createNew(int usedEntityId, HumanHandRole handRole, boolean isUsingSecondaryAction, Vector atPosition) {
        return T.createNew.invoke(usedEntityId, handRole, isUsingSecondaryAction, atPosition);
    }

    public static boolean hasSecondaryActionField() {
        return T.hasSecondaryActionField.invoker.invoke(null);
    }

    public abstract int getUsedEntityId();
    public abstract HumanHandRole getHandRole();
    public abstract boolean hasInteractAtPosition();
    public abstract Vector getInteractAtPosition();
    public abstract boolean isUsingSecondaryAction();
    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return getHandRole().getHandOf(humanEntity);
    }

    public static ServerboundInteractPacketHandle createNew(int usedEntityId, com.bergerkiller.bukkit.common.wrappers.HumanHandRole handRole, org.bukkit.util.Vector atPosition) {
        return createNew(usedEntityId, handRole, false, atPosition);
    }

    public static ServerboundInteractPacketHandle withUsingSecondaryAction(ServerboundInteractPacketHandle packet, boolean isUsingSecondaryAction) {
        return createNew(packet.getUsedEntityId(), packet.getHandRole(), isUsingSecondaryAction, packet.getInteractAtPosition());
    }

    public static ServerboundInteractPacketHandle withUsedEntityId(ServerboundInteractPacketHandle packet, int usedEntityId) {
        return createNew(usedEntityId, packet.getHandRole(), packet.isUsingSecondaryAction(), packet.getInteractAtPosition());
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundInteractPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundInteractPacketClass extends Template.Class<ServerboundInteractPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundInteractPacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundInteractPacketHandle>();
        public final Template.StaticMethod<Boolean> hasSecondaryActionField = new Template.StaticMethod<Boolean>();

        public final Template.Method<Integer> getUsedEntityId = new Template.Method<Integer>();
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted<HumanHandRole>();
        public final Template.Method<Boolean> hasInteractAtPosition = new Template.Method<Boolean>();
        public final Template.Method.Converted<Vector> getInteractAtPosition = new Template.Method.Converted<Vector>();
        public final Template.Method<Boolean> isUsingSecondaryAction = new Template.Method<Boolean>();

    }

}

