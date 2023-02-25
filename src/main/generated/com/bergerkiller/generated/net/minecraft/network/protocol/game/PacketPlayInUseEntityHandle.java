package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.HumanEntity;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInUseEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInUseEntity")
public abstract class PacketPlayInUseEntityHandle extends PacketHandle {
    /** @see PacketPlayInUseEntityClass */
    public static final PacketPlayInUseEntityClass T = Template.Class.create(PacketPlayInUseEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInUseEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static boolean hasSecondaryActionField() {
        return T.hasSecondaryActionField.invoker.invoke(null);
    }

    public abstract boolean isInteract();
    public abstract boolean isInteractAt();
    public abstract boolean isAttack();
    public abstract HumanHand getInteractHand(HumanEntity humanEntity);
    public abstract Vector getInteractAtPosition();
    public abstract void setAttack();
    public abstract void setInteract(HumanEntity humanEntity, HumanHand hand);
    public abstract boolean isUsingSecondaryAction();
    public abstract void setUsingSecondaryAction(boolean using);
    public abstract int getUsedEntityId();
    public abstract void setUsedEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInUseEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInUseEntityClass extends Template.Class<PacketPlayInUseEntityHandle> {
        public final Template.Field.Integer usedEntityId = new Template.Field.Integer();

        public final Template.StaticMethod<Boolean> hasSecondaryActionField = new Template.StaticMethod<Boolean>();

        public final Template.Method<Boolean> isInteract = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isInteractAt = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isAttack = new Template.Method<Boolean>();
        public final Template.Method<HumanHand> getInteractHand = new Template.Method<HumanHand>();
        public final Template.Method.Converted<Vector> getInteractAtPosition = new Template.Method.Converted<Vector>();
        public final Template.Method<Void> setAttack = new Template.Method<Void>();
        public final Template.Method<Void> setInteract = new Template.Method<Void>();
        public final Template.Method<Boolean> isUsingSecondaryAction = new Template.Method<Boolean>();
        public final Template.Method<Void> setUsingSecondaryAction = new Template.Method<Void>();

    }

}

