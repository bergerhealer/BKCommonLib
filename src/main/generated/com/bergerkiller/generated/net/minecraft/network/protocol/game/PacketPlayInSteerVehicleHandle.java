package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInSteerVehicle</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInSteerVehicle")
public abstract class PacketPlayInSteerVehicleHandle extends PacketHandle {
    /** @see PacketPlayInSteerVehicleClass */
    public static final PacketPlayInSteerVehicleClass T = Template.Class.create(PacketPlayInSteerVehicleClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInSteerVehicleHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayInSteerVehicleHandle createNew(boolean isLeft, boolean isRight, boolean isForward, boolean isBackward, boolean isJump, boolean isUnmount, boolean isSprint) {
        return T.createNew.invokeVA(isLeft, isRight, isForward, isBackward, isJump, isUnmount, isSprint);
    }

    public abstract boolean isLeft();
    public abstract boolean isRight();
    public abstract boolean isForward();
    public abstract boolean isBackward();
    public abstract float getSideways();
    public abstract float getForwards();
    public abstract boolean isJump();
    public abstract boolean isUnmount();
    public abstract boolean isSprint();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInSteerVehicle</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInSteerVehicleClass extends Template.Class<PacketPlayInSteerVehicleHandle> {
        public final Template.StaticMethod.Converted<PacketPlayInSteerVehicleHandle> createNew = new Template.StaticMethod.Converted<PacketPlayInSteerVehicleHandle>();

        public final Template.Method<Boolean> isLeft = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isRight = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isForward = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isBackward = new Template.Method<Boolean>();
        public final Template.Method<Float> getSideways = new Template.Method<Float>();
        public final Template.Method<Float> getForwards = new Template.Method<Float>();
        public final Template.Method<Boolean> isJump = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isUnmount = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isSprint = new Template.Method<Boolean>();

    }

}

